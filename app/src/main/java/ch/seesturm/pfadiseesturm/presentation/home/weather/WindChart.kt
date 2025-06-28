package ch.seesturm.pfadiseesturm.presentation.home.weather

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
import ch.seesturm.pfadiseesturm.domain.wordpress.model.Weather
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_BLUE
import ch.seesturm.pfadiseesturm.util.DummyData
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
import java.text.DecimalFormat
import kotlin.math.max

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

    WindChartContentView(
        modelProducer,
        maxY = maxY,
        modifier = modifier
    )
}

@Composable
private fun WindChartContentView(
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

@Preview(showBackground = true)
@Composable
private fun WindChartPreview() {
    PfadiSeesturmTheme {
        WindChart(
            weather = DummyData.weather,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}