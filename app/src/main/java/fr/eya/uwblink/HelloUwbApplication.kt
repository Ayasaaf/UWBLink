package fr.eya.uwblink

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import fr.eya.uwblink.uwbranging.data.AppContainerImpl

@HiltAndroidApp
class HelloUwbApplication : Application() {

    lateinit var container: AppContainer



    fun initContainer(afterLoading: () -> Unit ) {
        container = AppContainerImpl(applicationContext, afterLoading )
    }

}
