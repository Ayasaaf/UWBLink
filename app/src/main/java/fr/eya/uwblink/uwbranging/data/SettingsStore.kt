package fr.eya.uwblink.uwbranging.data


import kotlinx.coroutines.flow.StateFlow

interface SettingsStore {

    val appSettings: StateFlow<AppSettings>

    fun updateDeviceType(deviceType: DeviceType)

    fun updateConfigType(configType: ConfigType)

    fun updateDeviceDisplayName(displayName: String)
}
