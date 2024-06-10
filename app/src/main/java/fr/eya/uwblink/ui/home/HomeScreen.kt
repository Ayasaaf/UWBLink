package fr.eya.uwblink.ui.home

import android.content.Context
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun HomeScreen(uiState: HomeUiState , modifier: Modifier = Modifier, ) {
    val topAppBarState = rememberTopAppBarState()
    val context = LocalContext.current

    val connectedEndpoints = uiState.connectedEndpoints
    val avgDistance = connectedEndpoints.mapNotNull { it.position.distance?.value }.average()
    val avgAzimuth = connectedEndpoints.mapNotNull { it.position.azimuth?.value }.average()
    val avgElevation = connectedEndpoints.mapNotNull { it.position.elevation?.value }.average()

    Scaffold(
        topBar = { HomeTopAppBar(isRanging = uiState.isRanging, topAppBarState = topAppBarState) },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(modifier = Modifier.padding(innerPadding)) {
                ConnectStatusBar(
                    uiState.connectedEndpoints.map { it.position },
                    uiState.connectedEndpoints.map { it.endpoint },
                    uiState.disconnectedEndpoints,
                    avgDistance.toFloat(),
                    avgAzimuth.toFloat(),
                    avgElevation.toFloat()
                )
            }
            Row {
                RangingPlot(uiState.connectedEndpoints)
            }
            // Button to check stored data

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
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {
        val centerX = size.width / 2.0f
        val centerY = size.height / 2.0f
        val maxRadius = centerX.coerceAtMost(centerY)  // Limit radius to half of canvas dimension

        connectedEndpoints.forEachIndexed { index, endpoint ->
            endpoint.position.distance?.let { distance ->
                endpoint.position.azimuth?.let { azimuth ->
                    val distanceValue = distance.value
                    val azimuthRad = azimuth.value * PI / 180.0

                    // Calculate 3D coordinates based on distance, azimuth, and elevation
                    val x = distanceValue * sin(azimuthRad) * maxRadius
                    val y = distanceValue * cos(azimuthRad) * maxRadius
                    val z =
                        endpoint.position.elevation?.value ?: 0.0f  // Use elevation if available

                    // Calculate projected positions using perspective projection (adjust scale and offset as needed)
                    val perspectiveScale = 200.0f
                    val projectedX = (x / (z + 100.0f)) * perspectiveScale + centerX
                    val projectedY = (y / (z + 100.0f)) * perspectiveScale + centerY

                    // Draw endpoint using perspective projection
                    val endpointColor = ENDPOINT_COLORS[index % ENDPOINT_COLORS.size]

                    // Draw a 3D cube representing the endpoint
                    val cubeSize = 30.0f
                    val halfCubeSize = cubeSize / 2.0f

                    val topLeft = Offset(
                        (projectedX - halfCubeSize).toFloat(),
                        (projectedY - halfCubeSize).toFloat()
                    )
                    val size = androidx.compose.ui.geometry.Size(cubeSize, cubeSize)

                    // Draw front face
                    drawRect(color = Color.Green, topLeft = topLeft, size = size)

                    // Optionally, draw a line connecting the endpoint to the center (origin)
                    drawLine(
                        color = Color.Gray,
                        start = Offset(centerX, centerY),
                        end = Offset(projectedX.toFloat(), projectedY.toFloat()),
                        strokeWidth = 5.0f
                    )
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
        radius = 20.0f
    )

    // Draw arrow (adjust size and position as needed)
    val arrowSize = 15.dp.toPx()
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
        strokeWidth = 3.dp.toPx()
    )
    drawLine(
        color = color,
        start = arrowTip,
        end = arrowTip.plus(
            Offset(
                (arrowSize * 0.5f * cos(angle - PI / 6)).toFloat(),
                (arrowSize * 0.5f * sin(angle - PI / 6)).toFloat()
            )
        ),
        strokeWidth = 3.dp.toPx()
    )
    drawLine(
        color = color,
        start = arrowTip,
        end = arrowTip.plus(
            Offset(
                (arrowSize * 0.5f * cos(angle + PI / 6)).toFloat(),
                (arrowSize * 0.5f * sin(angle + PI / 6)).toFloat()
            )
        ),
        strokeWidth = 3.dp.toPx()
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
    avgDistance: Float,
    avgAzimuth: Float,
    avgElevation: Float,
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
                            modifier = Modifier.width(50.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = contentDescription,
                                tint = Color.Green, // Adjust color for connecting state
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = " ${endpoint.id.split("|")[0]}",
                                textAlign = TextAlign.Center,
                                style = androidx.compose.ui.text.TextStyle(fontSize = 10.sp),
                                color = Color.Black
                            )
                        }
                    }
                }
                Row {
                    connectedEndpoints.forEachIndexed { index, endpoint ->

                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Avg Distance: %.2f".format(avgDistance),
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                    }
                }
                Row {
                    connectedEndpoints.forEachIndexed { index, endpoint ->
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Avg Azimuth: %.2f".format(avgAzimuth),
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                    }

                    connectedEndpoints.forEachIndexed { index, endpoint ->
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Avg Elevation: %.2f".format(avgElevation),
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                    }

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

