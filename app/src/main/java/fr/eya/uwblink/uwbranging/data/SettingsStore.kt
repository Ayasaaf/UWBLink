package fr.eya.uwblink.uwbranging.data

import fr.eya.uwblink.uwbranging.data.AppSettings
import fr.eya.uwblink.uwbranging.data.ConfigType
import fr.eya.uwblink.uwbranging.data.DeviceType
import kotlinx.coroutines.flow.StateFlow

interface SettingsStore {

    val appSettings: StateFlow<AppSettings>

    fun updateDeviceType(deviceType: DeviceType)

    fun updateConfigType(configType: ConfigType)

    fun updateDeviceDisplayName(displayName: String)
}
