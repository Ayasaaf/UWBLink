package fr.eya.ranging.implementation

import androidx.core.uwb.RangingParameters
import androidx.core.uwb.RangingResult
import androidx.core.uwb.UwbAddress
import androidx.core.uwb.UwbDevice
import fr.eya.ranging.EndpointEvents
import fr.eya.ranging.UwbEndPoint
import fr.eya.ranging.UwbSessionScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

internal class UwbSessionScopeImpl(
    private val localEndpoint: UwbEndPoint,
    private val connector: OobConnector,
) : UwbSessionScope {

    private val localAddresses = mutableSetOf<UwbAddress>()

    private val remoteDeviceMap = mutableMapOf<UwbAddress, UwbEndPoint>()

    private val activeJobs = mutableMapOf<UwbEndPoint, Job>()

    override fun prepareSession() = channelFlow {
        val job = launch {
            connector.start().collect { event ->
                when (event) {
                    is UwbOobEvent.UwbEndPointFound -> {
                        val rangingEvents = processEndpointFound(event)
                        activeJobs[event.endpoint] = launch { rangingEvents.collect { sendResult(it) } }
                    }
                    is UwbOobEvent.UwbEndPointLost -> processEndpointLost(event.endpoint)
                    is UwbOobEvent.MessageReceived ->
                        trySend(EndpointEvents.EndpointMessage(event.endpoint, event.message))
                }
            }
        }
        awaitClose {
            job.cancel()
            remoteDeviceMap.clear()
        }
    }

    override fun sendMessage(endpoint: UwbEndPoint, message: ByteArray) {
        connector.sendMessage(endpoint, message)
    }

    private fun ProducerScope<EndpointEvents>.processEndpointLost(endpoint: UwbEndPoint) {
        trySend(EndpointEvents.EndpointLost(endpoint))
        activeJobs[endpoint]?.cancel()
    }

    private fun ProducerScope<EndpointEvents>.processEndpointFound(
        event: UwbOobEvent.UwbEndPointFound,
    ): Flow<RangingResult> {
        remoteDeviceMap[event.endpointAddress] = event.endpoint
        localAddresses.add(event.sessionScope.localAddress)
        val rangingParameters =
            RangingParameters(
                event.configId,
                event.sessionId,
                event.sessionKeyInfo,
                event.complexChannel,
                listOf(UwbDevice(event.endpointAddress)),
                RangingParameters.RANGING_UPDATE_RATE_FREQUENT
            )
        trySend(EndpointEvents.EndpointFound(event.endpoint))
        return event.sessionScope.prepareSession(rangingParameters)
    }

    private fun ProducerScope<EndpointEvents>.sendResult(result: RangingResult) {
        val endpoint =
            if (localAddresses.contains(result.device.address)) localEndpoint
            else remoteDeviceMap[result.device.address] ?: return
        when (result) {
            is RangingResult.RangingResultPosition ->
                trySend(EndpointEvents.PositionUpdated(endpoint, result.position))
            is RangingResult.RangingResultPeerDisconnected ->
                trySend(EndpointEvents.UwbDisconnected(endpoint))
        }
    }
}