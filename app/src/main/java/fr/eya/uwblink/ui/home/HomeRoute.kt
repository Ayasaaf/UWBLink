package fr.eya.uwblink.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.apps.hellouwb.ui.home.HomeScreen

@Composable
fun HomeRoute(homeViewModel : HomeViewModel){
    val uiState by homeViewModel.uiState.collectAsState()
    HomeScreen(uiState = uiState)

}