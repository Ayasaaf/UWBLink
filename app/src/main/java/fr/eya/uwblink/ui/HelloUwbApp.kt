package fr.eya.uwblink.ui

import OnboardingScreen
import SplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.eya.uwblink.AppContainer
import fr.eya.uwblink.ui.Bluetooth.BluetoothViewModel
import fr.eya.uwblink.ui.Bluetooth.componets.DeviceScreen
import fr.eya.uwblink.ui.nav.AppNavBar
import fr.eya.uwblink.ui.ranging.RangingViewModel
import fr.eya.uwblink.ui.theme.MyAppTheme
import fr.eya.uwblink.ui.welcomescreen.HelloScreen

@Composable
fun HelloUwbApp(appContainer: AppContainer) {
    val rangingViewModel: RangingViewModel =
        viewModel(factory = RangingViewModel.provideFactory(appContainer.rangingResultSource))
    val viewModel = hiltViewModel<BluetoothViewModel>()
    val state by viewModel.state.collectAsState()

    val navController = rememberNavController()
    MyAppTheme {
        NavHost(navController = navController, startDestination = "Splash_Screen") {
            composable("Splash_Screen") {
                SplashScreen(navController = navController, context = MainActivity())
            }
            composable("On_Boarding_Screen") {
                OnboardingScreen(navController = navController)
            }
            composable("Hello_Screen") {
                HelloScreen(navController = navController)
            }
            composable("MainScreen") {
                MainScreen( appContainer = appContainer)
            }
            composable("Device_Screen") {
                DeviceScreen(
                    state = state,
                    onStartScan = viewModel::StartScan,
                    onStopScan = viewModel::StopScan,
                    onDeviceClick = viewModel::connectToDevice,
                    onStartServer = viewModel::waitForIncomingConnections,
                    navController = navController,
                )
            }


        }

    }
}
@Composable
fun MainScreen( appContainer: AppContainer) {
    val rangingViewModel: RangingViewModel =
        viewModel(factory = RangingViewModel.provideFactory(appContainer.rangingResultSource))
    val uiState by rangingViewModel.uiState.collectAsState()

    AppNavBar(
        appContainer = appContainer,
        isRanging = uiState,

        startRanging = { rangingViewModel.startRanging() },

        stopRanging = { rangingViewModel.stopRanging() },
    )
}