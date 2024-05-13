package fr.eya.uwblink.ui.control

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.eya.uwblink.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlScreen(uiState: ControlUiState, modifier: Modifier = Modifier) {

    CenterAlignedTopAppBar(
        title = { Text("Device Control") }, modifier = modifier
    )

    Column(
        modifier = Modifier
            .padding(100.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState is ControlUiState.KeyState) {
            KeyScreen()
        } else if (uiState is ControlUiState.LockState) {
            LockScreen(isLocked = uiState.isLocked)
        }
    }
}

@Composable
fun LockScreen(isLocked: Boolean) {
    val icon = if (isLocked) R.drawable.ic_locked else R.drawable.ic_lock
    Image(
       painter = painterResource(id = icon),
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        contentDescription = null
    )
}

@Composable
fun KeyScreen() {
    val icon = R.drawable.ic_key

    Image(
        painter = painterResource(id = icon) ,
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        contentDescription = null
    )

}

@Preview
@Composable
fun PreviewControlScreen() {
    ControlScreen(
        uiState = ControlUiState.KeyState
    )
}