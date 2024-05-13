package fr.eya.ranging

import androidx.core.uwb.RangingPosition

abstract class EndpointEvents internal constructor() {
    /** Represents a UWB endpoint. */
    abstract val endpoint: UwbEndPoint

    /**
     * Device position update.
     *
     * @property position Position of the UWB device during Ranging
     */
    data class PositionUpdated(override val endpoint: UwbEndPoint, val position: RangingPosition) :
        EndpointEvents()

    /** A ranging result with peer disconnected status update. */
    data class UwbDisconnected(override val endpoint: UwbEndPoint) : EndpointEvents()

    /** Endpoint is found. */
    data class EndpointFound(override val endpoint: UwbEndPoint) : EndpointEvents()

    /** Endpoint is lost. */
    data class EndpointLost(override val endpoint: UwbEndPoint) : EndpointEvents()

    /** Received message through OOB. */
    data class EndpointMessage(override val endpoint: UwbEndPoint, val message: ByteArray) :
        EndpointEvents()
}
