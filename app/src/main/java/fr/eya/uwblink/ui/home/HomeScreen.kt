package com.google.apps.hellouwb.ui.home

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.uwb.RangingMeasurement
import androidx.core.uwb.RangingPosition
import fr.eya.ranging.UwbEndPoint
import fr.eya.uwblink.ui.home.ConnectedEndpoint
import fr.eya.uwblink.ui.home.HomeUiState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val ENDPOINT_COLORS =
    arrayListOf(
        Color.Red,
        Color.Blue,
        Color.Green,
        Color.Cyan,
        Color.Magenta,
        Color.DarkGray,
        Color.Yellow
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(uiState: HomeUiState, modifier: Modifier = Modifier) {
    val topAppBarState = rememberTopAppBarState()

    Scaffold(
        topBar = { HomeTopAppBar(isRanging = uiState.isRanging, topAppBarState = topAppBarState) },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(modifier = Modifier.padding(innerPadding)) {
                ConnectStatusBar(
                    uiState.connectedEndpoints.map { it.position },
                    uiState.connectedEndpoints.map { it.endpoint },
                    uiState.disconnectedEndpoints
                )
            }
            Row { RangingPlot(uiState.connectedEndpoints) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    isRanging: Boolean,
    modifier: Modifier = Modifier,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior? =
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState),
) {
    CenterAlignedTopAppBar(
        title = { Text("UWB Ranging") },
        actions = {
            val icon = if (isRanging) Icons.Filled.Done else Icons.Filled.Close
            val iconColor = if (isRanging) Color.Green else Color.DarkGray
            Image(
                imageVector = icon,
                colorFilter = ColorFilter.tint(iconColor),
                contentDescription = null
            )
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
fun RangingPlot(connectedEndpoints: List<ConnectedEndpoint>) {
    // Creating a 2D Drawing interface
    val context = LocalContext.current

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        // Assuming the canvas is 20 meters wide
        val center = Offset(size.width / 2.0f, size.height / 2.0f)
        val scale = drawPolar(center)
        connectedEndpoints.forEachIndexed { index, endpoint ->
            endpoint.position.distance?.let { distance ->

                endpoint.position.azimuth?.let { azimuth ->
                    val distanceValue = distance.value
                    Log.d("RangingPlot", "Endpoint ${endpoint}: distance - $distanceValue meters, ")
                    drawPositionIndicator(
                        distanceValue,
                        azimuth.value,
                        scale,
                        centerOffset = center,
                        color = ENDPOINT_COLORS[index % ENDPOINT_COLORS.size]
                    )
                    if (distanceValue < 0.5f) {

                        val textOffset =
                            Offset(center.x, center.y + 30.dp.toPx()) // Adjust offset as needed
                        showToast(context, "Arrived!")

                    }
                }
            }
        }
    }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

private fun DrawScope.drawPositionIndicator(
    distance: Float,
    azimuth: Float,
    scale: Float,
    centerOffset: Offset,
    color: Color,
) {
    val angle = azimuth * PI / 180
    val x = distance * sin(angle).toFloat()
    val y = distance * cos(angle).toFloat()

    // Draw circle
    drawCircle(
        center = centerOffset.plus(Offset(x * scale, -y * scale)),
        color = color,
        radius = 15.0f
    )

    // Draw arrow (adjust size and position as needed)
    val arrowSize = 10.dp.toPx()
    val arrowTip = centerOffset.plus(Offset(x * scale, -y * scale))
    val arrowBase = arrowTip.minus(
        Offset(
            (arrowSize * cos(angle)).toFloat(),
            (arrowSize * sin(angle)).toFloat()
        )
    )
    drawLine(
        color = color,
        start = arrowBase,
        end = arrowTip,
        strokeWidth = 2.dp.toPx()
    )
    drawLine(
        color = color,
        start = arrowTip,
        end = arrowTip.plus(
            Offset(
                (arrowSize * 0.3f * cos(angle - PI / 6)).toFloat(),
                (arrowSize * 0.3f * sin(angle - PI / 6)).toFloat()
            )
        ),
        strokeWidth = 2.dp.toPx()
    )
    drawLine(
        color = color,
        start = arrowTip,
        end = arrowTip.plus(
            Offset(
                (arrowSize * 0.3f * cos(angle + PI / 6)).toFloat(),
                (arrowSize * 0.3f * sin(angle + PI / 6)).toFloat()
            )
        ),
        strokeWidth = 2.dp.toPx()
    )
}

private fun DrawScope.drawPolar(centerOffset: Offset): Float {
    val scale = size.minDimension / 20.0f
    (1..10).forEach {
        drawCircle(
            center = centerOffset,
            color = Color.DarkGray,
            radius = it * scale,
            style = Stroke(2f)
        )
    }

    val angles = floatArrayOf(0f, 30f, 60f, 90f, 120f, 150f)
    angles.forEach {
        val rad = it * PI / 180
        val start =
            center + Offset((scale * 10f * cos(rad)).toFloat(), (scale * 10f * sin(rad)).toFloat())
        val end =
            center - Offset((scale * 10f * cos(rad)).toFloat(), (scale * 10f * sin(rad)).toFloat())
        drawLine(
            color = Color.DarkGray,
            start = start,
            end = end,
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.0f, 5.0f), 10f)
        )
    }
    return scale
}


@Composable
fun ConnectStatusBar(
    connectedEndpoints: List<RangingPosition>,
    connectingEndpoints: List<UwbEndPoint>,

    disconnectedEndpoints: List<UwbEndPoint>,
    modifier: Modifier = Modifier,
) {
    Box(modifier.height(100.dp)) {
        Box(modifier.height(100.dp)) {
            Column {
                Row {
                    connectingEndpoints.forEachIndexed { index, endpoint ->
                        val icon = Icons.Filled.LocationOn // Placeholder icon
                        val contentDescription = "Connecting to ${endpoint.id.split("|")[0]}"
                        Row(
                            modifier = Modifier.width(100.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = contentDescription,
                                tint = Color.Green, // Adjust color for connecting state
                                modifier = Modifier.size(50.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = endpoint.id.split("|")[0],
                                textAlign = TextAlign.Center,
                                style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp),
                                color = ENDPOINT_COLORS[index % ENDPOINT_COLORS.size]
                            )
                        }
                    }
                }
                Row {
                    connectedEndpoints.forEachIndexed { index, endpoint ->

                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = endpoint.distance?.value.toString(),
                            textAlign = TextAlign.Center,
                            color = ENDPOINT_COLORS[index % ENDPOINT_COLORS.size]
                        )
                    }
                }
            }

        }

        Row {
            disconnectedEndpoints.forEach { endpoint ->
                Text(modifier = Modifier.width(100.dp), text = endpoint.id, color = Color.DarkGray)
            }
        }
    }

}

@Preview
@Composable
fun PreviewHomeScreen(modifier: Modifier = Modifier) {
    HomeScreen(
        uiState =
        object : HomeUiState {
            override val connectedEndpoints =
                listOf(
                    ConnectedEndpoint(
                        UwbEndPoint("EP1", byteArrayOf()),
                        RangingPosition(
                            distance = RangingMeasurement(2.0f),
                            azimuth = RangingMeasurement(10.0f),
                            elevation = null,
                            elapsedRealtimeNanos = 200L
                        ),
                    ),
                    ConnectedEndpoint(
                        UwbEndPoint("EP2", byteArrayOf()),
                        RangingPosition(
                            distance = RangingMeasurement(10.0f),
                            azimuth = RangingMeasurement(-10.0f),
                            elevation = null,
                            elapsedRealtimeNanos = 200L
                        ),
                    )
                )

            override val disconnectedEndpoints: List<UwbEndPoint> =
                listOf(UwbEndPoint("EP3", byteArrayOf()), UwbEndPoint("EP4", byteArrayOf()))

            override val isRanging = true
        },
        modifier = modifier
    )
}