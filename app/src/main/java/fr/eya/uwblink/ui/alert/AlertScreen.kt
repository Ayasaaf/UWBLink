package fr.eya.uwblink.ui.alert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
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
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import fr.eya.uwblink.R
import fr.eya.uwblink.ui.home.HomeViewModel

@Composable
fun DangerZoneScreen(homeViewModel: HomeViewModel, onDistanceSet: (Float) -> Unit) {
    var distance by remember { mutableStateOf(0f) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.danger)) // Replace with your actual Lottie file name
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever // Loop the animation
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier
                .size(200.dp) // Adjust size as needed
                .padding(bottom = 16.dp)
        )
        Text(text = "Define the Maximal Distance in Meters")
        Slider(
            value = distance,
            onValueChange = { distance = it },
            valueRange = 0f..100f, // Limite de distance entre 0 et 100 mètres
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Text(text = "Chosen Distance: ${distance.toInt()} meters")

        Button(onClick = {
            homeViewModel.setMaxDistanceAllowed(distance)
            onDistanceSet(distance)
        }) {
            Text(text = "Define Your Danger Zone")
        }
    }
}
