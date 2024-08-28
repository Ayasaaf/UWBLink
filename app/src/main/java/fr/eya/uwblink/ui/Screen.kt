package fr.eya.uwblink.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Device : Screen(  "Device_Screen" , "Device" , Icons.Filled.Edit)
    object Store : Screen(  "Storage_Screen" , "Save" , Icons.Filled.PlayArrow)
    object Chat : Screen(  "Chat_Screen" , "Chat" , Icons.Filled.Email )
    object Home : Screen("home", "Locate", Icons.Filled.LocationOn)
    object Scan : Screen("scan", "Scan", Icons.Filled.Search)
    object Send : Screen("send", "Share", Icons.AutoMirrored.Filled.Send)
    object Settings : Screen("settings", "Setting", Icons.Filled.Settings)
    object Alert : Screen("alert", "Alert" , Icons.Filled.Warning)
}