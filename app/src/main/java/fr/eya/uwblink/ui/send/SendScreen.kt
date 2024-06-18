package fr.eya.uwblink.ui.send

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import fr.eya.uwblink.R
import fr.eya.uwblink.ui.home.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException


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
        title = { Text("Pictures Transfer", color = Color.White) },
        modifier = modifier.background(Color(0xFF6200EE))
    )

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF5F5F5)) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (uiState) {
                is SendUiState.InitialState -> InitialScreen(onImagePicked)
                is SendUiState.SendingState -> {
                    Log.d("SendScreen", "Navigating to SendingScreen with URI: ${uiState.sendImageUri}")

                    SendingScreen(uiState.sendImageUri, onImageCleared)
                    uiState.message?.let {
                        Log.d("SendScreen", "Displaying message: $it")

                        Toast.makeText(LocalContext.current, it, Toast.LENGTH_LONG).show()
                        onMessageDisplayed()
                    }
                }
                is SendUiState.ReceivedState -> {
                    Log.d("SendScreen", "Navigating to ReceivedScreen with URI: ${uiState.receivedImageUri}")

                    ReceivedScreen(
                        uiState.receivedImageUri,
                        onImageCleared
                    )
                }
            }
        }
    }
}

@Composable
fun InitialScreen(onImagePicked: (Uri) -> Unit) {
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            Log.d("InitialScreen", "Image selected: $uri")
            uri?.let { onImagePicked(it) }
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .size(400.dp)
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.chat))

        LottieAnimation(
            composition,
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { launcher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(Color(0xFFF5F5F5))
        ) {
            Text(text = "Select a picture", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Receiving images...", color = Color.Gray)
    }
}

@Composable
fun SendingScreen(imgUri: Uri, onImageCleared: () -> Unit) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight()
    ) {
        IconButton(onClick = { onImageCleared() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_clear),
                contentDescription = "Clear",
                tint = Color(0xFF6200EE)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val image = loadImageFromUri(context, imgUri)
        image?.let {
            Box(
                Modifier
                    .height(400.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Sending images...", color = Color(0xFF6200EE), fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))
            CircularProgressIndicator(color = Color(0xFF6200EE))
        }
    }
}

@Composable
fun ReceivedScreen(imgUri: Uri, onImageCleared: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight()
    ) {
        IconButton(onClick = { onImageCleared() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_clear),
                contentDescription = "Clear",
                tint = Color(0xFF6200EE)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val image = loadImageFromUri(context, imgUri)
        image?.let {
            Box(
                Modifier
                    .height(400.dp)
                    .fillMaxWidth()
                    .clickable {
                        coroutineScope.launch(Dispatchers.Main) {
                            saveImageToExternalStorage(context, it, "received_image_${System.currentTimeMillis()}")
                            showToast(context, "Image saved successfully")
                        }
                    }
            ) {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Image is received. Click to save.",
                color = Color(0xFF6200EE),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun loadImageFromUri(context: Context, imgUri: Uri): Bitmap? {
    return try {
        val source = ImageDecoder.createSource(context.contentResolver, imgUri)
        ImageDecoder.decodeBitmap(source)
    } catch (e: Exception) {
        Log.e("loadImageFromUri", "Failed to load image: ${e.message}", e)
        null
    }
}

private fun saveImageToExternalStorage(context: Context, bitmap: Bitmap, imageName: String) {
    try {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$imageName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            context.contentResolver.openOutputStream(it).use { outputStream ->
                outputStream?.let { stream ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                        throw IOException("Couldn't save bitmap.")
                    }
                }
            }
        }
    } catch (e: IOException) {
        Log.e("saveImageToExternalStorage", "Failed to save image: ${e.message}", e)
    }
}

@Preview
@Composable
fun PreviewSendScreen(modifier: Modifier = Modifier) {
    InitialScreen {}
}