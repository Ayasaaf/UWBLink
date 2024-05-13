package fr.eya.ranging.implementation

import androidx.core.uwb.RangingParameters
import androidx.core.uwb.UwbAddress
import androidx.core.uwb.UwbClientSessionScope
import androidx.core.uwb.UwbComplexChannel
import androidx.core.uwb.UwbControleeSessionScope
import com.google.common.primitives.Shorts
import com.google.protobuf.ByteString
import fr.eya.ranging.UwbEndPoint
import fr.eya.ranging.implementation.proto.Control
import fr.eya.ranging.implementation.proto.Oob
import fr.eya.ranging.implementation.proto.UwbCapabilities
import fr.eya.ranging.implementation.proto.UwbConnectionInfo
import kotlinx.coroutines.flow.Flow

internal class NearByControleeConnector(
    private val localEndPoint: UwbEndPoint,
    connections: NearByConnection,
    private val sessionScopeCreator: suspend () -> UwbControleeSessionScope,
) : NearbyConnector(connections) {
    private val sessionMap = mutableMapOf<String, UwbClientSessionScope>()
    override fun prepareEventFlow(): Flow<NearbyEvent> {
        return connections.startAdvertising()
    }

    override suspend fun processEndpointConnected(endpointId: String) {
        val sessionScope = sessionScopeCreator()
        sessionMap[endpointId] = sessionScope
        connections.sendPayload(
            endpointId,
            Oob.newBuilder().setControl(uwbSessionInfo(sessionScope)).build().toByteArray()
        )
    }

    override suspend fun processUwbSessionInfo(
        endpointId: String,
        sessionInfo: Control,
    ): UwbOobEvent.UwbEndPointFound? {
        val configuration =
            if (sessionInfo.connectionInfo.hasConfiguration()) sessionInfo.connectionInfo.configuration
            else return null
        val sessionScope = sessionMap[endpointId] ?: return null
        val endpoint = UwbEndPoint(sessionInfo.id, sessionInfo.metadata.toByteArray())
        addEndPoint(endpointId, endpoint)

        return UwbOobEvent.UwbEndPointFound(
            endpoint,
            configuration.configId,
            UwbAddress(Shorts.toByteArray(sessionInfo.localAddress.toShort())),
            UwbComplexChannel(configuration.channel, configuration.preambleIndex),
            configuration.sessionId,
            configuration.securityInfo.toByteArray(),
            sessionScope
        )
    }

    private fun uwbSessionInfo(scope: UwbClientSessionScope) =
        Control.newBuilder()
            .setId(localEndPoint.id)
            .setMetadata(ByteString.copyFrom(localEndPoint.metadata))
            .setLocalAddress(Shorts.fromByteArray(scope.localAddress.address).toInt())
            .setConnectionInfo(
                UwbConnectionInfo.newBuilder()
                    .setCapabilities(
                        UwbCapabilities.newBuilder()
                            .addAllSupportedConfigIds(
                                listOf(
                                    RangingParameters.CONFIG_UNICAST_DS_TWR,
                                    RangingParameters.CONFIG_MULTICAST_DS_TWR
                                )
                            )
                            .setSupportsAzimuth(scope.rangingCapabilities.isAzimuthalAngleSupported)
                            .setSupportsElevation(scope.rangingCapabilities.isElevationAngleSupported)
                            .build()
                    )
                    .build()
            )
            .build()
}
