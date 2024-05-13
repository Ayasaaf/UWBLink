package fr.eya.uwblink

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import fr.eya.uwblink.ui.Bluetooth.BluetoothViewModel
import fr.eya.uwblink.uwbranging.BluetoothChat.data.chat.AndroidBluetoothController
import fr.eya.uwblink.uwbranging.data.AppContainerImpl

@HiltAndroidApp
class HelloUwbApplication : Application() {

    lateinit var container: AppContainer

    fun initContainer(afterLoading: () -> Unit ) {
        val bluetoothController = AndroidBluetoothController(applicationContext)
        container = AppContainerImpl(applicationContext, afterLoading , viewModel = BluetoothViewModel(bluetoothController) )
    }
}
