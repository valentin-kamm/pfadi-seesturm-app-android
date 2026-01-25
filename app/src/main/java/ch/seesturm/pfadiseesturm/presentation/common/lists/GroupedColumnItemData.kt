package ch.seesturm.pfadiseesturm.presentation.common.lists

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

data class GroupedColumnItemData(
    val key: Any?,
    val onClick: (() -> Unit)?,
    val disableRoundedCorners: Boolean,
    val padding: PaddingValues,
    val content: @Composable () -> Unit
)