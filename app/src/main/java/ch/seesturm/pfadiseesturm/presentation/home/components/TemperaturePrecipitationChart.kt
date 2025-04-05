package ch.seesturm.pfadiseesturm.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_RED
import com.google.gson.Gson
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberEnd
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.continuous
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.floor

@Composable
private fun TemperaturePrecipitation(
    modelProducer: CartesianChartModelProducer,
    maxYTemperature: Double,
    minYTemperature: Double,
    maxYPrecipitation: Double,
    modifier: Modifier
) {

    val lineColor = Color.SEESTURM_RED
    val columnColor = Color.SEESTURM_BLUE.copy(alpha = 0.8f)
    val decorationColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
    val textStyle = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)

    val temperatureRangeProvider = CartesianLayerRangeProvider.fixed(
        minX = 7.0,
        maxX = 19.0,
        minY = minYTemperature,
        maxY = maxYTemperature
    )
    val precipitationRangeProvider = CartesianLayerRangeProvider.fixed(
        minX = 7.0,
        maxX = 19.0,
        minY = 0.0,
        maxY = maxYPrecipitation
    )

    val endAxisValueFormatter = CartesianValueFormatter.decimal(DecimalFormat("# °"))
    val startAxisValueFormatter = CartesianValueFormatter.decimal(DecimalFormat("# mm"))
    val xAxisValueFormatter = CartesianValueFormatter.decimal(DecimalFormat("# Uhr"))
    val yAxisItemPlacer = VerticalAxis.ItemPlacer.count(
        count = { 4 }
    )
    val xAxisItemPlacer = HorizontalAxis.ItemPlacer.aligned(
        spacing = { 3 }
    )

    val endAxis = VerticalAxis.rememberEnd(
        valueFormatter = endAxisValueFormatter,
        itemPlacer = yAxisItemPlacer,
        guideline = null,
        label = TextComponent(
            color = decorationColor.toArgb(),
            textSizeSp = textStyle.fontSize.value
        )
    )
    val startAxis = VerticalAxis.rememberStart(
        valueFormatter = startAxisValueFormatter,
        itemPlacer = yAxisItemPlacer,
        guideline = null,
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

    val temperatureChart = rememberLineCartesianLayer(
        lineProvider = LineCartesianLayer.LineProvider.series(
            LineCartesianLayer.rememberLine(
                stroke = LineCartesianLayer.LineStroke.continuous(5.dp),
                fill = LineCartesianLayer.LineFill.single(fill(lineColor)),
                pointConnector = LineCartesianLayer.PointConnector.cubic(curvature = 0.5f)
            )
        ),
        rangeProvider = temperatureRangeProvider,
        verticalAxisPosition = Axis.Position.Vertical.End
    )
    val precipitationChart = rememberColumnCartesianLayer(
        columnProvider = ColumnCartesianLayer.ColumnProvider.series(
            rememberLineComponent(
                fill = fill(columnColor),
                thickness = 16.dp
            )
        ),
        rangeProvider = precipitationRangeProvider,
        verticalAxisPosition = Axis.Position.Vertical.Start
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        Text(
            text = "Temperatur und Niederschlag",
            color = decorationColor,
            maxLines = 1,
            style = textStyle,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()

        )
        CartesianChartHost(
            modifier = Modifier
                .fillMaxSize(),
            chart = rememberCartesianChart(
                precipitationChart,
                temperatureChart,
                endAxis = endAxis,
                bottomAxis = bottomAxis,
                startAxis = startAxis
            ),
            modelProducer = modelProducer,
            scrollState = rememberVicoScrollState(scrollEnabled = false),
            zoomState = rememberVicoZoomState(zoomEnabled = false, initialZoom = Zoom.Content)
        )
    }
}

@Composable
fun TemperaturePrecipitationChart(
    weather: Weather,
    modifier: Modifier
) {

    val xData = weather.hourly.map { it.forecastStart.hour }
    val yDataTemperature = weather.hourly.map { it.temperature }
    val yDataPrecipitation = weather.hourly.map { it.precipitationAmount }

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries { series(xData, yDataPrecipitation) }
            lineSeries { series(xData, yDataTemperature) }
        }
    }

    val maxTemp = yDataTemperature.max()
    val minTemp = yDataTemperature.min()
    val tempMaxY = if (maxTemp > 15.0) {
        ceil(maxTemp) + 5
    }
    else {
        15.0
    }
    val tempMinY = if (minTemp > 0.0) {
        0.0
    }
    else {
        floor(minTemp) - 5
    }

    val maxPre = yDataPrecipitation.max()
    val preMaxY = if (maxPre > 5.0) {
        ceil(maxPre) + 5.0
    }
    else {
        5.0
    }

    TemperaturePrecipitation(
        modelProducer = modelProducer,
        maxYTemperature = tempMaxY,
        minYTemperature = tempMinY,
        maxYPrecipitation = preMaxY,
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
private fun TemperaturePrecipitationChartPreview() {
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
      "precipitationAmount": 30,
      "snowfallAmount": 0,
      "temperature": -5.35,
      "windSpeed": 4.05,
      "windGust": 9.16
    },
    {
      "forecastStart": "2025-02-15T08:00:00Z",
      "cloudCover": 0.21,
      "precipitationType": "clear",
      "precipitationAmount": 40,
      "snowfallAmount": 0,
      "temperature": -3.83,
      "windSpeed": 4.45,
      "windGust": 11.6
    },
    {
      "forecastStart": "2025-02-15T09:00:00Z",
      "cloudCover": 0.08,
      "precipitationType": "clear",
      "precipitationAmount": 30,
      "snowfallAmount": 0,
      "temperature": -2.48,
      "windSpeed": 5.55,
      "windGust": 13.64
    },
    {
      "forecastStart": "2025-02-15T10:00:00Z",
      "cloudCover": 0.04,
      "precipitationType": "clear",
      "precipitationAmount": 20,
      "snowfallAmount": 0,
      "temperature": -1.33,
      "windSpeed": 5.17,
      "windGust": 13.53
    },
    {
      "forecastStart": "2025-02-15T11:00:00Z",
      "cloudCover": 0.06,
      "precipitationType": "clear",
      "precipitationAmount": 10,
      "snowfallAmount": 0,
      "temperature": -0.17,
      "windSpeed": 4.64,
      "windGust": 13.15
    },
    {
      "forecastStart": "2025-02-15T12:00:00Z",
      "cloudCover": 0.09,
      "precipitationType": "clear",
      "precipitationAmount": 5,
      "snowfallAmount": 0,
      "temperature": -0.7,
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
    val yDataTemperature = weather.hourly.map { it.temperature }
    val yDataPrecipitation = weather.hourly.map { it.precipitationAmount }

    val modelProducer = remember { CartesianChartModelProducer() }
    runBlocking {
        modelProducer.runTransaction {
            columnSeries { series(xData, yDataPrecipitation) }
            lineSeries { series(xData, yDataTemperature) }
        }
    }

    val maxTemp = yDataTemperature.max()
    val minTemp = yDataTemperature.min()
    val tempMaxY = if (maxTemp > 15.0) {
        ceil(maxTemp) + 5
    }
    else {
        15.0
    }
    val tempMinY = if (minTemp > 0.0) {
        0.0
    }
    else {
        floor(minTemp) - 5
    }

    val maxPre = yDataPrecipitation.max()
    val preMaxY = if (maxPre > 5.0) {
        ceil(maxPre) + 5.0
    }
    else {
        5.0
    }

    TemperaturePrecipitation(
        modelProducer = modelProducer,
        maxYTemperature = tempMaxY,
        minYTemperature = tempMinY,
        maxYPrecipitation = preMaxY,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}