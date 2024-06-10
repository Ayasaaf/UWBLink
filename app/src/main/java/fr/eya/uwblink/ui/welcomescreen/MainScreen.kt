package fr.eya.uwblink.ui.welcomescreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.eya.uwblink.AppContainer
import fr.eya.uwblink.ui.home.HomeViewModel
import fr.eya.uwblink.ui.nav.AppNavBar
import fr.eya.uwblink.ui.ranging.RangingViewModel

@Composable
fun MainScreen(appContainer: AppContainer) {
    val rangingViewModel: RangingViewModel =
        viewModel(factory = RangingViewModel.provideFactory(appContainer.rangingResultSource))
    val uiState by rangingViewModel.uiState.collectAsState()
    val homeViewModel: HomeViewModel =
        viewModel(
            factory = HomeViewModel.provideFactory(
                LocalContext.current,
                appContainer.rangingResultSource,
                // Pass the context here
            )
        )
    AppNavBar(
        appContainer = appContainer,
        viewModel = homeViewModel,
        isRanging = uiState,
        startRanging = { rangingViewModel.startRanging() },
        stopRanging = { rangingViewModel.stopRanging() }
    )
}
