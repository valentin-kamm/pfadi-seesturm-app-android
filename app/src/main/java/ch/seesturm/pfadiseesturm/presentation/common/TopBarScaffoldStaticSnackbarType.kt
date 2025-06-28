package ch.seesturm.pfadiseesturm.presentation.common

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent

sealed class TopBarScaffoldStaticSnackbarType {
    data object None: TopBarScaffoldStaticSnackbarType()
    data class Show(
        val snackbarEvent: SeesturmSnackbarEvent,
        val additionalBottomPadding: Dp = 0.dp
    ): TopBarScaffoldStaticSnackbarType()
}