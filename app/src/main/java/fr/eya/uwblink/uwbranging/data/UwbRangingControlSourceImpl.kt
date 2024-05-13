package fr.eya.uwblink.uwbranging.data

import android.content.Context
import androidx.core.uwb.RangingParameters
import fr.eya.ranging.EndpointEvents
import fr.eya.ranging.UwbConnectionManager
import fr.eya.ranging.UwbEndPoint
import fr.eya.ranging.UwbSessionScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.SecureRandom
import kotlin.properties.Delegates

internal class UwbRangingControlSourceImpl(
    context: Context,
    endpointId: String,
    private val coroutineScope: CoroutineScope,
    private val uwbConnectionManager: UwbConnectionManager =
        UwbConnectionManager.getInstance(context),
) : UwbRangingControlSource {

    private var uwbEndpoint = UwbEndPoint(endpointId, SecureRandom.getSeed(8))

    private var uwbSessionScope: UwbSessionScope =
        getSessionScope(DeviceType.CONTROLLER, ConfigType.CONFIG_UNICAST_DS_TWR)

    private var rangingJob: Job? = null

    private val resultFlow = MutableSharedFlow<EndpointEvents>(
        replay = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
        extraBufferCapacity = 1
    )

    private val runningStateFlow = MutableStateFlow(false)

    override val isRunning = runningStateFlow.asStateFlow()

    private fun getSessionScope(deviceType: DeviceType, configType: ConfigType): UwbSessionScope {
        return when (deviceType) {
            DeviceType.CONTROLLEE -> uwbConnectionManager.controleeUwbScope(uwbEndpoint)
            DeviceType.CONTROLLER ->
                uwbConnectionManager.controllerUwbScope(uwbEndpoint, when (configType) {
                    ConfigType.CONFIG_UNICAST_DS_TWR -> RangingParameters.CONFIG_UNICAST_DS_TWR
                    ConfigType.CONFIG_MULTICAST_DS_TWR -> RangingParameters.CONFIG_MULTICAST_DS_TWR
                    else -> throw java.lang.IllegalStateException()
                })
            else -> throw IllegalStateException()
        }
    }

    override fun observeRangingResults(): Flow<EndpointEvents> {
        return resultFlow
    }

    override var deviceType: DeviceType by
    Delegates.observable(DeviceType.CONTROLLER) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            stop()
            uwbSessionScope = getSessionScope(newValue, configType)
        }
    }

    override var configType: ConfigType by
    Delegates.observable(ConfigType.CONFIG_UNICAST_DS_TWR) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            stop()
            uwbSessionScope = getSessionScope(deviceType, newValue)
        }
    }

    override fun updateEndpointId(id: String) {
        if (id != uwbEndpoint.id) {
            stop()
            uwbEndpoint = UwbEndPoint(id, SecureRandom.getSeed(8))
            uwbSessionScope = getSessionScope(deviceType, configType)
        }
    }

    override fun start() {
        if (rangingJob == null) {
            rangingJob =
                coroutineScope.launch {
                    uwbSessionScope.prepareSession().collect {
                        resultFlow.tryEmit(it)
                    }
                }
            runningStateFlow.update { true }
        }
    }

    override fun stop() {
        val job = rangingJob ?: return
        job.cancel()
        rangingJob = null
        runningStateFlow.update { false }
    }

    override fun sendOobMessage(endpoint: UwbEndPoint, message: ByteArray) {
        uwbSessionScope.sendMessage(endpoint, message)
    }
}
