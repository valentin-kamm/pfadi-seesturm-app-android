package ch.seesturm.pfadiseesturm.presentation.common.snackbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_BLUE
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED

enum class SeesturmSnackbarType {
    Error,
    Info,
    Success
}

@get:Composable
val SeesturmSnackbarType.color: Color
    get() = when (this) {
        SeesturmSnackbarType.Error -> { Color.SEESTURM_RED }
        SeesturmSnackbarType.Info -> { Color.SEESTURM_BLUE }
        SeesturmSnackbarType.Success -> { Color.SEESTURM_GREEN }
    }
@get:Composable
val SeesturmSnackbarType.icon: ImageVector
    get() = when (this) {
        SeesturmSnackbarType.Error -> { Icons.Outlined.Cancel }
        SeesturmSnackbarType.Info -> { Icons.Outlined.Info }
        SeesturmSnackbarType.Success -> { Icons.Outlined.CheckCircle }
    }