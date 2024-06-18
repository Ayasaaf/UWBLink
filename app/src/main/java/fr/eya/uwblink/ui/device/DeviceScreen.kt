package fr.eya.uwblink.ui.device

import LoaderAnimation
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.eya.uwblink.BluetoothChat.BluetoothUiState
import fr.eya.uwblink.BluetoothChat.domain.BluetoothDevice
import fr.eya.uwblink.R
import fr.eya.uwblink.ui.nav.AppDestination


@Composable
fun DeviceScreen(
    navController: NavController,
    onStartServer: () -> Unit,
    state: BluetoothUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onDeviceClick: (BluetoothDevice) -> Unit
) {
    val (isScanning, setIsScanning) = remember { mutableStateOf(false) } // Track scanning state

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier
            .fillMaxWidth() .align(Alignment.Start)
            .padding()) {
            IconButton(onClick = { navController.navigate(AppDestination.Choose_ROUTE) }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Disconnect")
            }
        }
        BluetoothDeviceList(
            navController = navController,
            PairedDevices = state.pairedDevices,
            ScannedDevices = state.scannedDevices,
            onClick = onDeviceClick,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Row( // Modifier adjustments for center alignment
            modifier = Modifier
                .fillMaxWidth() // Stretch horizontally
                .padding() // Add some padding (optional)
                .align(Alignment.CenterHorizontally) // Vertically center within parent
        ) {
            Button( // Circular scan button with dynamic color
                onClick = {
                    if (isScanning) {
                        onStopScan()
                        setIsScanning(false)
                    } else {
                        onStartScan()
                        setIsScanning(true)
                    }
                },
                modifier = Modifier.size(width = 100.dp, height = 50.dp),
                colors = if (isScanning) ButtonDefaults.buttonColors(Color.Green) else ButtonDefaults.buttonColors(Color(0xFF6200EE))
            ) {
                Text(text = if (isScanning) "Stop" else "Start")
            }
            Spacer(modifier = Modifier.weight(0.5f))
            Button( // Circular pairing button
                onClick = { onStartServer(); navController.navigate(AppDestination.CHAT_ROUTE) },
                modifier = Modifier.size(width = 100.dp, height = 50.dp),

                colors = ButtonDefaults.buttonColors(Color(0xFF6200EE))
            ) {
                Text(text = "Pair")
            }
        }
    }
}

@Composable
fun BluetoothDeviceList(

    navController: NavController,
    PairedDevices: List<BluetoothDevice>,
    ScannedDevices: List<BluetoothDevice>,
    onClick: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically // Alignement vertical au centre
            ) {
                LoaderAnimation(
                    modifier = Modifier.size(100.dp), anim = R.raw.buettoth
                )


                Text(
                    text = "Paired Devices",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        items(PairedDevices) { device ->

            Text(
                text = device.name ?: "(No name)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClick(device)
                        navController.navigate("Chat_Screen")
                    }
                    .padding(16.dp))
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {


                LoaderAnimation(
                    modifier = Modifier.size(100.dp), anim = R.raw.buettoth
                )
                Text(
                    text = "Scanned Devices",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        items(ScannedDevices) { device ->

            Text(
                text = device.name ?: "(No name)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClick(device)
                        navController.navigate(AppDestination.CHAT_ROUTE)
                    }
                    .padding(16.dp)
            )


        }
    }

}




