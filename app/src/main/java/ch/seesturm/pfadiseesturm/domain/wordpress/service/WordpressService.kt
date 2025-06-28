package ch.seesturm.pfadiseesturm.domain.wordpress.service

import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmAppError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.delay
import okio.IOException
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

open class WordpressService {

    suspend inline fun <T, R> fetchFromWordpress(
        crossinline fetchAction: suspend () -> T,
        crossinline transform: (T) -> R
    ): SeesturmResult<R, DataError.Network> {

        try {
            val randomDelay = Random.nextLong(Constants.MIN_ARTIFICIAL_DELAY, Constants.MAX_ARTIFICIAL_DELAY)
            delay(randomDelay.milliseconds)
            val response = fetchAction()
            val transformedResponse = transform(response)
            return SeesturmResult.Success(transformedResponse)
        }
        catch (e: retrofit2.HttpException) {
            return SeesturmResult.Error(DataError.Network.INVALID_REQUEST(e.code(), e.message))
        }
        catch (e: JsonSyntaxException) {
            return SeesturmResult.Error(DataError.Network.INVALID_DATA)
        }
        catch (e: IOException) {
            return SeesturmResult.Error(DataError.Network.IO_EXCEPTION)
        }
        catch (e: java.io.IOException) {
            return SeesturmResult.Error(DataError.Network.IO_EXCEPTION)
        }
        catch (e: PfadiSeesturmAppError) {
            return SeesturmResult.Error(
                when (e) {
                    is PfadiSeesturmAppError.DateError -> DataError.Network.INVALID_DATE
                    is PfadiSeesturmAppError.WeatherConditionError -> DataError.Network.INVALID_WEATHER_CONDITION
                    else -> {
                        DataError.Network.UNKNOWN
                    }
                }
            )
        }
        catch (e: Exception) {
            return SeesturmResult.Error(DataError.Network.UNKNOWN)
        }
    }
}