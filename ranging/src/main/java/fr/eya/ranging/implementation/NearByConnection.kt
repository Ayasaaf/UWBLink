package fr.eya.ranging.implementation

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import fr.eya.ranging.UwbEndPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val CONNECTION_SERVICE_ID = "0x0000002A"
private const val CONNECTION_NAME = "0x0000002A"

internal class NearByConnection(
    context: Context ,
    dispatcher: CoroutineDispatcher,
    private val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(context),
) {
    private val coroutineScope =
        CoroutineScope(
            dispatcher +
                    Job() +
                    CoroutineExceptionHandler { _, e ->
                        Log.e(
                            "NearbyConnections",
                            "Connection Error",
                            e
                        )
                    }
        )

    //connection Callbacks for controller and Controllee
    private val connectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endPointId: String, connectionInfo: ConnectionInfo) {
                Nearby.getConnectionsClient(context).stopAdvertising()
                Nearby.getConnectionsClient(context).stopDiscovery()
                val endpoint = UwbEndPoint(endPointId, connectionInfo.endpointInfo)
                Log.d("NearbyConnections", "onConnectionInitiated: $endPointId $connectionInfo")
                coroutineScope.launch {
                    Nearby.getConnectionsClient(context)
                        .acceptConnection(endPointId, payloadCallback).addOnSuccessListener { }
                    Log.d("NearbyConnections", "Connection accepted: $endPointId")

                }
            }

            override fun onConnectionResult(endPointId: String, result: ConnectionResolution) {
                Log.d(
                    "NearbyConnections",
                    "onConnectionResult: $endPointId, ${result.status.statusCode}"
                )


                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        Nearby.getConnectionsClient(context).stopAdvertising()
                        Nearby.getConnectionsClient(context).stopDiscovery()
                        Log.d(
                            "NearbyConnection",
                            "Connection established with $endPointId"
                        ) // Log for debugging (optional)
                    }

                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        Log.w(
                            "NearbyConnection",
                            "Connection rejected by $endPointId"
                        ) // Log for debugging (optional)
                        // Handle connection rejected scenario (optional)
                    }

                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        Log.e(
                            "NearbyConnection",
                            "Connection error with $endPointId",
                        ) // Log error details (optional)
                        // Handle connection error scenario (optional)
                    }
                }
                if (result.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
                    dispatchEvent(NearbyEvent.EndpointConnected(endPointId))
                    Log.d("connected" , "endpointconnected $endPointId")
                }
            }


            override fun onDisconnected(endPointId: String) {
                Log.d("NearbyConnections", "onDisconnected: $endPointId")

                dispatchEvent(NearbyEvent.EndpointLost(endPointId))
            }
        }
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            // Log received payload details
            Log.d("NearbyConnection", "onPayloadReceived: endpointId=$endpointId, payload=$payload")

            // Retrieve connection using endpoint ID and call onReceive
            val bytes = payload.asBytes()
            if (bytes == null) {
                Log.d("onPayloadReceived", "Payload is null, returning")
                return
            }
            Log.d("onPayloadReceived", "Payload size: ${bytes.size} bytes")
            dispatchEvent(NearbyEvent.PayloadReceived(endpointId, bytes))
        }


        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Log payload transfer update details
            Log.d(
                "NearbyConnection",
                "onPayloadTransferUpdate: endpointId=$endpointId, update=$update"
            )
        }
    }

    private val endpointDiscoveryCallback =
        object : EndpointDiscoveryCallback() {

            override fun onEndpointFound(endPointId: String, info: DiscoveredEndpointInfo) {
                val endpoint = UwbEndPoint(endPointId, info.endpointInfo)
                Nearby.getConnectionsClient(context)
                    .requestConnection(CONNECTION_NAME, endpoint.id, connectionLifecycleCallback)
                    .addOnSuccessListener { }
                Log.d("NearbyConnections", "onEndpointFound: $endPointId")
            }

            override fun onEndpointLost(endPointId: String) {
                dispatchEvent(NearbyEvent.EndpointLost(endPointId))
            }


        }


    fun sendPayload(endpointId: String, bytes: ByteArray) {


        // Log sending attempt with endpoint ID and payload size
        Log.d(
            "NearbyConnection",
            "Sending payload to endpoint: $endpointId, size: ${bytes.size} bytes"
        )

        coroutineScope.launch {
            connectionsClient.sendPayload(endpointId, Payload.fromBytes(bytes)).await()
        }

        // Log successful sending
        Log.d("NearbyConnection", "Payload sent successfully to endpoint: $endpointId")


    }

    private var dispatchEvent: (event: NearbyEvent) -> Unit = {}

    fun startDiscovery() = callbackFlow {
        Log.d("NearbyConnections", "startDiscovery")

        dispatchEvent = { trySend(it) }
        coroutineScope.launch {
            connectionsClient.startDiscovery(
                CONNECTION_SERVICE_ID,
                endpointDiscoveryCallback,
                DiscoveryOptions.Builder().setStrategy(
                    Strategy.P2P_CLUSTER
                ).build()
            ).await()
        }
        awaitClose {
            disconnectAll()
            connectionsClient.stopDiscovery()
        }
    }

    fun startAdvertising() = callbackFlow {
        Log.d("NearbyConnections", "startAdvertising")

        dispatchEvent = {
            trySend(it)
        }
        coroutineScope.launch {
            connectionsClient
                .startAdvertising(
                    CONNECTION_NAME,
                    CONNECTION_SERVICE_ID,
                    connectionLifecycleCallback,
                    AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
                ).await()
        }
        awaitClose {
            disconnectAll()
            connectionsClient.stopAdvertising()
        }
    }


    private fun disconnectAll() {
        connectionsClient.stopAllEndpoints()
    }
}

/** Events that happen in a Nearby Connections session. */
abstract class NearbyEvent private constructor() {

    abstract val endpointId: String

    /** An event that notifies a NC endpoint is connected. */
    data class EndpointConnected(override val endpointId: String) : NearbyEvent()

    /** An event that notifies a NC endpoint is lost. */
    data class EndpointLost(override val endpointId: String) : NearbyEvent()

    /** An event that notifies a UWB device is lost. */
    data class PayloadReceived(override val endpointId: String, val payload: ByteArray) :
        NearbyEvent()

}