package fr.eya.uwblink.ui.welcomescreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.eya.uwblink.AppContainer
import fr.eya.uwblink.ui.nav.AppNavBar
import fr.eya.uwblink.ui.ranging.RangingViewModel

@Composable
fun MainScreen(appContainer: AppContainer) {
    val rangingViewModel: RangingViewModel =
        viewModel(factory = RangingViewModel.provideFactory(appContainer.rangingResultSource))
    val uiState by rangingViewModel.uiState.collectAsState()
    AppNavBar(
        appContainer = appContainer,
        isRanging = uiState,
        startRanging = { rangingViewModel.startRanging() },
        stopRanging = { rangingViewModel.stopRanging() }
    )
}
