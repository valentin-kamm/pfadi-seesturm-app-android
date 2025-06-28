package ch.seesturm.pfadiseesturm.presentation.home.weather


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
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.DummyData
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

@Composable
fun CloudCoverChart(
    weather: Weather,
    modifier: Modifier
) {

    val xData = weather.hourly.map { it.forecastStart.hour }
    val yData = weather.hourly.map { it.cloudCoverPercentage }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries { series(xData, yData) }
        }
    }

    CloudCoverChartContentView(
        modelProducer,
        modifier
    )
}

@Composable
private fun CloudCoverChartContentView(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier
) {

    val chartColor = MaterialTheme.colorScheme.onBackground
    val decorationColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
    val textStyle = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)

    val rangeProvider = CartesianLayerRangeProvider.fixed(
        minY = 0.0,
        maxY = 100.0,
        minX = 7.0,
        maxX = 19.0
    )

    val yAxisValueFormatter = CartesianValueFormatter.yPercent()
    val xAxisValueFormatter = CartesianValueFormatter.decimal(DecimalFormat("# Uhr"))
    val yAxisItemPlacer = VerticalAxis.ItemPlacer.step(
        step = { 25.0 }
    )
    val xAxisItemPlacer = HorizontalAxis.ItemPlacer.aligned(
        spacing = { 3 }
    )

    val line = LineCartesianLayer.rememberLine(
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

    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        Text(
            text = "Bewölkung",
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
                rememberLineCartesianLayer(
                    lineProvider = LineCartesianLayer.LineProvider.series(
                        line
                    ),
                    rangeProvider = rangeProvider
                ),
                endAxis = endAxis,
                bottomAxis = bottomAxis
            ),
            modelProducer = modelProducer,
            scrollState = rememberVicoScrollState(scrollEnabled = false),
            zoomState = rememberVicoZoomState(zoomEnabled = false, initialZoom = Zoom.Content)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CloudCoverChartPreview() {
    PfadiSeesturmTheme {
        CloudCoverChart(
            weather = DummyData.weather,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}