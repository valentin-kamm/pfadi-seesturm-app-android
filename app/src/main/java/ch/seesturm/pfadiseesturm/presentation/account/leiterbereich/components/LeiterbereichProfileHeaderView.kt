package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.PersonRemove
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenu
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenuItem
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.DummyData

@Composable
fun LeiterbereichProfileHeaderView(
    user: FirebaseHitobitoUser,
    isLoading: Boolean,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier
) {

    var showAccountMenu by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box {
            CircleProfilePictureView(
                user = user,
                size = 40.dp,
                isLoading = isLoading,
                showEditBadge = true,
                onClick = { showAccountMenu = true },
                modifier = Modifier
                    .padding(bottom = 8.dp)
            )
            ThemedDropdownMenu(
                expanded = showAccountMenu,
                onDismissRequest = {
                    showAccountMenu = false
                }
            ) {
                ThemedDropdownMenuItem(
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
                ThemedDropdownMenuItem(
                    text = {
                        Text(text = "App-Account löschen")
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

@Preview(showBackground = true)
@Composable
private fun LeiterbereichProfileHeaderViewPreview1() {
    PfadiSeesturmTheme {
        LeiterbereichProfileHeaderView(
            user = DummyData.user3,
            isLoading = false,
            onSignOut = {},
            onDeleteAccount = {}
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun LeiterbereichProfileHeaderViewPreview2() {
    PfadiSeesturmTheme {
        LeiterbereichProfileHeaderView(
            user = DummyData.user1,
            isLoading = true,
            onSignOut = {},
            onDeleteAccount = {}
        )
    }
}