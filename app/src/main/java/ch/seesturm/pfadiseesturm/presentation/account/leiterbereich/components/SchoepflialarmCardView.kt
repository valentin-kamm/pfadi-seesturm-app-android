package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.firestore.model.Schoepflialarm
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIcon
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIconType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.types.SchoepflialarmReactionType

@Composable
fun SchoepflialarmCardView(
    schoepflialarm: Schoepflialarm,
    user: FirebaseHitobitoUser,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomCardView(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                CircleProfilePictureView(
                    user = user,
                    size = 30.dp
                )
                Text(
                    text = schoepflialarm.user?.displayNameShort ?: "Unbekannter Benutzer",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, hyphens = Hyphens.Auto),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Text(
                    text = schoepflialarm.createdFormatted,
                    style = MaterialTheme.typography.labelSmall.copy(hyphens = Hyphens.Auto),
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    modifier = Modifier
                        .alpha(0.4f)
                        .weight(0.5f)
                )
            }
            Text(
                text = schoepflialarm.message,
                style = MaterialTheme.typography.bodyMedium.copy(hyphens = Hyphens.Auto),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                SchoepflialarmReactionType.entries.sortedBy { it.sortingOrder }.forEach { reaction ->
                    CustomCardView(
                        shadowColor = Color.Transparent,
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp)
                        ) {
                            TextWithIcon(
                                imageVector = reaction.icon,
                                type = TextWithIconType.Text(
                                    text = "${schoepflialarm.reactionCount(reaction)}",
                                    textStyle = { MaterialTheme.typography.bodyMedium }
                                ),
                                iconTint = reaction.color
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SchoepflialarmCardViewPreview() {
    PfadiSeesturmTheme {
        SchoepflialarmCardView(
            schoepflialarm = DummyData.schoepflialarm.copy(
                createdFormatted = "Sonntag, 22. Juni, 00:00 Uhr",
                user = DummyData.user1.copy(pfadiname = "Ein ganz langer Pfadiname")
            ),
            user = DummyData.user1,
            onClick = {},
            modifier = Modifier
                .padding(16.dp)
        )
    }
}