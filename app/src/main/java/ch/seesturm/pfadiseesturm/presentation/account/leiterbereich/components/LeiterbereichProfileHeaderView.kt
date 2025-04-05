package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import java.time.ZonedDateTime

@Composable
fun LeiterbereichProfileHeaderView(
    user: FirebaseHitobitoUser,
    isLoading: Boolean,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit
) {

    var showAccountMenu by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box {
            SeesturmButton(
                type = SeesturmButtonType.IconButton(
                    buttonColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = Color.SEESTURM_GREEN,
                    icon = SeesturmButtonIconType.Custom(
                        image = painterResource(R.drawable.logotabbar)
                    )
                ),
                title = null,
                onClick = {
                    showAccountMenu = true
                },
                isLoading = isLoading
            )
            DropdownMenu(
                expanded = showAccountMenu,
                onDismissRequest = {
                    showAccountMenu = false
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(text = "Abmelden")
                    },
                    onClick = {
                        onSignOut()
                        showAccountMenu = false
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = null
                        )
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(text = "Account löschen")
                    },
                    onClick = {
                        onDeleteAccount()
                        showAccountMenu = false
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.PersonRemove,
                            contentDescription = null
                        )
                    }
                )
            }
        }
        Text(
            text = "Willkommen, ${user.displayNameShort}!",
            maxLines = 2,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
        )
        if (user.email != null) {
            Text(
                text = user.email,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun LeiterbereichProfileHeaderViewPreview() {
    LeiterbereichProfileHeaderView(
        user = FirebaseHitobitoUser(
            userId = "123",
            vorname = "Sepp",
            nachname = "Meier",
            pfadiname = "Tarantula",
            email = "sepp.meier@gmail.com",
            created = ZonedDateTime.now(),
            createdFormatted = "",
            modified = ZonedDateTime.now(),
            modifiedFormatted = ""
        ),
        isLoading = false,
        onSignOut = {},
        onDeleteAccount = {}
    )
}