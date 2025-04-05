package ch.seesturm.pfadiseesturm.presentation.common.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

data class SeesturmSnackbarEvent(
    override val message: String,
    override val duration: SnackbarDuration,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    val type: SnackbarType,
    val allowManualDismiss: Boolean,
    val onDismiss: () -> Unit,
    val showInSheetIfPossible: Boolean
): SnackbarVisuals
