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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressDocument
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemTertiaryElementType
import ch.seesturm.pfadiseesturm.presentation.common.components.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest

@Composable
fun <T>DokumenteLuuchtturmCell(
    document: WordpressDocument,
    items: List<T>,
    index: Int,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    FormItem(
        items = items,
        index = index,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        trailingElement = FormItemTertiaryElementType.DisclosureIndicator,
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
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                        ) {
                            Icon(
                                Icons.Outlined.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .size(
                                        with(LocalDensity.current) {
                                            MaterialTheme.typography.labelSmall.lineHeight.toPx().toDp()
                                        }
                                    )
                                    .alpha(0.4f)
                            )
                            Text(
                                text = document.published.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .alpha(0.4f)
                            )
                        }
                    }
                }
            }
        )
    )
}

@Preview
@Composable
fun DokumenteLuuchtturmCellPreview() {
    DokumenteLuuchtturmCell(
        document = WordpressDocument(
            id = "123",
            thumbnailUrl = "https://seesturm.ch/wp-content/uploads/2022/04/190404_Infobroschuere-Pfadi-Thurgau-pdf-212x300.jpg",
            thumbnailWidth = 212,
            thumbnailHeight = 300,
            title = "Infobroschüre Pfadi Thurgau",
            url = "https://seesturm.ch/wp-content/uploads/2022/04/190404_Infobroschuere-Pfadi-Thurgau.pdf",
            published = "test 2022-04-22T13:26:20+00:00"
        ),
        items = listOf(
            WordpressDocument(
                id = "123",
                thumbnailUrl = "https://seesturm.ch/wp-content/uploads/2022/04/190404_Infobroschuere-Pfadi-Thurgau-pdf-212x300.jpg",
                thumbnailWidth = 212,
                thumbnailHeight = 300,
                title = "Infobroschüre Pfadi Thurgau",
                url = "https://seesturm.ch/wp-content/uploads/2022/04/190404_Infobroschuere-Pfadi-Thurgau.pdf",
                published = "test 2022-04-22T13:26:20+00:00"
            )
        ),
        index = 0
    )
}