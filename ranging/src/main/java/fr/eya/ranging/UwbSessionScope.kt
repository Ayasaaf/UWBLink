package fr.eya.ranging

import kotlinx.coroutines.flow.Flow

interface UwbSessionScope {
    fun prepareSession(): Flow<EndpointEvents>

    fun sendMessage(endpoint: UwbEndPoint, message: ByteArray)
}