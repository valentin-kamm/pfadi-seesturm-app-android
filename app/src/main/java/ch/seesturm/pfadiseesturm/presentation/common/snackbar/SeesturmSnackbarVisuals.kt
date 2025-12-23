package ch.seesturm.pfadiseesturm.presentation.common.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

data class SeesturmSnackbarVisuals(
    override val message: String,
    override val duration: SnackbarDuration,
    val type: SeesturmSnackbar,
    val allowManualDismiss: Boolean,
    val onDismiss: () -> Unit,
    val location: SeesturmSnackbarLocation,
    override val actionLabel: String?,
    override val withDismissAction: Boolean
): SnackbarVisuals
