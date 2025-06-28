package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.domain.wordpress.model.DailyWeather
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import ch.seesturm.pfadiseesturm.util.types.getWeatherCondition
import java.time.ZoneId
import kotlin.math.roundToInt

data class DailyWeatherDto(
    val forecastStart: String,
    val forecastEnd: String,
    val conditionCode: String,
    val temperatureMax: Double,
    val temperatureMin: Double,
    val precipitationAmount: Double,
    val precipitationChance: Double,
    val snowfallAmount: Double,
    val cloudCover: Double,
    val humidity: Double,
    val windDirection: Double,
    val windSpeed: Double,
    val sunrise: String,
    val sunset: String
)

fun DailyWeatherDto.toDailyWeather(): DailyWeather {

    val forecastStartDate = DateTimeUtil.shared.parseIsoDateWithOffset(forecastStart).atZone(ZoneId.systemDefault())
    val forecastEndDate = DateTimeUtil.shared.parseIsoDateWithOffset(forecastEnd).atZone(ZoneId.systemDefault())
    val sunriseDate = DateTimeUtil.shared.parseIsoDateWithOffset(sunrise).atZone(ZoneId.systemDefault())
    val sunsetDate = DateTimeUtil.shared.parseIsoDateWithOffset(sunset).atZone(ZoneId.systemDefault())

    return DailyWeather(
        forecastStart = forecastStartDate,
        forecastEnd = forecastEndDate,
        dayFormatted = DateTimeUtil.shared.formatDate(
            date = forecastStartDate,
            format = "EEEE, dd. MMMM",
            type = DateFormattingType.Relative(withTime = false)
        ),
        weatherCondition = conditionCode.getWeatherCondition,
        temperatureMax = "${temperatureMax.roundToInt()}°",
        temperatureMin = "${temperatureMin.roundToInt()}°",
        precipitationAmount = "${precipitationAmount.roundToInt()} mm",
        precipitationChance = "${100*precipitationChance.roundToInt()} %",
        snowfallAmount = "${snowfallAmount.roundToInt()} mm",
        cloudCover = "${100*cloudCover.roundToInt()} %",
        humidity = "${100*humidity.roundToInt()} %",
        windDirection = convertWindDirection(windDirection),
        windSpeed = "${windSpeed.roundToInt()} km/h",
        sunrise = sunriseDate,
        sunriseFormatted = DateTimeUtil.shared.formatDate(
            date = sunriseDate,
            format = "HH:mm",
            type = DateFormattingType.Absolute
        ),
        sunset = sunsetDate,
        sunsetFormatted = DateTimeUtil.shared.formatDate(
            date = sunsetDate,
            format = "HH:mm",
            type = DateFormattingType.Absolute
        )
    )
}

private fun convertWindDirection(direction: Double): String {
    val ranges = listOf(
        11.25..33.75,
        33.75..56.25,
        56.25..78.75,
        78.75..101.25,
        101.25..123.75,
        123.75..146.25,
        146.25..168.75,
        168.75..191.25,
        191.25..213.75,
        213.75..236.25,
        236.25..258.75,
        258.75..281.25,
        281.25..303.75,
        303.75..326.25,
        326.25..348.75
    )
    val directions = listOf(
        "NNO",
        "NO",
        "ONO",
        "O",
        "OSO",
        "SO",
        "SSO",
        "S",
        "SSW",
        "SW",
        "WSW",
        "W",
        "WNW",
        "NW",
        "NNW"
    )

    for (i in ranges.indices) {
        if (direction in ranges[i]) {
            return directions[i]
        }
    }
    return "N"
}