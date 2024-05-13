package fr.eya.uwblink.ui.nav

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object AppDestination {
    const val WAIT_SCREEN = "Wait_Screen"
    const val SPLASH_ROUTE = "Splash_Screen"
   const val ON_BORDING_ROUTE = "On_Boarding_Screen"
    const val Hello_ROUTE = "Hello_Screen"
    const val Choose_ROUTE = "Choose_Screen"
    const val DEVICE_ROUTE = "Device_Screen"
    const val CHAT_ROUTE = "Chat_Screen"
    const val HOME_ROUTE = "home"
    const val SEND_ROUTE = "send"
    const val SETTINGS_ROUTE = "settings"
    const val CONTROL_ROUTE = "control"

}

class AppNavigation(private val navController: NavHostController) {

    val navToHello: () -> Unit = { navTo(AppDestination.Hello_ROUTE) }
    val navToonboarding: () -> Unit = { navTo(AppDestination.ON_BORDING_ROUTE) }
    val navTodevicescreen: () -> Unit = { navTo(AppDestination.DEVICE_ROUTE) }
    val navTochatscreen: () -> Unit = { navTo(AppDestination.CHAT_ROUTE) }
    val navToHome: () -> Unit = { navTo(AppDestination.HOME_ROUTE) }
    val navToSend: () -> Unit = { navTo(AppDestination.SEND_ROUTE) }
    val navToSettings: () -> Unit = { navTo(AppDestination.SETTINGS_ROUTE) }
    val navToControl: () -> Unit = { navTo(AppDestination.CONTROL_ROUTE) }

    fun navTo(destination: String) {
        navController.navigate(destination) {
            popUpTo(navController.graph.findStartDestination().id){
                saveState= true }
                launchSingleTop = true
            restoreState = true
            }

        }
    }
