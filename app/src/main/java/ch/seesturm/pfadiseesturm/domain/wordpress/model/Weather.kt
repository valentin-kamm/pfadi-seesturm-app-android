package ch.seesturm.pfadiseesturm.domain.wordpress.model

import ch.seesturm.pfadiseesturm.util.WeatherCondition
import java.time.ZonedDateTime

data class Weather(
    val attributionURL: String,
    val readTimeFormatted: String,
    val daily: DailyWeather,
    val hourly: List<HourlyWeather>
)
data class DailyWeather(
    var forecastStart: ZonedDateTime,
    var forecastEnd: ZonedDateTime,
    var dayFormatted: String,
    var weatherCondition: WeatherCondition,
    var temperatureMax: String,
    var temperatureMin: String,
    var precipitationAmount: String,
    var precipitationChance: String,
    var snowfallAmount: String,
    var cloudCover: String,
    var humidity: String,
    var windDirection: String,
    var windSpeed: String,
    var sunrise: ZonedDateTime,
    var sunriseFormatted: String,
    var sunset: ZonedDateTime,
    var sunsetFormatted: String

)
data class HourlyWeather(
    var forecastStart: ZonedDateTime,
    var cloudCover: Double,
    var precipitationType: String,
    var precipitationAmount: Double,
    var snowfallAmount: Double,
    var temperature: Double,
    var windSpeed: Double,
    var windGust: Double,
)