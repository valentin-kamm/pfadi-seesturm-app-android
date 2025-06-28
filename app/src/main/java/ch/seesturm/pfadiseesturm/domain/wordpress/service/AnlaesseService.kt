package ch.seesturm.pfadiseesturm.domain.wordpress.service

import ch.seesturm.pfadiseesturm.data.wordpress.dto.toGoogleCalendarEvent
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toGoogleCalendarEvents
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvents
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.AnlaesseRepository
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar

class AnlaesseService(
    private val repository: AnlaesseRepository
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
}