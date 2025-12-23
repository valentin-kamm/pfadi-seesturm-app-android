package ch.seesturm.pfadiseesturm.presentation.common.sheet

import androidx.compose.ui.unit.times
import com.composables.core.SheetDetent

sealed class ModalBottomSheetDetent {

    data object Medium: ModalBottomSheetDetent()
    data object Large: ModalBottomSheetDetent()
    data object Hidden: ModalBottomSheetDetent()

    private val largeMultiplier = 0.9f
    private val mediumMultiplier = 0.5f
    private val largeIdentifier = "seesturm_large"
    private val mediumIdentifier = "seesturm_medium"

    val heightMultiplier: Float
        get() = when (this) {
            Hidden -> 0.0f
            Medium -> mediumMultiplier
            Large -> largeMultiplier
        }

    val sheetDetent: SheetDetent
        get() = when (this) {
            Hidden -> SheetDetent.Hidden
            Medium -> {
                SheetDetent(mediumIdentifier) { containerHeight, _ ->
                    mediumMultiplier * containerHeight
                }
            }
            Large -> {
                SheetDetent(largeIdentifier) { containerHeight, _ ->
                    largeMultiplier * containerHeight
                }
            }
        }
}