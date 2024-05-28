

@file:Suppress("PreviewAnnotationInFunctionWithParameters")


package fr.eya.uwblink.ui.welcomescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import fr.eya.uwblink.R
import fr.eya.uwblink.ui.nav.AppDestination

@Preview
@Composable
fun HelloScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
      //  verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Notification Icon
            Icon(
                painter = painterResource(id = R.drawable.ic_notification),
                contentDescription = "Notification Icon",
                tint = Color.Black,
                modifier = Modifier.clickable {
                    // Handle notification icon click
                }
            )

            // User Profile Icon
            Icon(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "User Profile Icon",
                tint = Color.Black,
                modifier = Modifier.clickable {

                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
         verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animation
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.hello))
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(350.dp)
        )

        // Text: What do you want to locate today?
        Text(
            text = "What do you want to locate today?",
            style = MaterialTheme.typography.h5
        )

        // Lottie Animations: Phone and IoT object
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Phone Animation
            val composition1 by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.phone))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LottieAnimation(
                    composition = composition1,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(100.dp)
                )
                Button(
                    onClick = { navController.navigate(AppDestination.DEVICE_ROUTE) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary
                    ),
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp)
                ) {
                    Text("Phone")
                }
            }

            // IoT Device Animation
            val composition2 by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sensor))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LottieAnimation(
                    composition = composition2,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(100.dp)
                )
                Button(
                    onClick = { navController.navigate(AppDestination.DEVICE_ROUTE) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary
                    ),
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp)
                ) {
                    Text("IOT device")
                }
            }
        }}}