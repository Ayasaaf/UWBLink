package fr.eya.uwblink.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Device : Screen( route = "Device_Screen" , "Device" , Icons.Filled.Add)
    object Chat : Screen( route = "Chat_Screen" , "Chat" , Icons.Filled.Email )
    object Home : Screen("home", "Ranging", Icons.Filled.Home)
    object Control : Screen("control", "Control", Icons.Filled.Lock)
    object Send : Screen("send", "Share file", Icons.AutoMirrored.Filled.Send)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}