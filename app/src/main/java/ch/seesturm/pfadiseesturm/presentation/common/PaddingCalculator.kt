package ch.seesturm.pfadiseesturm.presentation.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

fun PaddingValues.intersectWith(
    other: PaddingValues,
    layoutDirection: LayoutDirection,
    additionalTopPadding: Dp = 0.dp,
    additionalBottomPadding: Dp = 0.dp,
    additionalStartPadding: Dp = 0.dp,
    additionalEndPadding: Dp = 0.dp
): PaddingValues {
    return PaddingValues(
        start = maxOf(this.calculateStartPadding(layoutDirection), other.calculateStartPadding(layoutDirection)) + additionalStartPadding,
        top = maxOf(this.calculateTopPadding(), other.calculateTopPadding()) + additionalTopPadding,
        end = maxOf(this.calculateEndPadding(layoutDirection), other.calculateEndPadding(layoutDirection)) + additionalEndPadding,
        bottom = maxOf(this.calculateBottomPadding(), other.calculateBottomPadding()) + additionalBottomPadding
    )
}