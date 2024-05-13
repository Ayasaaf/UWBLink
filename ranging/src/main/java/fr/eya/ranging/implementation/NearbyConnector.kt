package fr.eya.ranging.implementation

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

internal abstract class NearbyConnector ( protected val connections : NearByConnection):
    OobConnector {
         private val peerMap = mutableMapOf<String , UwbEndPoint>()
    protected fun addEndPoint(endPointId : String , endPoint : UwbEndPoint ) {
        peerMap[endPointId]= endPoint

    }
    private fun lookupEndpoint(endpointId: String): UwbEndPoint? {
        return peerMap[endpointId]
    }
    private fun lookupEndpointId(endpoint:UwbEndPoint) : String? {
        return peerMap.firstNotNullOfOrNull {
            if (it.value == endpoint ) it.key else null
        }
    }

    private fun tryParseOobMessage (payload : ByteArray) : Data? {
        return try {
            val oob = Oob.parseFrom(payload)
            if (oob.hasControl()) oob.data else null
        }  catch (_: InvalidProtocolBufferException) {
            null

        }
    }
    private fun tryParseUwbSessionInfo (payload: ByteArray): Control? {
        return try {
            val oob = Oob.parseFrom(payload)
            if (oob.hasControl()) oob.control else null
        }  catch (_: InvalidProtocolBufferException) {
            null

    }
    }
protected abstract fun prepareEventFlow(): Flow<NearbyEvent>
    protected abstract suspend fun processEndpointConnected(endpointId: String)

    protected abstract suspend fun processUwbSessionInfo(
        endpointId: String,
        sessionInfo: Control,
    ): UwbOobEvent.UwbEndPointFound?

    private fun processEndpointLost(event: NearbyEvent.EndpointLost): UwbOobEvent? {
        val endpoint = peerMap.remove(event.endpointId) ?: return null
        return UwbOobEvent.UwbEndPointLost(endpoint)
    }
    override fun start() = channelFlow {
        val events = prepareEventFlow()
        val job = launch {
            events.collect { event ->
                when (event) {
                    is NearbyEvent.EndpointConnected -> {
                        processEndpointConnected(event.endpointId)
                        null
                    }
                    is NearbyEvent.PayloadReceived -> processPayload(event)
                    is NearbyEvent.EndpointLost -> processEndpointLost(event)
                    else -> null
                }?.let { trySend(it) }
            }
        }
        awaitClose { job.cancel() }
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
