package ch.seesturm.pfadiseesturm.data.wordpress.repository

import ch.seesturm.pfadiseesturm.util.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventsDto
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.AnlaesseRepository
import ch.seesturm.pfadiseesturm.util.SeesturmCalendar
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date

class AnlaesseRepositoryImpl(
    private val api: WordpressApi
): AnlaesseRepository {

    override var eventListMemoryCache: MutableMap<SeesturmCalendar, List<GoogleCalendarEventDto>> = mutableMapOf()
    override var nextEventsMemoryCache: MutableMap<SeesturmCalendar, List<GoogleCalendarEventDto>> = mutableMapOf()

    override suspend fun getEvents(
        calendar: SeesturmCalendar,
        includePast: Boolean,
        maxResults: Int
    ): GoogleCalendarEventsDto {
        val response = api.getEvents(
            calendarId = calendar.calendarId,
            includePast = includePast,
            maxResults = maxResults
        )
        eventListMemoryCache[calendar] = response.items
        return response
    }

    override suspend fun getEvents(
        calendar: SeesturmCalendar,
        pageToken: String,
        maxResults: Int
    ): GoogleCalendarEventsDto {
        val response = api.getEvents(calendarId = calendar.calendarId, pageToken = pageToken, maxResults = maxResults)
        val newMapContent = eventListMemoryCache[calendar]?.plus(response.items) ?: emptyList()
        eventListMemoryCache[calendar] = newMapContent
        return response
    }

    override suspend fun getEvents(
        calendar: SeesturmCalendar,
        timeMin: Instant
    ): GoogleCalendarEventsDto {
        val response = api.getEvents(calendarId = calendar.calendarId, timeMin = timeMin)
        val newMapContent = eventListMemoryCache[calendar]?.plus(response.items) ?: emptyList()
        eventListMemoryCache[calendar] = newMapContent
        return response
    }

    override suspend fun getEvent(calendar: SeesturmCalendar, eventId: String, cacheIdentifier: MemoryCacheIdentifier): GoogleCalendarEventDto {
        return when (cacheIdentifier) {
            MemoryCacheIdentifier.Push -> {
                api.getEvent(calendar.calendarId, eventId)
            }
            MemoryCacheIdentifier.List -> {
                eventListMemoryCache[calendar]?.find { it.id == eventId }
                    ?: api.getEvent(calendar.calendarId, eventId)
            }
            MemoryCacheIdentifier.Home -> {
                nextEventsMemoryCache[calendar]?.find { it.id == eventId }
                    ?: api.getEvent(calendar.calendarId, eventId)
            }
        }
    }

    override suspend fun getNext3Events(calendar: SeesturmCalendar): GoogleCalendarEventsDto {
        val response = api.getEvents(calendarId = calendar.calendarId, includePast = false, maxResults = 3)
        nextEventsMemoryCache[calendar] = response.items
        return response
    }
}