package fr.eya.uwblink.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.uwb.RangingMeasurement
import androidx.core.uwb.RangingPosition
import fr.eya.ranging.UwbEndPoint
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
    Scaffold(topBar = {
        HomeTopAppBar(isRanging = uiState.isRanging, topAppBarState = topAppBarState)
    }, modifier = modifier)
    { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(modifier = Modifier.padding(innerPadding)) {
                ConnectStatusBar(
                    uiState.connectedEndpoints.map { it.endpoint },
                    uiState.disconnectedEndpoints
                )

            }
            Row {
                RangingPlot(uiState.connectedEndpoints) }
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
    CenterAlignedTopAppBar(title = { Text(text = "UWBLINK") },
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
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        // Assume the canvas is 20 meters wide.
        val center = Offset(size.width / 2.0f, size.height / 2.0f)

        val scale = drawPolar(center)
        connectedEndpoints.forEachIndexed { index, endpoint ->
            endpoint.position.distance?.let { distance ->
                endpoint.position.azimuth?.let { azimuth ->
                    drawPosition(
                        distance.value,
                        azimuth.value,
                        scale = scale,
                        centerOffset = center,
                        color = ENDPOINT_COLORS[index % ENDPOINT_COLORS.size]
                    )
                }
            }
        }
    }
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

private fun DrawScope.drawPosition(
    distance: Float,
    azimuth: Float,
    scale: Float,
    centerOffset: Offset,
    color: Color,
) {
    val angle = azimuth * PI / 180
    val x = distance * sin(angle).toFloat()
    val y = distance * cos(angle).toFloat()
    drawCircle(
        center = centerOffset.plus(Offset(x * scale, -y * scale)),
        color = color,
        radius = 15.0f
    )
}

@Composable
fun ConnectStatusBar(
    connectedEndpoints: List<UwbEndPoint>,
    disconnectedEndpoints: List<UwbEndPoint>,
    modifier: Modifier = Modifier,
) {
    Box(modifier.height(50.dp)) {
        Column {
            //
            Row {
                connectedEndpoints.forEachIndexed { index, endpoint ->
                    Text(
                        modifier = Modifier.width(100.dp),
                        text = endpoint.id.split("|")[0],
                        color = ENDPOINT_COLORS[index % ENDPOINT_COLORS.size]
                    )
                }
            }
            Row {
                disconnectedEndpoints.forEach { endpoint ->
                    Text(
                        modifier = Modifier.width(100.dp),
                        text = endpoint.id,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen(modifier: Modifier = Modifier) {
    HomeScreen(
        uiState = object : HomeUiState {
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




