package fr.eya.uwblink.ui.scan

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun QRScannerUI() {
    var scanResult by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Create a launcher for scanning QR codes
    val qrCodeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val contents = result.data?.getStringExtra("SCAN_RESULT")
            scanResult = contents
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                val options = ScanOptions().apply {
                    setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                    setPrompt("Scan a QR code")
                    setCameraId(0)  // Optional: Select a camera ID, 0 for rear and 1 for front
                    setBeepEnabled(false)
                    setBarcodeImageEnabled(true)
                }
                val intent = options.createScanIntent(context)
                qrCodeLauncher.launch(intent)
            }
        ) {
            Text(text = "Scan QR Code")
        }

        Spacer(modifier = Modifier.height(16.dp))

        scanResult?.let {
            Text(
                text = "Scan Result: $it",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}