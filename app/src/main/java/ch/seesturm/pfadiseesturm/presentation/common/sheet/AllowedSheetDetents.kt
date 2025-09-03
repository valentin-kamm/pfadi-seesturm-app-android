package ch.seesturm.pfadiseesturm.presentation.common.sheet

import com.composables.core.SheetDetent

sealed class AllowedSheetDetents {
    data object MediumOnly: AllowedSheetDetents()
    data object LargeOnly: AllowedSheetDetents()
    data object All: AllowedSheetDetents()

    private val detents: List<ModalBottomSheetDetent>
        get() = when (this) {
            All -> listOf(
                ModalBottomSheetDetent.Hidden,
                ModalBottomSheetDetent.Medium,
                ModalBottomSheetDetent.Large
            )
            LargeOnly -> listOf(
                ModalBottomSheetDetent.Hidden,
                ModalBottomSheetDetent.Large
            )
            MediumOnly -> listOf(
                ModalBottomSheetDetent.Hidden,
                ModalBottomSheetDetent.Medium
            )
        }
    val defaultDetent: ModalBottomSheetDetent
        get() = when (this) {
            All, MediumOnly -> ModalBottomSheetDetent.Medium
            LargeOnly -> ModalBottomSheetDetent.Large
        }
    val sheetDetents: List<SheetDetent>
        get() = this.detents.map { it.sheetDetent }
    val largestDetentMultiplier: Float
        get() = this.detents.maxOf { it.heightMultiplier }
    val mediumDetentMultiplier: Float
        get() = ModalBottomSheetDetent.Medium.heightMultiplier
}