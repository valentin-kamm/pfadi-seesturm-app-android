package ch.seesturm.pfadiseesturm.presentation.account.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.util.types.SeesturmAuthState

@Composable
fun LoggedOutView(
    authState: SeesturmAuthState,
    onAuthenticate: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomCardView(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logotabbar),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
            )
            Text(
                text = "Login",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, hyphens = Hyphens.Auto),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            )
            Text(
                text = "Melde dich mit MiData an um fortzufahren.",
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            SeesturmButton(
                type = SeesturmButtonType.Primary(
                    icon = SeesturmButtonIconType.Custom(
                        image = painterResource(R.drawable.midata_logo)
                    )
                ),
                title = "Login mit MiData",
                onClick = onAuthenticate,
                isLoading = authState.signInButtonIsLoading,
            )
        }
    }
}
