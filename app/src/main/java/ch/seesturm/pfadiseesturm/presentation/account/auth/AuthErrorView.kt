package ch.seesturm.pfadiseesturm.presentation.account.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonOff
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
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_RED

@Composable
fun AuthErrorView(
    message: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomCardView(
        modifier = modifier
            .padding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.PersonOff,
                contentDescription = null,
                tint = Color.SEESTURM_RED,
                modifier = Modifier
                    .size(50.dp)
            )
            Text(
                text = "Beim Anmelden ist ein Fehler aufgetreten",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            SeesturmButton(
                type = SeesturmButtonType.Primary(),
                title = "Zurück",
                onClick = {
                    onButtonClick()
                }
            )
        }
    }
}

@Preview
@Composable
private fun AuthErrorViewPreview() {
    AuthErrorView(
        message = "Irgend ein Fehler",
        onButtonClick = {}
    )
}