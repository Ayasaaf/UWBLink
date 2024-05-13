package fr.eya.ranging.implementation

import fr.eya.ranging.UwbEndPoint
import kotlinx.coroutines.flow.Flow

internal interface OobConnector {
    fun start(): Flow<UwbOobEvent>
    fun sendMessage(endpoint: UwbEndPoint, message: ByteArray)
}