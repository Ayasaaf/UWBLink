package fr.eya.uwblink.ui

import androidx.compose.runtime.Composable
import fr.eya.uwblink.AppContainer
import fr.eya.uwblink.ui.theme.MyAppTheme
import fr.eya.uwblink.ui.welcomescreen.MainScreen

@Composable


    fun HelloUwbApp(appContainer: AppContainer) {

        MyAppTheme {
          MainScreen(appContainer = appContainer)
            }
        }
