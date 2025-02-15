package fr.eya.uwblink.ui.Bluetooth

import fr.eya.uwblink.uwbranging.BluetoothChat.domain.BluetoothDevice
import fr.eya.uwblink.uwbranging.BluetoothChat.domain.BluetoothMessage

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val ErrorMessage: String? = null,
    val messages : List<BluetoothMessage> = emptyList()
)