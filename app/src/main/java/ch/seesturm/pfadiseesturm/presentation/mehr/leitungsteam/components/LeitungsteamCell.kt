package ch.seesturm.pfadiseesturm.presentation.mehr.leitungsteam.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.wordpress.model.LeitungsteamMember
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIcon
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIconType
import ch.seesturm.pfadiseesturm.presentation.common.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.DummyData
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest

@Composable
fun LeitungsteamCell(
    member: LeitungsteamMember,
    modifier: Modifier = Modifier
) {

    CustomCardView(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(member.photo)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(130.dp)
            ) {
                when (painter.state) {
                    AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Loading -> {
                        Box(
                            modifier = Modifier
                                .graphicsLayer()
                                .fillMaxSize()
                                .customLoadingBlinking()
                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    is AsyncImagePainter.State.Error -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = Color.SEESTURM_GREEN,
                                modifier = Modifier
                                    .size(50.dp)
                            )
                        }
                    }
                    is AsyncImagePainter.State.Success -> {
                        SubcomposeAsyncImageContent()
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = member.name,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Text(
                    text = member.job,
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                if (member.contact.isNotEmpty()) {
                    TextWithIcon(
                        type = TextWithIconType.Text(
                            text = member.contact,
                            textStyle = { MaterialTheme.typography.labelMedium }
                        ),
                        imageVector = Icons.Outlined.MailOutline,
                        textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        iconTint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        maxLines = 1,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun LeitungsteamCellPreview() {
    PfadiSeesturmTheme {
        LeitungsteamCell(
            member = DummyData.leitungsteamMember
        )
    }
}