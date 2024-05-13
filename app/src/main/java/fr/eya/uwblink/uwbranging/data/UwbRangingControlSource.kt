package fr.eya.uwblink.uwbranging.data


import fr.eya.ranging.EndpointEvents
import fr.eya.ranging.UwbEndPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UwbRangingControlSource {

    fun observeRangingResults(): Flow<EndpointEvents>

    var deviceType: DeviceType

    var configType: ConfigType

    fun updateEndpointId(id: String)

    fun start()

    fun stop()

    fun sendOobMessage(endpoint: UwbEndPoint, message: ByteArray)

    val isRunning: StateFlow<Boolean>
}
