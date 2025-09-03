package ch.seesturm.pfadiseesturm.presentation.common.sheet

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.times
import com.composables.core.ModalBottomSheetState

fun ModalBottomSheetState.visibleAreaBottomPadding(density: Density, maxHeight: Dp, detents: AllowedSheetDetents): Dp {

    val currentSheetHeight = with(density) {
        this@visibleAreaBottomPadding.offset.toDp()
    }
    val maxSheetHeight = detents.largestDetentMultiplier * maxHeight
    val mediumSheetHeight = detents.mediumDetentMultiplier * maxHeight

    val padding = when {
        currentSheetHeight < mediumSheetHeight -> maxSheetHeight - mediumSheetHeight
        else -> maxSheetHeight - currentSheetHeight
    }

    return max(0.dp, padding)
}
