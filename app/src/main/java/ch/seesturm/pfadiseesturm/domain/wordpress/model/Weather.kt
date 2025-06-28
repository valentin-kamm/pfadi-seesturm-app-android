package ch.seesturm.pfadiseesturm.domain.wordpress.model


data class Weather(
    val attributionURL: String,
    val readTimeFormatted: String,
    val daily: DailyWeather,
    val hourly: List<HourlyWeather>
)