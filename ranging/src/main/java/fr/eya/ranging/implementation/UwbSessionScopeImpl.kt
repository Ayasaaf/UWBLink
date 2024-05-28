package fr.eya.ranging.implementation

import android.util.Log
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
        Log.d("processEndpointFound", "Created rangingParameters: $rangingParameters")

        trySend(EndpointEvents.EndpointFound(event.endpoint))
        Log.d("processEndpointFound", "Sent EndpointFound event for: ${event.endpoint}")

        return event.sessionScope.prepareSession(rangingParameters).also {
            Log.d("processEndpointFound", "Preparing session with rangingParameters")
        }
    }


    private fun ProducerScope<EndpointEvents>.sendResult(result: RangingResult) {
        Log.d("sendResult", "Processing ranging result: $result")

        val endpoint =
            if (localAddresses.contains(result.device.address)) {
                Log.d("sendResult", "Result device is local endpoint: ${result.device.address}")
                localEndpoint
            } else {
                remoteDeviceMap[result.device.address]?.also {
                    Log.d(
                        "sendResult",
                        "Result device found in remoteDeviceMap: ${result.device.address}"
                    )
                } ?: run {
                    Log.d(
                        "sendResult",
                        "Result device not found in remoteDeviceMap, ignoring: ${result.device.address}"
                    )
                    return
                }
            }

        when (result) {
            is RangingResult.RangingResultPosition -> {
                Log.d(
                    "sendResult",
                    "RangingResultPosition: ${result.position} for endpoint: $endpoint"
                )
                trySend(EndpointEvents.PositionUpdated(endpoint, result.position))
            }

            is RangingResult.RangingResultPeerDisconnected -> {
                Log.d("sendResult", "RangingResultPeerDisconnected for endpoint: $endpoint")
                trySend(EndpointEvents.UwbDisconnected(endpoint))
            }
        }
    }}