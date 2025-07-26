package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.domain.wordpress.model.Weather
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import java.time.ZoneId

data class WeatherDto(
    val attributionURL: String,
    val readTime: String,
    val daily: DailyWeatherDto,
    val hourly: List<HourlyWeatherDto>
)

fun WeatherDto.toWeather(): Weather {

    val targetDisplayTimezone = ZoneId.of("Europe/Zurich")

    val readDate = DateTimeUtil.shared.parseIsoDateWithOffset(iso8601DateString = readTime).atZone(targetDisplayTimezone)

    return Weather(
        attributionURL = attributionURL,
        readTimeFormatted = DateTimeUtil.shared.formatDate(
            date = readDate,
            format = "dd.MM.yyyy, HH:dd 'Uhr'",
            type = DateFormattingType.Relative(true)
        ),
        daily = daily.toDailyWeather(),
        hourly = hourly.map { it.toHourlyWeather() }
    )
}