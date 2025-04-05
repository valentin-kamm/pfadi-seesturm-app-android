package ch.seesturm.pfadiseesturm.presentation.common.snackbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_BLUE
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_RED

enum class SnackbarType {
    Error,
    Info,
    Success
}

@get:Composable
val SnackbarType.color: Color
    get() = when (this) {
        SnackbarType.Error -> { Color.SEESTURM_RED }
        SnackbarType.Info -> { Color.SEESTURM_BLUE }
        SnackbarType.Success -> { Color.SEESTURM_GREEN }
    }
@get:Composable
val SnackbarType.icon: ImageVector
    get() = when (this) {
        SnackbarType.Error -> { Icons.Outlined.Cancel }
        SnackbarType.Info -> { Icons.Outlined.Info }
        SnackbarType.Success -> { Icons.Outlined.CheckCircle }
    }