package fr.eya.uwblink.ui.ranging

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.eya.uwblink.R

@Composable
fun RangingControlIcon(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: (Boolean) -> Unit,
) {
    val selectState = remember { mutableStateOf(selected) }
    val icon = if (selectState.value) R.drawable.ic_start else R.drawable.ic_stop
    val iconColor = if (selectState.value) Color.Red else MaterialTheme.colorScheme.scrim
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val backgroundColor =
        if (selectState.value) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onPrimary
        }
    Surface(
        color = backgroundColor,
        shape = CircleShape,
        border = BorderStroke(2.dp, borderColor),
        modifier = modifier.size(36.dp, 36.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            colorFilter = ColorFilter.tint(iconColor),
            modifier =
            Modifier.padding(4.dp).selectable(selected = selectState.value) {
                selectState.value = !selectState.value
                onClick(selectState.value)
            },
            contentDescription = null // toggleable at higher level
        )
    }
}

@Preview("Off")
@Composable
fun RangingControlButtonOff() {
    RangingControlIcon(selected = false) {}
}

@Preview("On")
@Composable
fun RangingControlButtonOn() {
    RangingControlIcon(selected = true) {}
}