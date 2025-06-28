package ch.seesturm.pfadiseesturm.domain.wordpress.model

import java.time.ZonedDateTime

data class HourlyWeather(
    var forecastStart: ZonedDateTime,
    var cloudCoverPercentage: Double,
    var precipitationType: String,
    var precipitationAmount: Double,
    var snowfallAmount: Double,
    var temperature: Double,
    var windSpeed: Double,
    var windGust: Double,
)