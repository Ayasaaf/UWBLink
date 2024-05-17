package fr.eya.uwblink

import android.content.ContentResolver
import fr.eya.uwblink.uwbranging.data.SettingsStore
import fr.eya.uwblink.uwbranging.data.UwbRangingControlSource

interface AppContainer {
    val rangingResultSource: UwbRangingControlSource
    val settingsStore: SettingsStore
    val contentResolver: ContentResolver
}