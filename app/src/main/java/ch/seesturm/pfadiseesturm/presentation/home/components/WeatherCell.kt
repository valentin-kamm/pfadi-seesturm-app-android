package ch.seesturm.pfadiseesturm.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WeatherDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toWeather
import ch.seesturm.pfadiseesturm.domain.wordpress.model.Weather
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView
import com.google.gson.Gson

@Composable
fun WeatherCell(
    weather: Weather,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 3 }
    )
    
    CustomCardView(
        modifier = modifier
    ) {
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
                    ) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .size(
                                    with(LocalDensity.current) {
                                        MaterialTheme.typography.bodyMedium.lineHeight.toPx().toDp()
                                    }
                                )
                                .alpha(0.4f)
                        )
                        Text(
                            text = "Pfadiheim Bergbrücke",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                                .alpha(0.4f)
                        )
                    }
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
                            style = MaterialTheme.typography.displayMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                    Text(
                        text = weather.daily.weatherCondition.description,
                        maxLines = 1,
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
                        if (isSystemInDarkTheme()) {
                            weather.daily.weatherCondition.darkIconId
                        }
                        else {
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Outlined.WaterDrop,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .size(
                                    with(LocalDensity.current) {
                                        MaterialTheme.typography.bodySmall.lineHeight.toPx().toDp()
                                    }
                                )
                        )
                        Text(
                            text = weather.daily.precipitationAmount,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .background(Color.Transparent)
                        )
                    }
                }
                CustomCardView(
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    shadowColor = Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Outlined.Air,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .size(
                                    with(LocalDensity.current) {
                                        MaterialTheme.typography.bodySmall.lineHeight.toPx().toDp()
                                    }
                                )
                        )
                        Text(
                            text = weather.daily.windSpeed,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .background(Color.Transparent)
                        )
                    }
                }
                CustomCardView(
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    shadowColor = Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Filled.Cloud,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .size(
                                    with(LocalDensity.current) {
                                        MaterialTheme.typography.bodySmall.lineHeight.toPx().toDp()
                                    }
                                )
                        )
                        Text(
                            text = weather.daily.cloudCover,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .background(Color.Transparent)
                        )
                    }
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
                    shadowColor = MaterialTheme.colorScheme.onBackground,
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
                                    .padding(16.dp)
                            )
                        }
                        1 -> {
                            CloudCoverChart(
                                weather = weather,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            )
                        }
                        else -> {
                            WindChart(
                                weather = weather,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
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
}

@Preview
@Composable
fun WeatherCellPreview() {
    val json = """
            {
              "attributionURL": "https://developer.apple.com/weatherkit/data-source-attribution/",
              "readTime": "2025-01-26T12:15:11Z",
              "daily": {
                "forecastStart": "2025-02-01T07:00:00Z",
                "forecastEnd": "2025-02-01T19:00:00Z",
                "conditionCode": "Clear",
                "temperatureMax": 4.41,
                "temperatureMin": 0.93,
                "precipitationAmount": 0,
                "precipitationChance": 0,
                "snowfallAmount": 0,
                "cloudCover": 0.81,
                "humidity": 0.81,
                "windDirection": 298,
                "windSpeed": 7.16,
                "sunrise": "2025-02-01T06:48:49Z",
                "sunset": "2025-02-01T16:24:22Z"
              },
              "hourly": [
                {
                  "forecastStart": "2025-02-01T05:00:00Z",
                  "cloudCover": 0.81,
                  "precipitationType": "snow",
                  "precipitationAmount": 0.09,
                  "snowfallAmount": 0.84,
                  "temperature": 0.53,
                  "windSpeed": 6.13,
                  "windGust": 10.9
                },
                {
                  "forecastStart": "2025-02-01T06:00:00Z",
                  "cloudCover": 0.79,
                  "precipitationType": "snow",
                  "precipitationAmount": 0.1,
                  "snowfallAmount": 0.98,
                  "temperature": 0.62,
                  "windSpeed": 6.43,
                  "windGust": 11.13
                },
                {
                  "forecastStart": "2025-02-01T07:00:00Z",
                  "cloudCover": 0.77,
                  "precipitationType": "snow",
                  "precipitationAmount": 0.1,
                  "snowfallAmount": 0.97,
                  "temperature": 0.93,
                  "windSpeed": 6.54,
                  "windGust": 11.26
                },
                {
                  "forecastStart": "2025-02-01T08:00:00Z",
                  "cloudCover": 0.76,
                  "precipitationType": "snow",
                  "precipitationAmount": 0.1,
                  "snowfallAmount": 0.93,
                  "temperature": 1.45,
                  "windSpeed": 6.54,
                  "windGust": 11.41
                },
                {
                  "forecastStart": "2025-02-01T09:00:00Z",
                  "cloudCover": 0.76,
                  "precipitationType": "snow",
                  "precipitationAmount": 0.09,
                  "snowfallAmount": 0.8,
                  "temperature": 2.1,
                  "windSpeed": 6.62,
                  "windGust": 11.84
                },
                {
                  "forecastStart": "2025-02-01T10:00:00Z",
                  "cloudCover": 0.75,
                  "precipitationType": "snow",
                  "precipitationAmount": 0.06,
                  "snowfallAmount": 0.49,
                  "temperature": 2.87,
                  "windSpeed": 6.91,
                  "windGust": 12.75
                },
                {
                  "forecastStart": "2025-02-01T11:00:00Z",
                  "cloudCover": 0.76,
                  "precipitationType": "snow",
                  "precipitationAmount": 0.02,
                  "snowfallAmount": 0.17,
                  "temperature": 3.59,
                  "windSpeed": 7.26,
                  "windGust": 13.81
                },
                {
                  "forecastStart": "2025-02-01T12:00:00Z",
                  "cloudCover": 0.76,
                  "precipitationType": "clear",
                  "precipitationAmount": 0,
                  "snowfallAmount": 0,
                  "temperature": 4.09,
                  "windSpeed": 7.52,
                  "windGust": 14.5
                },
                {
                  "forecastStart": "2025-02-01T13:00:00Z",
                  "cloudCover": 0.78,
                  "precipitationType": "clear",
                  "precipitationAmount": 0,
                  "snowfallAmount": 0,
                  "temperature": 4.34,
                  "windSpeed": 7.68,
                  "windGust": 14.62
                },
                {
                  "forecastStart": "2025-02-01T14:00:00Z",
                  "cloudCover": 0.8,
                  "precipitationType": "clear",
                  "precipitationAmount": 0,
                  "snowfallAmount": 0,
                  "temperature": 4.41,
                  "windSpeed": 7.79,
                  "windGust": 14.45
                },
                {
                  "forecastStart": "2025-02-01T15:00:00Z",
                  "cloudCover": 0.83,
                  "precipitationType": "clear",
                  "precipitationAmount": 0,
                  "snowfallAmount": 0,
                  "temperature": 4.26,
                  "windSpeed": 7.8,
                  "windGust": 14.16
                },
                {
                  "forecastStart": "2025-02-01T16:00:00Z",
                  "cloudCover": 0.87,
                  "precipitationType": "clear",
                  "precipitationAmount": 0,
                  "snowfallAmount": 0,
                  "temperature": 3.97,
                  "windSpeed": 7.56,
                  "windGust": 13.72
                },
                {
                  "forecastStart": "2025-02-01T17:00:00Z",
                  "cloudCover": 0.9,
                  "precipitationType": "clear",
                  "precipitationAmount": 0,
                  "snowfallAmount": 0,
                  "temperature": 3.6,
                  "windSpeed": 7.11,
                  "windGust": 13.15
                },
                {
                  "forecastStart": "2025-02-01T18:00:00Z",
                  "cloudCover": 0.93,
                  "precipitationType": "clear",
                  "precipitationAmount": 0,
                  "snowfallAmount": 0,
                  "temperature": 3.29,
                  "windSpeed": 6.66,
                  "windGust": 12.75
                }
              ]
            }
        """.trimIndent()
    WeatherCell(
        weather = Gson().fromJson(json, WeatherDto::class.java).toWeather()
    )
}