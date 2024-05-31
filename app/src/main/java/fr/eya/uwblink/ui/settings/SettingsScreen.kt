package fr.eya.uwblink.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .padding(60.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        Text("Display Name")
        var fieldValue by remember { mutableStateOf(uiState.deviceDisplayName) }
        OutlinedTextField(
            fieldValue,
            onValueChange = { fieldValue = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions =
            KeyboardActions(
                onDone = {
                    updateDeviceDisplayName(fieldValue)
                    focusManager.clearFocus(true)
                }
            ),
            singleLine = true
        )

        Row {
            Column {
                Text("Device Type:", Modifier.padding(20.dp))
                Row(Modifier.padding(5.dp)) {
                    val selectedValue = remember { mutableStateOf(uiState.deviceType) }
                    Column(Modifier.width(120.dp)) {
                        RadioButton(
                            selected = selectedValue.value == DeviceType.CONTROLLER,
                            onClick = {
                                updateDeviceType(DeviceType.CONTROLLER)
                                selectedValue.value = DeviceType.CONTROLLER
                            },
                        )
                        Text("Controller")
                    }
                    Column(Modifier.width(120.dp)) {
                        RadioButton(
                            selected = selectedValue.value == DeviceType.CONTROLLEE,
                            onClick = {
                                updateDeviceType(DeviceType.CONTROLLEE)
                                selectedValue.value = DeviceType.CONTROLLEE
                            }
                        )
                        Text("Controlee")
                    }
                }
                Text("Config Type:", Modifier.padding(20.dp))
                Row(Modifier.padding(5.dp)) {
                    val selectedValue = remember { mutableStateOf(uiState.configType) }
                    Column(Modifier.width(120.dp)) {
                        RadioButton(
                            selected = selectedValue.value == ConfigType.CONFIG_UNICAST_DS_TWR,
                            onClick = {
                                updateConfigType(ConfigType.CONFIG_UNICAST_DS_TWR)
                                selectedValue.value = ConfigType.CONFIG_UNICAST_DS_TWR
                            },
                        )
                        Text("One-To-One")
                    }
                    Column(Modifier.width(120.dp)) {
                        RadioButton(
                            selected = selectedValue.value == ConfigType.CONFIG_MULTICAST_DS_TWR,
                            onClick = {
                                updateConfigType(ConfigType.CONFIG_MULTICAST_DS_TWR)
                                selectedValue.value = ConfigType.CONFIG_MULTICAST_DS_TWR
                            }
                        )
                        Text("One-To-Many")
                    }
                }
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