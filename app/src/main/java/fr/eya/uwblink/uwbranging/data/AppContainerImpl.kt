package fr.eya.uwblink.uwbranging.data

import android.content.ContentResolver
import android.content.Context
import fr.eya.uwblink.AppContainer
import fr.eya.uwblink.ui.Bluetooth.BluetoothViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class AppContainerImpl(
    private val context: Context,
    afterLoading: () -> Unit, override val viewModel: BluetoothViewModel,
) : AppContainer {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    override val rangingResultSource: UwbRangingControlSource
        get() =
            _rangingResultSource
                ?: throw IllegalStateException("rangingResultSource only can be accessed after loading.")

    private var _rangingResultSource: UwbRangingControlSource? = null

    override val settingsStore = SettingsStoreImpl(context, coroutineScope)

    override val contentResolver: ContentResolver = context.contentResolver

    init {
        coroutineScope.launch {
            settingsStore.appSettings.collect {
                val endpointId = it.deviceDisplayName + "|" + it.deviceUuid
                if (_rangingResultSource == null) {
                    _rangingResultSource =
                        UwbRangingControlSourceImpl(context, endpointId, coroutineScope)
                    afterLoading()
                } else {
                    rangingResultSource.deviceType = it.deviceType
                    rangingResultSource.updateEndpointId(endpointId)
                }
            }
        }
    }
}
