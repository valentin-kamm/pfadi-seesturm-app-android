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
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED

@Composable
fun AuthErrorView(
    message: String,
    onResetAuthState: () -> Unit,
    modifier: Modifier = Modifier
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
                imageVector = Icons.Outlined.PersonOff,
                contentDescription = null,
                tint = Color.SEESTURM_RED,
                modifier = Modifier
                    .size(50.dp)
            )
            Text(
                text = "Beim Anmelden ist ein Fehler aufgetreten",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, hyphens = Hyphens.Auto),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            SeesturmButton(
                type = SeesturmButtonType.Primary(),
                title = "Zurück",
                onClick = onResetAuthState,
                isLoading = false
            )
        }
    }
}

@Preview
@Composable
private fun AuthErrorViewPreview() {
    PfadiSeesturmTheme {
        AuthErrorView(
            message = "Schwerer Fehler",
            onResetAuthState = {}
        )
    }
}