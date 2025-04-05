package ch.seesturm.pfadiseesturm.presentation.common.snackbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView

@Composable
fun SeesturmSnackbarView(
    snackbarData: SnackbarData,
    event: SeesturmSnackbarEvent
) {
    CustomCardView(
        shadowColor = Color.Transparent,
        backgroundColor = event.type.color,
        modifier = Modifier
            .padding(16.dp)
            .then(
                if (event.allowManualDismiss) {
                    Modifier.clickable {
                        snackbarData.dismiss()
                    }
                }
                else {
                    Modifier
                }
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
            modifier = Modifier
                .padding(16.dp)
        ) {
            Icon(
                imageVector = event.type.icon,
                contentDescription = null,
                tint = Color.White
            )
            Text(
                text = event.message,
                maxLines = 3,
                style = MaterialTheme.typography.labelLarge,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}