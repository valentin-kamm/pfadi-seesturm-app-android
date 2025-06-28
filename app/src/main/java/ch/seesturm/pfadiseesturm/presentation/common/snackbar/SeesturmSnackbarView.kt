package ch.seesturm.pfadiseesturm.presentation.common.snackbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme

@Composable
fun SeesturmSnackbarView(
    snackbarData: SnackbarData,
    event: SeesturmSnackbarEvent,
    modifier: Modifier = Modifier
) {
    SeesturmSnackbarContentView(
        type = event.type,
        onClick = if (event.allowManualDismiss) {
            { snackbarData.dismiss() }
        }
        else null,
        message = event.message,
        modifier = modifier
            .padding(16.dp)
    )
}

@Composable
fun SeesturmSnackbarContentView(
    type: SeesturmSnackbarType,
    onClick: (() -> Unit)?,
    message: String,
    modifier: Modifier
) {

    CustomCardView(
        shadowColor = Color.Transparent,
        backgroundColor = type.color,
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
            modifier = Modifier
                .padding(16.dp)
        ) {
            Icon(
                imageVector = type.icon,
                contentDescription = null,
                tint = Color.White
            )
            Text(
                text = message,
                maxLines = 3,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Preview
@Composable
private fun SeesturmSnackbarViewPreview() {

    val successEvent = SeesturmSnackbarEvent(
        message = "Test Success-Snackbar Test Success-Snackbar Test Success-Snackbar Test Success-Snackbar Test Success-Snackbar Test Success-Snackbar",
        duration = SnackbarDuration.Short,
        type = SeesturmSnackbarType.Success,
        allowManualDismiss = true,
        onDismiss = {},
        showInSheetIfPossible = true
    )
    val infoEvent = SeesturmSnackbarEvent(
        message = "Test Info-Snackbar",
        duration = SnackbarDuration.Short,
        type = SeesturmSnackbarType.Info,
        allowManualDismiss = true,
        onDismiss = {},
        showInSheetIfPossible = true
    )
    val errorEvent = SeesturmSnackbarEvent(
        message = "Test Error-Snackbar",
        duration = SnackbarDuration.Short,
        type = SeesturmSnackbarType.Error,
        allowManualDismiss = true,
        onDismiss = {},
        showInSheetIfPossible = true
    )

    class SnackbarDataExample(
        override val visuals: SnackbarVisuals
    ): SnackbarData {
        override fun dismiss() {
        }
        override fun performAction() {
        }
    }

    PfadiSeesturmTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SeesturmSnackbarView(
                snackbarData = SnackbarDataExample(successEvent),
                event = successEvent
            )
            SeesturmSnackbarView(
                snackbarData = SnackbarDataExample(infoEvent),
                event = infoEvent
            )
            SeesturmSnackbarView(
                snackbarData = SnackbarDataExample(errorEvent),
                event = errorEvent
            )
        }
    }
}