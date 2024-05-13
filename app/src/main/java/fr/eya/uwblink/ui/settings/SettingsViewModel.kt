package fr.eya.uwblink.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.eya.uwblink.uwbranging.data.ConfigType
import fr.eya.uwblink.uwbranging.data.DeviceType
import fr.eya.uwblink.uwbranging.data.SettingsStore
import fr.eya.uwblink.uwbranging.data.UwbRangingControlSource

class SettingsViewModel(
    private val uwbRangingControlSource: UwbRangingControlSource,
    private val settingsStore: SettingsStore,
) : ViewModel() {

    val uiState = settingsStore.appSettings

    fun updateDeviceDisplayName(deviceName: String) {
        if (deviceName == uiState.value.deviceDisplayName) {
            return
        }
        settingsStore.updateDeviceDisplayName(deviceName)
    }

    fun updateDeviceType(deviceType: DeviceType) {
        if (deviceType == uiState.value.deviceType) {
            return
        }
        uwbRangingControlSource.deviceType = deviceType
        settingsStore.updateDeviceType(deviceType)
    }

    fun updateConfigType(configType: ConfigType) {
        if (configType == uiState.value.configType) {
            return
        }
        uwbRangingControlSource.configType = configType
        settingsStore.updateConfigType(configType)
    }

    companion object {
        fun provideFactory(
            uwbRangingControlSource: UwbRangingControlSource,
            settingsStore: SettingsStore,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(uwbRangingControlSource, settingsStore) as T
                }
            }
    }
}
