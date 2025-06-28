package ch.seesturm.pfadiseesturm.domain.wordpress.repository

import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventsDto
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import java.time.Instant

interface AnlaesseRepository {

    var eventListMemoryCache: MutableMap<SeesturmCalendar, List<GoogleCalendarEventDto>>
    var nextEventsMemoryCache: MutableMap<SeesturmCalendar, List<GoogleCalendarEventDto>>
    suspend fun getEvents(calendar: SeesturmCalendar, includePast: Boolean, maxResults: Int): GoogleCalendarEventsDto
    suspend fun getEvents(calendar: SeesturmCalendar, pageToken: String, maxResults: Int): GoogleCalendarEventsDto
    suspend fun getEvents(calendar: SeesturmCalendar, timeMin: Instant): GoogleCalendarEventsDto
    suspend fun getEvent(calendar: SeesturmCalendar, eventId: String, cacheIdentifier: MemoryCacheIdentifier): GoogleCalendarEventDto
    suspend fun getNextThreeEvents(calendar: SeesturmCalendar): GoogleCalendarEventsDto
}