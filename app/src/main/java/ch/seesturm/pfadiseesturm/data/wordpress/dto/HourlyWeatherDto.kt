package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.domain.wordpress.model.HourlyWeather
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import java.time.ZoneId

data class HourlyWeatherDto(
    val forecastStart: String,
    val cloudCover: Double,
    val precipitationType: String,
    val precipitationAmount: Double,
    val snowfallAmount: Double,
    val temperature: Double,
    val windSpeed: Double,
    val windGust: Double
)

fun HourlyWeatherDto.toHourlyWeather(): HourlyWeather {

    val targetDisplayTimezone = ZoneId.of("Europe/Zurich")

    return HourlyWeather(
        forecastStart = DateTimeUtil.shared.parseIsoDateWithOffset(forecastStart).atZone(targetDisplayTimezone),
        cloudCoverPercentage = 100 * cloudCover,
        precipitationType = precipitationType,
        precipitationAmount = precipitationAmount,
        snowfallAmount = snowfallAmount,
        temperature = temperature,
        windSpeed = windSpeed,
        windGust = windGust
    )
}