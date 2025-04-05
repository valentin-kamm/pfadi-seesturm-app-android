package ch.seesturm.pfadiseesturm.domain.wordpress.repository

import ch.seesturm.pfadiseesturm.data.wordpress.dto.WeatherDto

interface WeatherRepository {
    suspend fun getWeather(): WeatherDto
}