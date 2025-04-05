package ch.seesturm.pfadiseesturm.presentation.common.components

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
import ch.seesturm.pfadiseesturm.presentation.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN

@Composable
fun CardErrorView(
    errorTitle: String = "Ein Fehler ist aufgetreten",
    errorDescription: String,
    modifier: Modifier = Modifier,
    retryAction: (() -> Unit)? = null
) {
    CustomCardView(
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
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
                    .padding(top = 16.dp)
            )
            Text(
                text = errorTitle,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Text(
                text = errorDescription,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (retryAction == null) {
                            Modifier
                                .padding(bottom = 16.dp)
                        }
                        else {
                            Modifier
                        }
                    )
            )
            if (retryAction != null) {
                SeesturmButton(
                    type = SeesturmButtonType.Tertiary(),
                    title = "Erneut versuchen",
                    onClick = {
                        retryAction()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardErrorViewPreview() {
    PfadiSeesturmTheme {
        CardErrorView(
            "Ein Fehler ist aufgetreten",
            "Bitte versuche es erneut Hallihallo. Bitte versuche es erneut. Bitte versuche es erneut.Bitte versuche es erneut",
            Modifier
        ) {
            print("X")
        }
    }
}