package ch.seesturm.pfadiseesturm.data.wordpress.repository

import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WeatherDto
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val api: WordpressApi
): WeatherRepository {
    override suspend fun getWeather(): WeatherDto {
        return api.getWeather()
    }
}