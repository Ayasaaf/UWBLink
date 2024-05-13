
import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import fr.eya.uwblink.R
import fr.eya.uwblink.ui.MainActivity
import kotlinx.coroutines.delay

@Composable

fun SplashScreen(navController: NavController, context: MainActivity) {

    val alpha = remember {
        Animatable(0f)
    }

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("On_Boarding_Screen", Context.MODE_PRIVATE)
    val isOnboardingFinished = sharedPreferences.getBoolean("isFinished", false)

    LaunchedEffect(Unit) {
        delay(2000) // Add a delay for splash screen effect
        if (!isOnboardingFinished) {
            navController.navigate("On_Boarding_Screen")
        } else {
            navController.navigate("Hello_Screen")
        }
    }





    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isSystemInDarkTheme()) Color.DarkGray else Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoaderAnimation(
            modifier = Modifier.size(200.dp), anim = R.raw.splasherscreen
        )
        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Connect Locate Chat",
            modifier = Modifier
                .alpha(alpha.value)
                .fillMaxWidth(),
            fontSize = 40.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )

    }
}

@Composable
fun LoaderAnimation(modifier: Modifier, anim: Int) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(anim))

    LottieAnimation(
        composition = composition, iterations = LottieConstants.IterateForever,
        modifier = modifier
    )
}

