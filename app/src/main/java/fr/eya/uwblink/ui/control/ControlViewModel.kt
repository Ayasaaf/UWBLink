package fr.eya.uwblink.ui.control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.eya.ranging.EndpointEvents
import fr.eya.uwblink.uwbranging.data.DeviceType
import fr.eya.uwblink.uwbranging.data.SettingsStore
import fr.eya.uwblink.uwbranging.data.UwbRangingControlSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val LOCK_DISTANCE = 2.0f
private const val UNLOCK_DISTANCE = 0.25f

class ControlViewModel(
    private val uwbRangingControlSource: UwbRangingControlSource,
    settingsStore: SettingsStore
) : ViewModel() {

    private val _uiState: MutableStateFlow<ControlUiState> =
        MutableStateFlow(ControlUiState.KeyState)

    val uiState = _uiState.asStateFlow()

    private var lockJob: Job? = null

    private fun startLockObserving(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            launch {
                uwbRangingControlSource
                    .observeRangingResults()
                    .filterIsInstance<EndpointEvents.PositionUpdated>()
                    .collect {
                        it.position.distance?.let {
                            val state = _uiState.value as ControlUiState.LockState
                            if (!state.isLocked && it.value > LOCK_DISTANCE) {
                                _uiState.update { ControlUiState.LockState(isLocked = true) }
                            }
                            if (state.isLocked && it.value < UNLOCK_DISTANCE) {
                                _uiState.update { ControlUiState.LockState(isLocked = false) }
                            }
                        }
                    }
            }
            launch {
                uwbRangingControlSource.isRunning.collect {
                    val state = _uiState.value as ControlUiState.LockState
                    if (!state.isLocked && !it) {
                        _uiState.update { ControlUiState.LockState(isLocked = true) }
                    }
                }
            }
        }
    }

    init {
        settingsStore.appSettings
            .onEach {
                lockJob?.cancel()
                lockJob = null
                when (it.deviceType) {
                    DeviceType.CONTROLLEE -> _uiState.update { ControlUiState.KeyState }
                    DeviceType.CONTROLLER -> {
                        if (_uiState.value !is ControlUiState.LockState) {
                            _uiState.update { ControlUiState.LockState(isLocked = true) }
                            lockJob = startLockObserving()
                        }
                    }
                    else -> {}
                }
            }
            .launchIn(viewModelScope)
    }

    companion object {
        fun provideFactory(
            uwbRangingControlSource: UwbRangingControlSource,
            settingsStore: SettingsStore
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ControlViewModel(uwbRangingControlSource, settingsStore) as T
                }
            }
    }
}

sealed class ControlUiState {

    data class LockState(val isLocked: Boolean) : ControlUiState()

    object KeyState : ControlUiState()
}