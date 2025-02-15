package fr.eya.uwblink.ui.Bluetooth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.eya.uwblink.uwbranging.BluetoothChat.domain.BluetoothDeviceDomain
import fr.eya.uwblink.uwbranging.BluetoothChat.domain.chat.BluetoothController
import fr.eya.uwblink.uwbranging.BluetoothChat.domain.chat.ConnectionResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
) : ViewModel() {
    val TAG = "BluetoothViewModel"
    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        bluetoothController.ScannedDevices,
        bluetoothController.PairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,
            messages = if (state.isConnected) state.messages else emptyList()
        )

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private var deviceConnectionJob: Job? = null

    init {
        bluetoothController.isConnected.onEach { isConnected ->
            _state.update {
                Log.i(TAG, "bluetoothController.isConnected.onEach")
                it.copy(isConnected = isConnected) }
        }.launchIn(viewModelScope)
        bluetoothController.errors.onEach { error ->
            _state.update {
                Log.e(TAG, "bluetoothController.isConnected.onEach = $error")
                it.copy(ErrorMessage = error)
            }
        }.launchIn(viewModelScope)
    }

    fun connectToDevice(device: BluetoothDeviceDomain) {
        Log.d(TAG, "connectToDevice: ")
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController
            .ConnectToDevice(device)
            .listen()
    }

    fun disconnectFromDevice() {
        Log.d(TAG, "disconnectFromDevice: ")
        deviceConnectionJob?.cancel()
        bluetoothController.CloseConnection()
        _state.update {
            it.copy(
                isConnecting = false,
                isConnected = false
            )
        }
    }

    fun waitForIncomingConnections() {
        Log.d(TAG, "waitForIncomingConnections: ")
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController
            .StartBluetoothServer()
            .listen()
    }

    fun sendMessage(message: String) {
        Log.d(TAG, "sendMessage: ")
        viewModelScope.launch {
            val bluetoothMessage = bluetoothController.trySendMessage(message)
            if (bluetoothMessage != null) {
                _state.update {
                    it.copy(
                        messages = it.messages + bluetoothMessage
                    )
                }
            }
        }
    }

    fun StartScan() {
        Log.d(TAG, "StartScan: ")
        bluetoothController.startDiscovery()
    }

    fun StopScan() {
        Log.d(TAG, "StopScan: ")
        bluetoothController.Stop()
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        Log.d(TAG, "listen: ")
        return onEach { result ->
       when (result) {
                ConnectionResult.ConnectionEstablished -> {
                    Log.d(TAG, "listen: update state")
                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            ErrorMessage = null
                        )
                    }
                }

                is ConnectionResult.transfersucceded -> {
                    _state.update {
                        it.copy(
                            messages = it.messages + result.message
                        )
                    }
                }


                is ConnectionResult.Error -> {
                    Log.e(TAG, "listen: "+result.message )
                    _state.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                            ErrorMessage = result.message
                        )
                    }
                }
            }
        }
            .catch { throwable ->
                Log.e(TAG, "listen: ",throwable )
                bluetoothController.CloseConnection()
                _state.update {
                    it.copy(
                        isConnected = false,
                        isConnecting = false,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.Release()
    }



}

