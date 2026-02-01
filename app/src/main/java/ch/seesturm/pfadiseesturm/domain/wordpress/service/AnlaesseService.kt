package ch.seesturm.pfadiseesturm.domain.wordpress.service

import ch.seesturm.pfadiseesturm.data.wordpress.dto.toGoogleCalendarEvent
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toGoogleCalendarEvents
import ch.seesturm.pfadiseesturm.domain.fcf.model.CloudFunctionEventPayload
import ch.seesturm.pfadiseesturm.domain.fcf.model.toCloudFunctionEventPayloadDto
import ch.seesturm.pfadiseesturm.domain.fcf.repository.CloudFunctionsRepository
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvents
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.AnlaesseRepository
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import com.google.gson.JsonSyntaxException
import kotlinx.serialization.SerializationException

class AnlaesseService(
    private val repository: AnlaesseRepository,
    private val cloudFunctionsRepository: CloudFunctionsRepository
): WordpressService() {

    suspend fun fetchEvents(calendar: SeesturmCalendar, includePast: Boolean, maxResults: Int): SeesturmResult<GoogleCalendarEvents, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getEvents(calendar, includePast, maxResults) },
            transform = { it.toGoogleCalendarEvents() }
        )

    suspend fun fetchMoreEvents(calendar: SeesturmCalendar, pageToken: String, maxResults: Int): SeesturmResult<GoogleCalendarEvents, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getEvents(calendar, pageToken, maxResults) },
            transform = { it.toGoogleCalendarEvents() }
        )

    suspend fun getOrFetchEvent(calendar: SeesturmCalendar, eventId: String, cacheIdentifier: MemoryCacheIdentifier): SeesturmResult<GoogleCalendarEvent, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getEvent(calendar, eventId, cacheIdentifier) },
            transform = { it.toGoogleCalendarEvent() }
        )

    suspend fun fetchNextThreeEvents(calendar: SeesturmCalendar): SeesturmResult<List<GoogleCalendarEvent>, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getNextThreeEvents(calendar) },
            transform = { it.toGoogleCalendarEvents().items }
        )

    suspend fun addEvent(event: CloudFunctionEventPayload, calendar: SeesturmCalendar): SeesturmResult<Unit, DataError.CloudFunctionsError> {

        return try {
            val payload = event.toCloudFunctionEventPayloadDto()
            cloudFunctionsRepository.addEvent(
                calendar = calendar,
                event = payload
            )
            SeesturmResult.Success(Unit)
        }
        catch (e: SerializationException) {
            SeesturmResult.Error(DataError.CloudFunctionsError.INVALID_DATA)
        }
        catch (e: JsonSyntaxException) {
            SeesturmResult.Error(DataError.CloudFunctionsError.INVALID_DATA)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.CloudFunctionsError.UNKNOWN(e.localizedMessage ?: "Die Fehlerursache konnte nicht ermittelt werden."))
        }
    }

    suspend fun updateEvent(eventId: String, event: CloudFunctionEventPayload, calendar: SeesturmCalendar): SeesturmResult<Unit, DataError.CloudFunctionsError> {

        return try {
            val payload = event.toCloudFunctionEventPayloadDto()
            cloudFunctionsRepository.updateEvent(
                calendar = calendar,
                eventId = eventId,
                event = payload
            )
            SeesturmResult.Success(Unit)
        } catch (e: SerializationException) {
            SeesturmResult.Error(DataError.CloudFunctionsError.INVALID_DATA)
        } catch (e: JsonSyntaxException) {
            SeesturmResult.Error(DataError.CloudFunctionsError.INVALID_DATA)
        } catch (e: Exception) {
            SeesturmResult.Error(
                DataError.CloudFunctionsError.UNKNOWN(
                    e.localizedMessage ?: "Die Fehlerursache konnte nicht ermittelt werden."
                )
            )
        }
    }
}