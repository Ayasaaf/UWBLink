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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val CONNECTION_SERVICE_ID = "UWBLink"
private const val CONNECTION_NAME = "UWBLink"

internal class NearByConnection(
    context: Context,
    private val dispatcher: CoroutineDispatcher,
    private val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(context)
) {
    private val coroutineScope =
        CoroutineScope(dispatcher + Job() + CoroutineExceptionHandler { _, e ->
            Log.e(
                "NearbyConnections",
                "Connection Error",
                e
            )
        })

    //connection Callbacks for controller and Controllee
    private val connectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endPointId: String, connectionInfo: ConnectionInfo) {
                coroutineScope.launch {
                    connectionsClient.acceptConnection(endPointId, payloadCallback).await()
                }
            }

            override fun onConnectionResult(endPointId: String, result: ConnectionResolution) {
                if (result.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
                    dispatchEvent(NearbyEvent.EndpointLost(endPointId))
                }
            }

            override fun onDisconnected(endpointId: String) {
                dispatchEvent(NearbyEvent.EndpointLost(endpointId))
            }
        }
    private val payloadCallback =
        object : PayloadCallback() {
            override fun onPayloadReceived(endpointId: String, payload: Payload) {
                val bytes = payload.asBytes() ?: return
                dispatchEvent(NearbyEvent.PayloadReceived(endpointId, bytes))
            }

            override fun onPayloadTransferUpdate(
                endpointId: String,
                update: PayloadTransferUpdate
            ) {
            }
        }
    private val endpointDiscoveryCallback =
        object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endPointId: String, info: DiscoveredEndpointInfo) {
                coroutineScope.launch {
                    connectionsClient.requestConnection(
                        CONNECTION_NAME,
                        endPointId,
                        connectionLifecycleCallback
                    )
                }
            }

            override fun onEndpointLost(endPointId: String) {
                dispatchEvent(NearbyEvent.EndpointLost(endPointId))
            }


        }

    fun sendPayload(endpointId: String, bytes: ByteArray) {
        coroutineScope.launch {
            connectionsClient.sendPayload(endpointId, Payload.fromBytes(bytes)).await()
        }
    }

    private var dispatchEvent: (event: NearbyEvent) -> Unit = {}

    fun startDiscovery() = callbackFlow {
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