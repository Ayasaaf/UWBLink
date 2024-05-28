package fr.eya.ranging.implementation

import android.util.Log
import com.google.protobuf.ByteString
import com.google.protobuf.InvalidProtocolBufferException
import fr.eya.ranging.UwbEndPoint
import fr.eya.ranging.implementation.proto.Control
import fr.eya.ranging.implementation.proto.Data
import fr.eya.ranging.implementation.proto.Oob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

internal abstract class NearbyConnector(protected val connections: NearByConnection) :
    OobConnector {
    private val peerMap = mutableMapOf<String, UwbEndPoint>()
    protected fun addEndPoint(endPointId: String, endPoint: UwbEndPoint) {
        peerMap[endPointId] = endPoint

    }

    private fun lookupEndpoint(endpointId: String): UwbEndPoint? {
        return peerMap[endpointId]
    }

    private fun lookupEndpointId(endpoint: UwbEndPoint): String? {
        return peerMap.firstNotNullOfOrNull {
            if (it.value == endpoint) it.key else null
        }
    }

    private fun tryParseOobMessage(payload: ByteArray): Data? {
        return try {
            val oob = Oob.parseFrom(payload)
            if (oob.hasControl()) oob.data else null
        } catch (_: InvalidProtocolBufferException) {
            null

        }
    }

    private fun tryParseUwbSessionInfo(payload: ByteArray): Control? {
        return try {
            val oob = Oob.parseFrom(payload)
            if (oob.hasControl()) oob.control else null
        } catch (_: InvalidProtocolBufferException) {
            null

        }
    }

    protected abstract fun prepareEventFlow(): Flow<NearbyEvent>
    protected abstract suspend fun processEndpointConnected(endpointId: String)

    protected abstract suspend fun processUwbSessionInfo(
        endpointId: String,
        sessionInfo: Control,
    ): UwbOobEvent.UwbEndPointFound?
// removes the disconnected device from a map of nearby devices, and creates a new event object indicating the lost endpoint.
    private fun processEndpointLost(event: NearbyEvent.EndpointLost): UwbOobEvent? {
        val endpoint = peerMap.remove(event.endpointId) ?: return null
        return UwbOobEvent.UwbEndPointLost(endpoint)
    }

    override fun start() = channelFlow {
        Log.d("start", "Starting the channel flow")

        val events = prepareEventFlow()
        Log.d("start", "Event flow prepared")

        val job = launch {
            Log.d("start", "Launching coroutine to collect events")

            events.collect { event ->
                Log.d("start", "Event collected: $event")

                when (event) {
                    is NearbyEvent.EndpointConnected -> {
                        Log.d("start", "Processing EndpointConnected event for endpoint: ${event.endpointId}")

                        try {
                            Log.d("start", "Attempting to process endpoint connection: ${event.endpointId}")
                            processEndpointConnected(event.endpointId)
                            Log.d("start", "Successfully processed endpoint connection: ${event.endpointId}")
                        } catch (e: Exception) {
                            Log.e("start", "Error processing endpoint connection: ${event.endpointId}", e)
                        }

                        null
                    }

                    is NearbyEvent.PayloadReceived -> {
                        Log.d(
                            "start",
                            "Processing PayloadReceived event from endpoint: ${event.endpointId}"
                        )

                        processPayload(event)
                    }

                    is NearbyEvent.EndpointLost -> {
                        Log.d(
                            "start",
                            "Processing EndpointLost event for endpoint: ${event.endpointId}"
                        )

                        processEndpointLost(event)
                    }
                    else -> {
                        Log.d("start", "Unknown event type")
                        null
                    }
                }?.let {
                    Log.d("start", "Sending result to channel: $it")
                    trySend(it)
                }
            }
        }

        Log.d("start", "Awaiting close")

        awaitClose {
            Log.d("start", "Closing and cancelling job")
            job.cancel()
        }
    }
    private suspend fun processPayload(event: NearbyEvent.PayloadReceived): UwbOobEvent? {

        tryParseUwbSessionInfo(event.payload)?.let {
            return processUwbSessionInfo(event.endpointId, it)
        }

        val endpoint = lookupEndpoint(event.endpointId) ?: return null

        tryParseOobMessage(event.payload)?.let {
            return UwbOobEvent.MessageReceived(endpoint, it.message.toByteArray())
        }
        return null
    }

    override fun sendMessage(endpoint: UwbEndPoint, message: ByteArray) {
        val endpointId = lookupEndpointId(endpoint) ?: return
        connections.sendPayload(
            endpointId,
            Oob.newBuilder()
                .setData(Data.newBuilder().setMessage(ByteString.copyFrom(message)).build())
                .build()
                .toByteArray()
        )
    }
}
