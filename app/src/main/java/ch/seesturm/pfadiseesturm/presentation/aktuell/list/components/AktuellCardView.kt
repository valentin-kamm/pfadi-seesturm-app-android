package ch.seesturm.pfadiseesturm.presentation.aktuell.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPost
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.components.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi


@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun AktuellCardView(
    post: WordpressPost,
    cardAspectRatio: Float = 1.0f,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val hazeState = remember { HazeState() }
    CustomCardView(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
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
                    .haze(hazeState)
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
                    .hazeChild(hazeState, style = CupertinoMaterials.ultraThin())
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = post.titleDecoded,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
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
                            text = post.published.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .alpha(0.4f)
                        )
                    }
                    Text(
                        text = post.contentPlain,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
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

@Preview(showBackground = true)
@Composable
fun AktuellCardViewPreview() {
    PfadiSeesturmTheme {
        AktuellCardView(
            post = WordpressPost(
                id = 1,
                publishedYear = "2024",
                published = "Irgendwann im 21.12.2024",
                modified = "21.12.2024",
                imageUrl = "https://seesturm.ch/wp-content/uploads/2017/11/DSC_4041.sized_.jpg",
                title = "Erinnerung: Anmeldung KaTre noch bis am 1. Juli",
                titleDecoded = "Erinnerung: Anmeldung KaTre noch bis am 1. Juli",
                content = "\n<p>Über 1000 Pfadis aus dem ganzen Thurgau treffen sich jährlich zum Kantonalen Pfaditreffen (KaTre) \u2013 ein Höhepunkt im Kalender der Pfadi Thurgau. Dieses Jahr findet der Anlass erstmals seit 2019 wieder statt, und zwar am Wochenende <strong>vom 23. und 24. September</strong> unter dem Motto <strong>«Die Piraten vom Bodamicus»</strong>.</p>\n\n\n\n<p>Das KaTre 2023 findet ganz in der Nähe statt, nämlich in <strong>Romanshorn direkt am schönen Bodensee</strong>. Es wird von der Pfadi Seesturm, gemeinsam mit den Pfadi-Abteilungen aus Arbon und Romanshorn, organisiert. Die Biber- und Wolfsstufe werden das KaTre am Sonntag besuchen, während die Pfadi- und Piostufe das ganze Wochenende «Pfadi pur» erleben dürfen. Weitere Informationen zum KaTre 2023 findet ihr unter <a rel=\"noreferrer noopener\" href=\"http: //www.katre.ch\" target=\"_blank\">www.katre.ch</a> oder in unserem Mail vom 2. Juni.</p>\n\n\n\n<p>Leider haben wir bisher erst sehr <strong>wenige Anmeldungen</strong> erhalten. Es würde uns sehr freuen, wenn sich noch möglichst viele Seestürmlerinnen und Seestürmler aller Stufen anmelden. Füllt dazu einfach das <a href=\"https: //seesturm.ch/wp-content/uploads/2023/06/KaTre-23__Anmeldetalon.pdf\" target=\"_blank\" rel=\"noreferrer noopener\">Anmeldeformular</a> aus und sendet es <strong>bis am 01. Juli</strong> an <a href=\"mailto: al@seesturm.ch\" target=\"_blank\" rel=\"noreferrer noopener\">al@seesturm.ch</a>.</p>\n\n\n\n<p>Danke!</p>\n",
                contentPlain = "Über 1000 Pfadis aus dem ganzen Thurgau treffen sich jährlich zum Kantonalen Pfaditreffen (KaTre) \u2013 ein Höhepunkt im Kalender der Pfadi Thurgau. Dieses Jahr findet der Anlass erstmals seit 2019 wieder statt, und zwar am Wochenende vom 23. und 24. September unter dem Motto «Die Piraten vom Bodamicus».\n\n\n\nDas KaTre 2023 findet ganz in der Nähe statt, nämlich in Romanshorn direkt am schönen Bodensee. Es wird von der Pfadi Seesturm, gemeinsam mit den Pfadi-Abteilungen aus Arbon und Romanshorn, organisiert. Die Biber- und Wolfsstufe werden das KaTre am Sonntag besuchen, während die Pfadi- und Piostufe das ganze Wochenende «Pfadi pur» erleben dürfen. Weitere Informationen zum KaTre 2023 findet ihr unter www.katre.ch oder in unserem Mail vom 2. Juni.\n\n\n\nLeider haben wir bisher erst sehr wenige Anmeldungen erhalten. Es würde uns sehr freuen, wenn sich noch möglichst viele Seestürmlerinnen und Seestürmler aller Stufen anmelden. Füllt dazu einfach das Anmeldeformular aus und sendet es bis am 01. Juli an al@seesturm.ch.\n\n\n\nDanke!",
                aspectRatio = 425.0/680.0,
                author = "seesturm"
            ),
            modifier = Modifier,
            onClick = {}
        )
    }
}