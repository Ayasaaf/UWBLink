package fr.eya.uwblink.ui.device

import LoaderAnimation
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.eya.uwblink.R
import fr.eya.uwblink.ui.Bluetooth.BluetoothUiState
import fr.eya.uwblink.ui.nav.AppDestination
import fr.eya.uwblink.uwbranging.BluetoothChat.domain.BluetoothDevice


@Composable
fun DeviceScreen(

    navController: NavController,
    onStartServer: () -> Unit,
    state: BluetoothUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onDeviceClick: (BluetoothDevice) -> Unit,

    ) {


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(),
            verticalAlignment = Alignment.CenterVertically
        ) {


            IconButton(onClick = {

                navController.navigate(AppDestination.Choose_ROUTE)
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Disconnect"
                )
            }
        }

        BluetoothDeviceList(
            context = LocalContext.current,
            navController = navController,
            PairedDevices = state.pairedDevices,
            ScannedDevices = state.scannedDevices,
            onClick = onDeviceClick,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = {
                    onStartScan()
                    navController.navigate("Device_Screen")
                }


            ) {
                Text(text = "Start scan")
            }
            Button(
                onClick = { onStopScan() },

                ) {
                Text(text = "Stop Scan ")
            }
            Button(
                onClick = {
                    onStartServer()
                    navController.navigate("Chat_Screen")
                    }

            ) {
                Text(text = "Start Pairing")
            }
        }}}

@Composable
fun BluetoothDeviceList(

    navController: NavController,
    context: Context,
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
                         navController.navigate("Chat_Screen")
                    }
                    .padding(16.dp)
            )


        }
    }

}




