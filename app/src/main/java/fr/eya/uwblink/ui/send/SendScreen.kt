package fr.eya.uwblink.ui.send

import android.graphics.ImageDecoder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendScreen(
    uiState: SendUiState,
    onImagePicked: (Uri) -> Unit,
    onImageCleared: () -> Unit,
    onMessageDisplayed: () -> Unit,
    modifier: Modifier = Modifier
) {

    CenterAlignedTopAppBar(
        title = { Text("Pictures  Transfer") },
        modifier = modifier
    )

    Column(
        modifier = Modifier
            .padding(50.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (uiState) {
            is SendUiState.InitialState -> InitialScreen(onImagePicked)
            is SendUiState.SendingState -> {
                SendingScreen(uiState.sendImageUri, onImageCleared)
                uiState.message?.let {
                    Toast.makeText(LocalContext.current, it, Toast.LENGTH_LONG).show()
                    onMessageDisplayed()
                }
            }
            is SendUiState.ReceivedState -> ReceivedScreen(
                uiState.receivedImageUri,
                onImageCleared
            )
        }
    }
}

@Composable
fun InitialScreen(onImagePicked: (Uri) -> Unit) {
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri?
            ->
            uri?.let { onImagePicked(it) }
        }

    Button(onClick = { launcher.launch("image/*") }) { Text(text = "Select a picture") }

    Spacer(modifier = Modifier.height(12.dp))
    Text(text = "Receiving images...")
}

@Composable
fun SendingScreen(imgUri: Uri, onImageCleared: () -> Unit) {
    Button(onClick = { onImageCleared() }) { Text(text = "Clear") }

    Spacer(modifier = Modifier.height(12.dp))

    val source = ImageDecoder.createSource(LocalContext.current.contentResolver, imgUri)
    val image = ImageDecoder.decodeBitmap(source)
    Box(
        Modifier
            .height(400.dp)
            .fillMaxWidth()) {
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
    Text(text = "Sending images...")
}

@Composable
fun ReceivedScreen(imgUri: Uri, onImageCleared: () -> Unit) {
    Button(onClick = { onImageCleared() }) { Text(text = "Clear") }

    Spacer(modifier = Modifier.height(12.dp))

    val source = ImageDecoder.createSource(LocalContext.current.contentResolver, imgUri)
    val image = ImageDecoder.decodeBitmap(source)
    Box(
        Modifier
            .height(400.dp)
            .fillMaxWidth()) {
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
    Text(text = "Image is received.")
}

@Preview
@Composable
fun PreviewSendScreen(modifier: Modifier = Modifier) {
    InitialScreen {}
}