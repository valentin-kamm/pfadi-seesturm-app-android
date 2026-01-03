package ch.seesturm.pfadiseesturm.presentation.mehr.documents.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressDocument
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIcon
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIconType
import ch.seesturm.pfadiseesturm.presentation.common.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemTrailingElementType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.DummyData
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest

@Composable
fun <T>DocumentCardView(
    document: WordpressDocument,
    items: List<T>,
    index: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {

    FormItem(
        items = items,
        index = index,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        trailingElement = FormItemTrailingElementType.Blank,
        mainContent = FormItemContentType.Custom(
            contentPadding = PaddingValues(end = 16.dp),
            content = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(document.thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(75.dp)
                            .wrapContentWidth()
                            .aspectRatio(document.thumbnailWidth.toFloat() / document.thumbnailHeight.toFloat())
                            .clip(RoundedCornerShape(3.dp))
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
                                        imageVector = Icons.Outlined.HideImage,
                                        contentDescription = null,
                                        tint = Color.SEESTURM_GREEN,
                                        modifier = Modifier
                                            .size(25.dp)
                                    )
                                }
                            }
                            is AsyncImagePainter.State.Success -> {
                                SubcomposeAsyncImageContent()
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = document.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, hyphens = Hyphens.Auto),
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                        )
                        TextWithIcon(
                            type = TextWithIconType.Text(
                                text = document.publishedFormatted.uppercase(),
                                textStyle = { MaterialTheme.typography.labelSmall }
                            ),
                            imageVector = Icons.Outlined.CalendarMonth,
                            textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            iconTint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            maxLines = 2,
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        )
    )
}

@Preview
@Composable
private fun DocumentCardViewPreview() {
    PfadiSeesturmTheme {
        DocumentCardView(
            document = DummyData.document1,
            items = listOf(DummyData.document1),
            index = 0
        )
    }
}