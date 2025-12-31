package ch.seesturm.pfadiseesturm.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonColor
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN

@Composable
fun ErrorCardView(
    errorDescription: String,
    modifier: Modifier = Modifier,
    errorTitle: String = "Ein Fehler ist aufgetreten",
    retryAction: (() -> Unit)? = null
) {
    CustomCardView(
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Feedback,
                contentDescription = null,
                tint = Color.SEESTURM_GREEN,
                modifier = Modifier
                    .size(50.dp)
            )
            Text(
                text = errorTitle,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Text(
                text = errorDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            if (retryAction != null) {
                SeesturmButton(
                    type = SeesturmButtonType.Primary,
                    colors = SeesturmButtonColor.Custom(
                        contentColor = Color.White,
                        buttonColor = Color.SEESTURM_GREEN
                    ),
                    title = "Erneut versuchen",
                    onClick = {
                        retryAction()
                    },
                    isLoading = false
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CardErrorViewPreview1() {
    PfadiSeesturmTheme {
        ErrorCardView(
            errorDescription = "Bitte versuche es erneut Hallihallo. Bitte versuche es erneut. Bitte versuche es erneut.Bitte versuche es erneut",
            retryAction = {

            }
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun CardErrorViewPreview2() {
    PfadiSeesturmTheme {
        ErrorCardView(
            errorDescription = "Bitte versuche es erneut Hallihallo. Bitte versuche es erneut. Bitte versuche es erneut.Bitte versuche es erneut",
            retryAction = null
        )
    }
}