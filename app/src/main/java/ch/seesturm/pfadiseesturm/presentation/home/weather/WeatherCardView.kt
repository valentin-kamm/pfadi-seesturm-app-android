package ch.seesturm.pfadiseesturm.presentation.home.weather

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.domain.wordpress.model.Weather
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIcon
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIconType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.launchWebsite

@Composable
fun WeatherCardView(
    weather: Weather,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 3 }
    )

    val context = LocalContext.current

    Column (
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        CustomCardView {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = weather.daily.dayFormatted,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                        )
                        TextWithIcon(
                            type = TextWithIconType.Text(
                                text = "Pfadiheim Bergbrücke",
                                textStyle = { MaterialTheme.typography.bodyMedium }
                            ),
                            imageVector = Icons.Outlined.LocationOn,
                            textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            iconTint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            maxLines = 1,
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val annotatedString = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append(weather.daily.temperatureMin + " ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.Thin
                                    )
                                ) {
                                    append("|")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append(" " + weather.daily.temperatureMax)
                                }
                            }
                            Text(
                                annotatedString,
                                style = MaterialTheme.typography.displaySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                        Text(
                            text = weather.daily.weatherCondition.description,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                        )
                    }
                    Image(
                        painter = painterResource(
                            if (isDarkTheme) {
                                weather.daily.weatherCondition.darkIconId
                            } else {
                                weather.daily.weatherCondition.lightIconId
                            }
                        ),
                        contentScale = ContentScale.Fit,
                        contentDescription = null,
                        modifier = Modifier
                            .wrapContentSize()
                            .width(100.dp)
                            .aspectRatio(1f)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {
                    CustomCardView(
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        shadowColor = Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        TextWithIcon(
                            type = TextWithIconType.Text(
                                text = weather.daily.precipitationAmount,
                                textStyle = { MaterialTheme.typography.bodySmall }
                            ),
                            imageVector = Icons.Outlined.WaterDrop,
                            textColor = MaterialTheme.colorScheme.onBackground,
                            iconTint = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        )
                    }
                    CustomCardView(
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        shadowColor = Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        TextWithIcon(
                            type = TextWithIconType.Text(
                                text = weather.daily.windSpeed,
                                textStyle = { MaterialTheme.typography.bodySmall }
                            ),
                            imageVector = Icons.Outlined.Air,
                            textColor = MaterialTheme.colorScheme.onBackground,
                            iconTint = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        )
                    }
                    CustomCardView(
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        shadowColor = Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        TextWithIcon(
                            type = TextWithIconType.Text(
                                text = weather.daily.cloudCover,
                                textStyle = { MaterialTheme.typography.bodySmall }
                            ),
                            imageVector = Icons.Filled.Cloud,
                            textColor = MaterialTheme.colorScheme.onBackground,
                            iconTint = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        )
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    pageSize = PageSize.Fill,
                    pageSpacing = 32.dp,
                    verticalAlignment = Alignment.Top,
                    userScrollEnabled = true,
                    reverseLayout = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp)
                ) { page ->
                    CustomCardView(
                        shadowColor = Color.Transparent,
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        when (page) {
                            0 -> {
                                TemperaturePrecipitationChart(
                                    weather = weather,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                )
                            }

                            1 -> {
                                CloudCoverChart(
                                    weather = weather,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                )
                            }

                            else -> {
                                WindChart(
                                    weather = weather,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color = if (pagerState.currentPage == iteration) {
                            MaterialTheme.colorScheme.onBackground
                        } else {
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        }
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(8.dp)
                        )
                    }
                }
            }
        }
        Icon(
            painter = painterResource(R.drawable.icon_apple_weather),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            modifier = Modifier
                .width(60.dp)
                .height(25.dp)
                .clickable {
                    launchWebsite(
                        context = context,
                        url = weather.attributionURL
                    )
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherCardViewPreview() {
    PfadiSeesturmTheme {
        WeatherCardView(
            weather = DummyData.weather,
            isDarkTheme = false
        )
    }
}