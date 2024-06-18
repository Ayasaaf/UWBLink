package fr.eya.ranging.implementation

import android.util.Log.d
import android.util.Log.e
import android.util.Log.w
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

@Suppress("UNREACHABLE_CODE")
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
            if (oob.hasData()) oob.data else null
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
    d("processEndpointLost", "Processing endpoint lost event for endpointId: ${event.endpointId}")

    val endpoint = peerMap.remove(event.endpointId)

    return if (endpoint != null) {
        d("processEndpointLost", "Endpoint found and removed from peerMap: $endpoint")
        UwbOobEvent.UwbEndPointLost(endpoint)
    } else {
        w("processEndpointLost", "Endpoint not found in peerMap for endpointId: ${event.endpointId}")
        null
    }
}

    override fun start() = channelFlow {
        d("start", "Starting the channel flow")

        val events = prepareEventFlow()
        d("start", "Event flow prepared")

        val job = launch {
            d("start", "Launching coroutine to collect events")

            events.collect { event ->
                d("start", "Event collected: $event")

                when (event) {
                    is NearbyEvent.EndpointConnected -> {
                        d("start", "Processing EndpointConnected event for endpoint: ${event.endpointId}")

                        try {
                            d("start", "Attempting to process endpoint connection: ${event.endpointId}")
                            processEndpointConnected(event.endpointId)
                            d("start", "Successfully processed endpoint connection: ${event.endpointId}")
                        } catch (e: Exception) {
                            e("start", "Error processing endpoint connection: ${event.endpointId}", e)
                        }

                        null
                    }

                    is NearbyEvent.PayloadReceived -> {
                        d(
                            "start",
                            "Processing PayloadReceived event from endpoint: ${event.endpointId}"
                        )

                        processPayload(event)
                    }

                    is NearbyEvent.EndpointLost -> {
                        d(
                            "start",
                            "Processing EndpointLost event for endpoint: ${event.endpointId}"
                        )

                        processEndpointLost(event)
                    }
                    else -> {
                        d("start", "Unknown event type")
                        null
                    }
                }?.let {
                    d("start", "Sending result to channel: $it")
                    trySend(it)
                }
            }
        }

        d("start", "Awaiting close")

        awaitClose {
            d("start", "Closing and cancelling job")
            job.cancel()
        }
    }
    private suspend fun processPayload(event: NearbyEvent.PayloadReceived): UwbOobEvent? {
        d("processPayload", "Processing payload received event for endpointId: ${event.endpointId}")

        // Log raw payload for debugging
        d("processPayload", "Raw payload: ${event.payload.contentToString()}")

        tryParseUwbSessionInfo(event.payload)?.let {
            d("processPayload", "Parsed UwbSessionInfo successfully for endpointId: ${event.endpointId}")
            return processUwbSessionInfo(event.endpointId, it)
        }

        val endpoint = lookupEndpoint(event.endpointId)
        if (endpoint == null) {
            w("processPayload", "Endpoint not found for endpointId: ${event.endpointId}")
            return null
        }

        tryParseOobMessage(event.payload)?.let { parsedOobMessage ->
            d("processPayload", "Parsed OobMessage successfully for endpointId: ${event.endpointId}")
            return UwbOobEvent.MessageReceived(endpoint, parsedOobMessage.message.toByteArray())
        } ?: run {
            w("processPayload", "Failed to parse OobMessage for endpointId: ${event.endpointId}")
            // Log the event payload for debugging
            d("processPayload", "Event payload: ${event.payload.contentToString()}") // Log the raw payload
            return null // Or return a default value if appropriate
        }

        w("processPayload", "Failed to parse payload for endpointId: ${event.endpointId}")
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
