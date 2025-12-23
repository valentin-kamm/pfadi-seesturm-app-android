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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme

@Composable
fun SeesturmSnackbarView(
    data: SnackbarData,
    visuals: SeesturmSnackbarVisuals,
    modifier: Modifier = Modifier
) {
    SeesturmSnackbarContentView(
        snackbar = visuals.type,
        onClick = if (visuals.allowManualDismiss) {
            { data.dismiss() }
        }
        else {
            null
        },
        modifier = modifier
            .padding(16.dp)
    )
}

@Composable
fun SeesturmSnackbarContentView(
    snackbar: SeesturmSnackbar,
    onClick: (() -> Unit)?,
    modifier: Modifier
) {

    CustomCardView(
        shadowColor = Color.Transparent,
        backgroundColor = snackbar.color,
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
                imageVector = snackbar.icon,
                contentDescription = null,
                tint = Color.White
            )
            Text(
                text = snackbar.message,
                maxLines = 3,
                style = MaterialTheme.typography.bodySmall,
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
private fun SnackbarPreview() {
    PfadiSeesturmTheme {
        PfadiSeesturmTheme {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                SeesturmSnackbarContentView(
                    snackbar = SeesturmSnackbar.Success(
                        message = "Erfolg",
                        onDismiss = {},
                        location = SeesturmSnackbarLocation.Default,
                        duration = SnackbarDuration.Long,
                        allowManualDismiss = true
                    ),
                    onClick = null,
                    modifier = Modifier
                        .padding(16.dp)
                )
                SeesturmSnackbarContentView(
                    snackbar = SeesturmSnackbar.Error(
                        message = "Erfolg",
                        onDismiss = {},
                        location = SeesturmSnackbarLocation.Default,
                        duration = SnackbarDuration.Long,
                        allowManualDismiss = true
                    ),
                    onClick = null,
                    modifier = Modifier
                        .padding(16.dp)
                )
                SeesturmSnackbarContentView(
                    snackbar = SeesturmSnackbar.Info(
                        message = "Erfolg",
                        onDismiss = {},
                        location = SeesturmSnackbarLocation.Default,
                        duration = SnackbarDuration.Long,
                        allowManualDismiss = true
                    ),
                    onClick = null,
                    modifier = Modifier
                        .padding(16.dp)
                )
            }
        }
    }
}