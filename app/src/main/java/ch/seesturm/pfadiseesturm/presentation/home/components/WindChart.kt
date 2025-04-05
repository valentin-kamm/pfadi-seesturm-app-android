package ch.seesturm.pfadiseesturm.presentation.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WeatherDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toWeather
import ch.seesturm.pfadiseesturm.domain.wordpress.model.Weather
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_BLUE
import com.google.gson.Gson
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberEnd
import com.patrykandpatrick.vico.compose.cartesian.layer.continuous
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import kotlin.math.max

@Composable
private fun Wind(
    modelProducer: CartesianChartModelProducer,
    maxY: Double,
    modifier: Modifier
) {

    val chartColor = Color.SEESTURM_BLUE
    val decorationColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
    val textStyle = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)

    val rangeProvider = CartesianLayerRangeProvider.fixed(
        minX = 7.0,
        maxX = 19.0,
        minY = 0.0,
        maxY = maxY
    )

    val yAxisValueFormatter = CartesianValueFormatter.decimal(DecimalFormat("# km/h"))
    val xAxisValueFormatter = CartesianValueFormatter.decimal(DecimalFormat("# Uhr"))
    val yAxisItemPlacer = VerticalAxis.ItemPlacer.count(
        count = { 4 }
    )
    val xAxisItemPlacer = HorizontalAxis.ItemPlacer.aligned(
        spacing = { 3 }
    )

    val windLine = LineCartesianLayer.rememberLine(
        stroke = LineCartesianLayer.LineStroke.continuous(5.dp),
        fill = LineCartesianLayer.LineFill.single(fill(chartColor)),
        pointConnector = LineCartesianLayer.PointConnector.cubic(curvature = 0.5f),
        areaFill = LineCartesianLayer.AreaFill.single(
            fill(
                ShaderProvider.verticalGradient(
                    arrayOf(chartColor, Color.Transparent)
                )
            )
        )
    )
    val gustLine = LineCartesianLayer.rememberLine(
        stroke = LineCartesianLayer.LineStroke.continuous(5.dp),
        fill = LineCartesianLayer.LineFill.single(fill(chartColor.copy(alpha = 0.4f))),
        pointConnector = LineCartesianLayer.PointConnector.cubic(curvature = 0.5f)
    )

    val endAxis = VerticalAxis.rememberEnd(
        valueFormatter = yAxisValueFormatter,
        itemPlacer = yAxisItemPlacer,
        guideline = rememberAxisGuidelineComponent(
            fill = fill(decorationColor),
            thickness = 0.5.dp
        ),
        label = TextComponent(
            color = decorationColor.toArgb(),
            textSizeSp = textStyle.fontSize.value
        )
    )
    val bottomAxis = HorizontalAxis.rememberBottom(
        valueFormatter = xAxisValueFormatter,
        itemPlacer = xAxisItemPlacer,
        guideline = rememberAxisGuidelineComponent(
            fill = fill(decorationColor),
            thickness = 0.5.dp,
            shape = Shape.Rectangle
        ),
        label = TextComponent(
            color = decorationColor.toArgb(),
            textSizeSp = textStyle.fontSize.value
        )
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Wind",
                color = decorationColor,
                maxLines = 1,
                style = textStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .weight(1f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .wrapContentWidth()
            ) {
                Canvas(
                    modifier = Modifier
                        .size(10.dp)
                ) {
                    drawCircle(color = chartColor, radius = 10.0f)
                }
                Text(
                    text = "Windgeschwindigkeit",
                    color = decorationColor,
                    maxLines = 1,
                    style = textStyle,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .wrapContentWidth()
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .wrapContentWidth()
            ) {
                Canvas(
                    modifier = Modifier
                        .size(10.dp)
                ) {
                    drawCircle(color = chartColor.copy(alpha = 0.4f), radius = 10.0f)
                }
                Text(
                    text = "Böen",
                    color = decorationColor,
                    maxLines = 1,
                    style = textStyle,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .wrapContentWidth()
                )
            }
        }
        CartesianChartHost(
            modifier = Modifier
                .fillMaxSize(),
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider = LineCartesianLayer.LineProvider.series(
                        lines = listOf(windLine, gustLine)
                    ),
                    rangeProvider = rangeProvider
                ),
                endAxis = endAxis,
                bottomAxis = bottomAxis
            ),
            modelProducer = modelProducer,
            scrollState = rememberVicoScrollState(scrollEnabled = false),
            zoomState = rememberVicoZoomState(zoomEnabled = false, initialZoom = Zoom.Content),
        )
    }
}

@Composable
fun WindChart(
    weather: Weather,
    modifier: Modifier
) {

    val xData = weather.hourly.map { it.forecastStart.hour }
    val yDataWind = weather.hourly.map { it.windSpeed }
    val yDataGust = weather.hourly.map { it.windGust }

    val data = mapOf(
        "Windgeschwindigkeit" to xData.zip(yDataWind).toMap(),
        "Böen" to xData.zip(yDataGust).toMap()
    )

    val maxWind = yDataWind.max()
    val maxGust = yDataGust.max()
    val maxY = max(maxWind, maxGust) * 1.2

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries { data.forEach { (_, map) -> series(map.keys, map.values) } }
        }
    }

    Wind(
        modelProducer,
        maxY = maxY,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun WindChartPreview() {
    val json = """
            {
  "attributionURL": "https://developer.apple.com/weatherkit/data-source-attribution/",
  "readTime": "2025-02-12T16:52:45Z",
  "daily": {
    "forecastStart": "2025-02-15T07:00:00Z",
    "forecastEnd": "2025-02-15T19:00:00Z",
    "conditionCode": "Clear",
    "temperatureMax": 2.04,
    "temperatureMin": -5.35,
    "precipitationAmount": 0,
    "precipitationChance": 0,
    "snowfallAmount": 0,
    "cloudCover": 0.1,
    "humidity": 0.71,
    "windDirection": 101,
    "windSpeed": 4.45,
    "sunrise": "2025-02-15T06:27:57Z",
    "sunset": "2025-02-15T16:46:22Z"
  },
  "hourly": [
    {
      "forecastStart": "2025-02-15T05:00:00Z",
      "cloudCover": 0.42,
      "precipitationType": "clear",
      "precipitationAmount": 0,
      "snowfallAmount": 0,
      "temperature": -5.65,
      "windSpeed": 4.82,
      "windGust": 9.57
    },
    {
      "forecastStart": "2025-02-15T06:00:00Z",
      "cloudCover": 0.38,
      "precipitationType": "clear",
      "precipitationAmount": 0,
      "snowfallAmount": 0,
      "temperature": -5.73,
      "windSpeed": 3.68,
      "windGust": 8.72
    },
    {
      "forecastStart": "2025-02-15T07:00:00Z",
      "cloudCover": 0.32,
      "precipitationType": "clear",
      "precipitationAmount": 0,
      "snowfallAmount": 0,
      "temperature": -5.35,
      "windSpeed": 4.05,
      "windGust": 9.16
    },
    {
      "forecastStart": "2025-02-15T08:00:00Z",
      "cloudCover": 0.21,
      "precipitationType": "clear",
      "precipitationAmount": 0,
      "snowfallAmount": 0,
      "temperature": -3.83,
      "windSpeed": 4.45,
      "windGust": 11.6
    },
    {
      "forecastStart": "2025-02-15T09:00:00Z",
      "cloudCover": 0.08,
      "precipitationType": "clear",
      "precipitationAmount": 0,
      "snowfallAmount": 0,
      "temperature": -2.48,
      "windSpeed": 5.55,
      "windGust": 13.64
    },
    {
      "forecastStart": "2025-02-15T10:00:00Z",
      "cloudCover": 0.04,
      "precipitationType": "clear",
      "precipitationAmount": 0,
      "snowfallAmount": 0,
      "temperature": -1.33,
      "windSpeed": 5.17,
      "windGust": 13.53
    },
    {
      "forecastStart": "2025-02-15T11:00:00Z",
      "cloudCover": 0.06,
      "precipitationType": "clear",
      "precipitationAmount": 0,
      "snowfallAmount": 0,
      "temperature": -0.17,
      "windSpeed": 4.64,
      "windGust": 13.15
    },
    {
      "forecastStart": "2025-02-15T12:00:00Z",
      "cloudCover": 0.09,
      "precipitationType": "clear",
      "precipitationAmount": 0,
      "snowfallAmount": 0,
      "temperature": 0.69,
      "windSpeed": 4.22,
      "windGust": 10.45
    },
    {
      "forecastStart": "2025-02-15T13:00:00Z",
      "cloudCover": 0.08,
      "precipitationType": "clear",
      "precipitationAmount": 0,
      "snowfallAmount": 0,
      "temperature": 1.36,
      "windSpeed": 4.42,
      "windGust": 10.41
    },
    {
      "forecastStart": "2025-02-15T14:00:00Z",
      "cloudCover": 0.04,
      "precipitationType": "clear",
      "precipitationAmount": 0,
      "snowfallAmount": 0,
      "temperature": 1.85,
      "windSpeed": 4.5,
      "windGust": 10.88
    },
    {
      "forecastStart": "2025-02-15T15:00:00Z",
      "cloudCover": 0.02,
      "precipitationType": "clear",
      "precipitationAmount": 0,
      "snowfallAmount": 0,
      "temperature": 2,
      "windSpeed": 5.02,
      "windGust": 12.05
    },
    {
      "forecastStart": "2025-02-15T16:00:00Z",
      "cloudCover": 0.05,
      "precipitationType": "clear",
      "precipitationAmount": 0,
      "snowfallAmount": 0,
      "temperature": 1.19,
      "windSpeed": 3.93,
      "windGust": 9.25
    },
    {
      "forecastStart": "2025-02-15T17:00:00Z",
      "cloudCover": 0.1,
      "precipitationType": "clear",
      "precipitationAmount": 0,
      "snowfallAmount": 0,
      "temperature": -0.29,
      "windSpeed": 3.5,
      "windGust": 6.85
    },
    {
      "forecastStart": "2025-02-15T18:00:00Z",
      "cloudCover": 0.15,
      "precipitationType": "clear",
      "precipitationAmount": 0,
      "snowfallAmount": 0,
      "temperature": -1.33,
      "windSpeed": 3.94,
      "windGust": 6.51
    },
    {
      "forecastStart": "2025-02-15T19:00:00Z",
      "cloudCover": 0.26,
      "precipitationType": "clear",
      "precipitationAmount": 0,
      "snowfallAmount": 0,
      "temperature": -1.89,
      "windSpeed": 3.97,
      "windGust": 5.86
    }
  ]
}
        """.trimIndent()
    val weather = Gson().fromJson(json, WeatherDto::class.java).toWeather()

    val xData = weather.hourly.map { it.forecastStart.hour }
    val yDataWind = weather.hourly.map { it.windSpeed }
    val yDataGust = weather.hourly.map { it.windGust }

    val data = mapOf(
        "Windgeschwindigkeit" to xData.zip(yDataWind).toMap(),
        "Böen" to xData.zip(yDataGust).toMap()
    )

    val maxWind = yDataWind.max()
    val maxGust = yDataGust.max()
    val maxY = max(maxWind, maxGust) * 1.2

    val modelProducer = remember { CartesianChartModelProducer() }
    runBlocking {
        modelProducer.runTransaction {
            lineSeries { data.forEach { (_, map) -> series(map.keys, map.values) } }
        }
    }

    Wind(
        modelProducer,
        maxY = maxY,
        Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}