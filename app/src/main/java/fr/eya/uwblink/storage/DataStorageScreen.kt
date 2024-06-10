package fr.eya.uwblink.storage

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import fr.eya.uwblink.R
import fr.eya.uwblink.ui.home.HomeViewModel



@Composable
fun DataStorageScreen(viewModel: HomeViewModel, context: Context) {
    var periodText by remember { mutableStateOf("") }

    // Load Lottie animation composition
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Lottie animation
        LottieAnimation(
            composition,
            modifier = Modifier.size(300.dp) .fillMaxWidth() .align(Alignment.CenterHorizontally)
        )

        // Text field for entering storage period
        OutlinedTextField(
            value = periodText,
            onValueChange = { periodText = it },
            label = { Text("Enter storage period in seconds") },
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Start Storage Button
            Button(
                onClick = {
                    viewModel.startSavingDataPeriodically(periodText.toIntOrNull() ?: 0, context)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Storage")
            }

            // Stop Storage Button
            Button(
                onClick = {
                    viewModel.stopSavingDataPeriodically(context)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Stop Storage")
            }

            // Save Data Button
            Button(
                onClick = {
                    viewModel.saveDataToTextFile(context)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Data")
            }
        }
    }
}