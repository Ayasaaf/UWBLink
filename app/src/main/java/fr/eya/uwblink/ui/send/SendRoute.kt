package fr.eya.uwblink.ui.send

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun SendRoute(sendViewModel: SendViewModel) {
    val uiState by sendViewModel.uiState.collectAsState()
    SendScreen(
        uiState = uiState,
        onImagePicked = { sendViewModel.setSentUri(it) },
        onImageCleared = { sendViewModel.clear() },
        onMessageDisplayed = { sendViewModel.messageShown() }
    )
}