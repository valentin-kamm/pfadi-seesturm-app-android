package ch.seesturm.pfadiseesturm.presentation.aktuell.list.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPost
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
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi


@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun AktuellCardView(
    post: WordpressPost,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardAspectRatio: Float = 1.0f
) {
    val hazeState = remember { HazeState() }

    CustomCardView(
        onClick = onClick,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(cardAspectRatio)
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(post.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (Build.VERSION.SDK_INT >= 30) {
                            Modifier
                                .hazeSource(hazeState)
                        } else {
                            Modifier
                        }
                    )
            ) {
                when (painter.state) {
                    is AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer()
                                .customLoadingBlinking()
                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    is AsyncImagePainter.State.Error -> {
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.HideImage,
                                contentDescription = null,
                                tint = Color.SEESTURM_GREEN,
                                modifier = Modifier
                                    .size(50.dp)
                                    .offset(y = 100.dp)
                            )
                        }
                    }
                    is AsyncImagePainter.State.Success -> {
                        SubcomposeAsyncImageContent()
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (Build.VERSION.SDK_INT >= 30) {
                            Modifier
                                .hazeEffect(hazeState, style = CupertinoMaterials.ultraThin())
                                .background(Color.Transparent)
                        } else {
                            Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        }
                    )
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = post.titleDecoded,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, hyphens = Hyphens.Auto),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                    )
                    TextWithIcon(
                        type = TextWithIconType.Text(
                            text = post.publishedFormatted.uppercase(),
                            textStyle = { MaterialTheme.typography.labelSmall }
                        ),
                        imageVector = Icons.Outlined.CalendarMonth,
                        textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        iconTint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        maxLines = 1,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Text(
                        text = post.contentPlain,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                    )
                }
            }
        }
    }
}

@Preview("No image")
@Composable
private fun AktuellCardViewPreview1() {
    PfadiSeesturmTheme {
        AktuellCardView(
            post = DummyData.aktuellPost3,
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
@Preview("Hochformat")
@Composable
private fun AktuellCardViewPreview2() {
    PfadiSeesturmTheme {
        AktuellCardView(
            post = DummyData.aktuellPost2,
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
@Preview("Normal")
@Composable
private fun AktuellCardViewPreview3() {
    PfadiSeesturmTheme {
        AktuellCardView(
            post = DummyData.aktuellPost1,
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}