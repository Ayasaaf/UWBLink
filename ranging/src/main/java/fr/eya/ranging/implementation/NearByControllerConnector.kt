package fr.eya.ranging.implementation

import androidx.core.uwb.UwbAddress
import androidx.core.uwb.UwbControllerSessionScope
import com.google.common.primitives.Shorts
import com.google.protobuf.ByteString
import fr.eya.ranging.UwbEndPoint
import fr.eya.ranging.implementation.proto.Control
import fr.eya.ranging.implementation.proto.Oob
import fr.eya.ranging.implementation.proto.UwbConfiguration
import fr.eya.ranging.implementation.proto.UwbConnectionInfo
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

internal class NearByControllerConnector(
    private val localEndpoint: UwbEndPoint,
    private val configId: Int,
    connections: NearByConnection,
    private val sessionScopeCreator: suspend () -> UwbControllerSessionScope,
) : NearbyConnector(connections) {

    override fun prepareEventFlow(): Flow<NearbyEvent> {
        return connections.startDiscovery()
    }

    override suspend fun processEndpointConnected(endpointId: String) {}

    override suspend fun processUwbSessionInfo(
        endpointId: String,
        sessionInfo: Control,
    ): UwbOobEvent.UwbEndPointFound? {

        val capabilities =
            if (sessionInfo.connectionInfo.hasCapabilities()) sessionInfo.connectionInfo.capabilities
            else return null
        if (!capabilities.supportedConfigIdsList.contains(configId)) {
            return null
        }
        val endpoint = UwbEndPoint(sessionInfo.id, sessionInfo.metadata.toByteArray())
        addEndPoint(endpointId, endpoint)
        val sessionScope = sessionScopeCreator()
        val sessionId = Random.nextInt()
        val sessionKeyInfo = Random.nextBytes(8)
        val endpointAddress = UwbAddress(Shorts.toByteArray(sessionInfo.localAddress.toShort()))
        val localAddress = sessionScope.localAddress
        val complexChannel = sessionScope.uwbComplexChannel
        val endpointFoundEvent =
            UwbOobEvent.UwbEndPointFound(
                endpoint,
                configId,
                endpointAddress,
                complexChannel,
                sessionId,
                sessionKeyInfo,
                sessionScope
            )

        connections.sendPayload(
            endpointId,
            Oob.newBuilder()
                .setControl(
                    Control.newBuilder()
                        .setId(localEndpoint.id)
                        .setMetadata(ByteString.copyFrom(localEndpoint.metadata))
                        .setLocalAddress(Shorts.fromByteArray(localAddress.address).toInt())
                        .setConnectionInfo(
                            UwbConnectionInfo.newBuilder()
                                .setConfiguration(
                                    UwbConfiguration.newBuilder()
                                        .setConfigId(configId)
                                        .setSessionId(sessionId)
                                        .setChannel(complexChannel.channel)
                                        .setPreambleIndex(complexChannel.preambleIndex)
                                        .setSecurityInfo(ByteString.copyFrom(sessionKeyInfo))
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build()
                .toByteArray()
        )
        return endpointFoundEvent
    }
}