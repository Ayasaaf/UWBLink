package fr.eya.uwblink.ui.nav

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.eya.uwblink.AppContainer
import fr.eya.uwblink.ui.Bluetooth.componets.BluetoothViewModel
import fr.eya.uwblink.ui.chat.ChatScreen
import fr.eya.uwblink.ui.control.ControlRoute
import fr.eya.uwblink.ui.control.ControlViewModel
import fr.eya.uwblink.ui.device.DeviceScreen
import fr.eya.uwblink.ui.home.HomeRoute
import fr.eya.uwblink.ui.home.HomeViewModel
import fr.eya.uwblink.ui.nav.AppDestination.CONTROL_ROUTE
import fr.eya.uwblink.ui.send.SendRoute
import fr.eya.uwblink.ui.send.SendViewModel
import fr.eya.uwblink.ui.settings.SettingsRoute
import fr.eya.uwblink.ui.settings.SettingsViewModel


@Composable
fun AppNavGraph(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppDestination.DEVICE_ROUTE,

    ) {
    val viewModel = hiltViewModel<BluetoothViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    var TAG = "AppNavGraph"
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {


        composable(AppDestination.HOME_ROUTE) {
            val homeViewModel: HomeViewModel =
                viewModel(
                    factory = HomeViewModel.provideFactory(
                        appContainer.rangingResultSource,

                        )
                )
            HomeRoute(homeViewModel = homeViewModel)
        }

        composable(AppDestination.DEVICE_ROUTE) {

            DeviceScreen(
                state = state,
                onStartScan = viewModel::StartScan,
                onStopScan = viewModel::StopScan,
                onDeviceClick = viewModel::connectToDevice,
                onStartServer = viewModel::waitForIncomingConnections,
                navController = navController,

                )
        }
        composable("Chat_Screen") {
            Log.d(TAG, "Chat_Screen: State - $state")
            when {
                state.isConnecting -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Text(text = "Connecting ...")
                    }
                }

                state.isConnected -> {
                    Log.d(TAG, "Chat_Screen: Connected")

                    ChatScreen(state = state,
                        OnDisconnect = viewModel::disconnectFromDevice,
                        OnSendMessage = viewModel::sendMessage,
                        navController = navController,
                       )
                }
                else -> {
                    Log.d(TAG, "Chat_Screen: Not Connecting or Connected")
                }
            }
        }


        composable(CONTROL_ROUTE) {
            val controlViewModel: ControlViewModel =
                viewModel(
                    factory = ControlViewModel.provideFactory(
                        appContainer.rangingResultSource,
                        appContainer.settingsStore
                    )
                )
            //ControlRoute()
            ControlRoute(controlViewModel = controlViewModel)

        }
        composable(AppDestination.SEND_ROUTE) {
            val sendViewModel: SendViewModel =
                viewModel(
                    factory =
                    SendViewModel.provideFactory(
                        appContainer.rangingResultSource,
                        appContainer.contentResolver
                    )
                )
            SendRoute(sendViewModel = sendViewModel)
        }
        composable(AppDestination.SETTINGS_ROUTE) {
            val settingsViewModel: SettingsViewModel =
                viewModel(
                    factory =
                    SettingsViewModel.provideFactory(
                        appContainer.rangingResultSource,
                        appContainer.settingsStore
                    )
                )
            SettingsRoute(settingsViewModel)
        }

    }
}



