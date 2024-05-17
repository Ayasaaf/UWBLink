package fr.eya.uwblink.ui.nav

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.eya.uwblink.AppContainer
import fr.eya.uwblink.ui.Screen
import fr.eya.uwblink.ui.ranging.RangingControlIcon


@Composable
fun AppNavBar(
    appContainer: AppContainer,

    // parameters for the ranging Button
    isRanging: Boolean,
    startRanging: () -> Unit,
    stopRanging: () -> Unit,
    CurrentDestination: String?

    ) {
    val navController = rememberNavController()
    //ranging state to store the state of the ranging button
    val rangingState = remember { mutableStateOf(isRanging) }
    val showRangingFab = CurrentDestination == Screen.Home.route // Check if the current destination is the home screen

        Scaffold(
            bottomBar = {
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
            },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                if (showRangingFab) {
                Column {
                    FloatingActionButton(
                        shape = CircleShape,
                        onClick = {},
                        contentColor = Color.White
                    ) {
                        RangingControlIcon(selected = rangingState.value) {
                            rangingState.value = it
                            if (it) {
                                startRanging()
                            } else {
                                stopRanging()
                            }
                        }
                    }

                    }

                } }


        ) { innerPadding ->
            AppNavGraph(
                appContainer = appContainer,
                modifier = Modifier.padding(innerPadding),
                navController = navController
            )
        }
    }

private val items =
    listOf(Screen.Device, Screen.Home, Screen.Chat, Screen.Control, Screen.Send, Screen.Settings)
