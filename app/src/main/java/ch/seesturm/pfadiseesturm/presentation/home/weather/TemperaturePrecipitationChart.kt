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
import ch.seesturm.pfadiseesturm.domain.wordpress.model.Weather
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_BLUE
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.DummyData
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
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.floor

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

    TemperaturePrecipitationChartContentView(
        modelProducer = modelProducer,
        maxYTemperature = tempMaxY,
        minYTemperature = tempMinY,
        maxYPrecipitation = preMaxY,
        modifier = modifier
    )
}

@Composable
private fun TemperaturePrecipitationChartContentView(
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

@Preview(showBackground = true)
@Composable
private fun TemperaturePrecipitationChartPreview() {
    PfadiSeesturmTheme {
        TemperaturePrecipitationChart(
            weather = DummyData.weather,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}