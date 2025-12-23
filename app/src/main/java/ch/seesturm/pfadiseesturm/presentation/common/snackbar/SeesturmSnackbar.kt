package ch.seesturm.pfadiseesturm.presentation.common.snackbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_BLUE
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED

sealed interface SeesturmSnackbar {

    val message: String
    val onDismiss: () -> Unit
    val location: SeesturmSnackbarLocation
    val allowManualDismiss: Boolean
    val duration: SnackbarDuration

    data class Error(
        override val message: String,
        override val onDismiss: () -> Unit,
        override val location: SeesturmSnackbarLocation,
        override val allowManualDismiss: Boolean,
        override val duration: SnackbarDuration = SnackbarDuration.Short
    ): SeesturmSnackbar
    data class Info(
        override val message: String,
        override val onDismiss: () -> Unit,
        override val location: SeesturmSnackbarLocation,
        override val allowManualDismiss: Boolean,
        override val duration: SnackbarDuration = SnackbarDuration.Short
    ): SeesturmSnackbar
    data class Success(
        override val message: String,
        override val onDismiss: () -> Unit,
        override val location: SeesturmSnackbarLocation,
        override val allowManualDismiss: Boolean,
        override val duration: SnackbarDuration = SnackbarDuration.Short
    ): SeesturmSnackbar

    val visuals: SeesturmSnackbarVisuals
        get() = SeesturmSnackbarVisuals(
            message = message,
            duration = duration,
            type = this,
            allowManualDismiss = allowManualDismiss,
            onDismiss = onDismiss,
            location = location,
            actionLabel = null,
            withDismissAction = false
        )

    @get:Composable
    val color: Color
        get() = when (this) {
            is Error -> Color.SEESTURM_RED
            is Info -> Color.SEESTURM_BLUE
            is Success -> Color.SEESTURM_GREEN
        }

    @get:Composable
    val icon: ImageVector
        get() = when (this) {
            is Error -> Icons.Outlined.Cancel
            is Info -> Icons.Outlined.Info
            is Success -> Icons.Outlined.CheckCircle
        }
}