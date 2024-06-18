package fr.eya.uwblink.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import fr.eya.uwblink.R
import fr.eya.uwblink.uwbranging.data.AppSettings
import fr.eya.uwblink.uwbranging.data.ConfigType
import fr.eya.uwblink.uwbranging.data.DeviceType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreen(
    uiState: AppSettings,
    updateDeviceDisplayName: (String) -> Unit,
    updateDeviceType: (DeviceType) -> Unit,
    updateConfigType: (ConfigType) -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = { Text("Device Settings") },
        modifier = modifier
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        

        // Lottie animation
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.settings))
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        var fieldValue by remember { mutableStateOf(uiState.deviceDisplayName) }
        OutlinedTextField(
            fieldValue,
            onValueChange = { fieldValue = it },
            singleLine = true
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(20.dp)
        ) {
            Text("Device Type:")
            Row(Modifier.padding(5.dp)) {
                val selectedValue = remember { mutableStateOf(uiState.deviceType) }
                RadioButton(
                    selected = selectedValue.value == DeviceType.CONTROLLER,
                    onClick = {
                        updateDeviceType(DeviceType.CONTROLLER)
                        selectedValue.value = DeviceType.CONTROLLER
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Green,
                        unselectedColor = Color.Blue
                    )
                )
                Text("Controller")

                RadioButton(
                    selected = selectedValue.value == DeviceType.CONTROLLEE,
                    onClick = {
                        updateDeviceType(DeviceType.CONTROLLEE)
                        selectedValue.value = DeviceType.CONTROLLEE
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF6200EE),
                        unselectedColor = Color.Blue
                    )
                )
                Text("Controlee")
            }

            Text("Config Type:")
            Row(Modifier.padding(5.dp)) {
                val selectedValue = remember { mutableStateOf(uiState.configType) }
                RadioButton(
                    selected = selectedValue.value == ConfigType.CONFIG_UNICAST_DS_TWR,
                    onClick = {
                        updateConfigType(ConfigType.CONFIG_UNICAST_DS_TWR)
                        selectedValue.value = ConfigType.CONFIG_UNICAST_DS_TWR
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Green,
                        unselectedColor = Color.Blue
                    )
                )
                Text("One-To-One")

                RadioButton(
                    selected = selectedValue.value == ConfigType.CONFIG_MULTICAST_DS_TWR,
                    onClick = {
                        updateConfigType(ConfigType.CONFIG_MULTICAST_DS_TWR)
                        selectedValue.value = ConfigType.CONFIG_MULTICAST_DS_TWR
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF6200EE),
                        unselectedColor = Color.Blue
                    )
                )
                Text("One-To-Many")
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen(
        AppSettings.newBuilder()
            .setDeviceDisplayName("UWB")
            .setDeviceType(DeviceType.CONTROLLEE)
            .setConfigType(ConfigType.CONFIG_MULTICAST_DS_TWR)
            .build(),
        {},
        {},
        {}
    )
}