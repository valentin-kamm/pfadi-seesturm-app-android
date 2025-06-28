package ch.seesturm.pfadiseesturm.domain.wordpress.service

import ch.seesturm.pfadiseesturm.data.wordpress.dto.toWeather
import ch.seesturm.pfadiseesturm.domain.wordpress.model.Weather
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.WeatherRepository
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult

class WeatherService(
    private val repository: WeatherRepository
): WordpressService() {

    suspend fun getWeather(): SeesturmResult<Weather, DataError.Network> {
        val result = fetchFromWordpress(
            fetchAction = { repository.getWeather() },
            transform = { it.toWeather() }
        )
        return when (result) {
            is SeesturmResult.Success -> {
                if (result.data.hourly.isEmpty()) {
                    SeesturmResult.Error(DataError.Network.INVALID_DATA)
                }
                else {
                    result
                }
            }
            is SeesturmResult.Error -> {
                result
            }
        }
    }
}