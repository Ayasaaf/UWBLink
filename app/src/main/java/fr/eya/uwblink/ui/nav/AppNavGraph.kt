package fr.eya.uwblink.ui.nav

import OnboardingScreen
import SplashScreen
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.eya.uwblink.AppContainer
import fr.eya.uwblink.BluetoothChat.BluetoothViewModel
import fr.eya.uwblink.storage.DataStorageScreen
import fr.eya.uwblink.ui.MainActivity
import fr.eya.uwblink.ui.chat.ChatScreen
import fr.eya.uwblink.ui.control.ControlRoute
import fr.eya.uwblink.ui.control.ControlViewModel
import fr.eya.uwblink.ui.device.DeviceScreen
import fr.eya.uwblink.ui.home.HomeRoute
import fr.eya.uwblink.ui.home.HomeViewModel
import fr.eya.uwblink.ui.nav.AppDestination.CHAT_ROUTE
import fr.eya.uwblink.ui.nav.AppDestination.CONTROL_ROUTE
import fr.eya.uwblink.ui.nav.AppDestination.DEVICE_ROUTE
import fr.eya.uwblink.ui.send.SendRoute
import fr.eya.uwblink.ui.send.SendViewModel
import fr.eya.uwblink.ui.settings.SettingsRoute
import fr.eya.uwblink.ui.settings.SettingsViewModel
import fr.eya.uwblink.ui.welcomescreen.HelloScreen
import fr.eya.uwblink.ui.welcomescreen.MainScreen


@Composable
fun AppNavGraph(
    context: Context,
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppDestination.SPLASH_ROUTE,
) {

    val viewModel = hiltViewModel<BluetoothViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(AppDestination.SPLASH_ROUTE) {
            SplashScreen(navController = navController, MainActivity)
        }
        composable(AppDestination.ON_BORDING_ROUTE) {
            OnboardingScreen(navController = navController)
        }
        composable(AppDestination.HOME_ROUTE) {

            val homeViewModel: HomeViewModel =
                viewModel(
                    factory = HomeViewModel.provideFactory(
                        LocalContext.current,
                        appContainer.rangingResultSource,
                        // Pass the context here
                    )
                )
            HomeRoute(homeViewModel = homeViewModel)
        }
        composable(AppDestination.Main_Route) {
            MainScreen(appContainer = appContainer)
        }
        composable(AppDestination.Store_Route) {
            val homeViewModel: HomeViewModel =
                viewModel(
                    factory = HomeViewModel.provideFactory(
                        LocalContext.current,
                        appContainer.rangingResultSource,
                    )
                )
            DataStorageScreen(viewModel = homeViewModel , context )
        }
        composable(DEVICE_ROUTE) {

            DeviceScreen(
                state = state,
                onStartScan = viewModel::StartScan,
                onStopScan = viewModel::StopScan,
                onDeviceClick = viewModel::connectToDevice,
                onStartServer = viewModel::waitForIncomingConnections,
                navController = navController,

                )
        }
        composable(CHAT_ROUTE) {
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

                    ChatScreen(
                        state = state,
                        OnDisconnect = viewModel::disconnectFromDevice,
                        OnSendMessage = viewModel::sendMessage,
                        navController = navController,
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "You need to pair a device to chat ")
                    }
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
        composable(AppDestination.Choose_ROUTE)
        {
            HelloScreen(navController = navController)
        }
        composable(AppDestination.SEND_ROUTE) {
            val sendViewModel: SendViewModel =
                viewModel(
                    factory =
                    SendViewModel.provideFactory(
                        appContainer.rangingResultSource,
                        appContainer.contentResolver , appContext = context
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



