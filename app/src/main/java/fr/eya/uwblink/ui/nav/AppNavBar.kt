package fr.eya.uwblink.ui.nav

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.eya.uwblink.AppContainer
import fr.eya.uwblink.ui.Screen
import fr.eya.uwblink.ui.home.HomeViewModel
import fr.eya.uwblink.ui.home.showToast
import fr.eya.uwblink.ui.ranging.RangingControlIcon

@Composable
fun AppNavBar(
    appContainer: AppContainer,
    viewModel: HomeViewModel ,
    // parameters for the ranging Button
    isRanging: Boolean,
    startRanging: () -> Unit,
    stopRanging: () -> Unit,
) {
    val context = LocalContext.current

    // Check onboarding completion from SharedPreferences
    val sharedPreferences = context.getSharedPreferences("On_Boarding_Screen", Context.MODE_PRIVATE)
    val isOnboardingFinished = sharedPreferences.getBoolean("isFinished", false)


    val navController = rememberNavController()
    // Ranging state to store the state of the ranging button
    val rangingState = remember { mutableStateOf(isRanging) }

    Scaffold(
        bottomBar = {
            if (isOnboardingFinished) { // Conditionally show NavigationBar
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Image(imageVector = screen.icon, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = { AppNavigation(navController).navTo(screen.route) }
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showFloatingActionButton =  // Check if it's the home screen
                currentDestination?.hierarchy?.any { it.route == Screen.Home.route } == true // Conditionally show FloatingActionButton
            if (showFloatingActionButton && isOnboardingFinished) {

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                ) {
                    Button(onClick = {
                        viewModel.loadEndpointData(context)
                        showToast(context, "Data Loaded")
                    }) {
                        Text("Load Data")
                    }

                    // Floating Action Button for ranging
                    FloatingActionButton(
                        shape = CircleShape,
                        onClick = {},
                        contentColor = Color.White,
                        modifier = Modifier.padding(start = 10.dp) // Add padding to separate the buttons
                    ) {
                        RangingControlIcon(selected = rangingState.value) {
                            rangingState.value = it
                            Log.d("AppNavBar", "Ranging button clicked: $it")
                            if (it) {
                                startRanging()
                                Log.d("AppNavBar", "Starting ranging")
                            } else {
                                stopRanging()
                                Log.d("AppNavBar", "Stopping ranging")
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        AppNavGraph(
            appContainer = appContainer,
            modifier = Modifier.padding(innerPadding),
            navController = navController , context = context
        )
    }
}


private val items =
    listOf(Screen.Device, Screen.Home, Screen.Chat, Screen.Scan, Screen.Send, Screen.Settings , Screen.Store , Screen.Alert)

