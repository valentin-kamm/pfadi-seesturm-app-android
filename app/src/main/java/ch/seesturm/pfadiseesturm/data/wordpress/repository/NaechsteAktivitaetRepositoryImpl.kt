package ch.seesturm.pfadiseesturm.data.wordpress.repository


import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventsDto
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.NaechsteAktivitaetRepository
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe

class NaechsteAktivitaetRepositoryImpl(
    private val api: WordpressApi
): NaechsteAktivitaetRepository {

    override var aktivitaetenMemoryCache = emptyMap<SeesturmStufe, GoogleCalendarEventsDto>()

    override suspend fun fetchNaechsteAktivitaet(stufe: SeesturmStufe): GoogleCalendarEventsDto {
        val response = api.getEvents(
            calendarId = stufe.calendar.calendarId,
            includePast = false,
            maxResults = 1
        )
        saveToMemoryCache(stufe, response)
        return response
    }

    override suspend fun getAktivitaetById(
        stufe: SeesturmStufe,
        eventId: String,
        cacheIdentifier: MemoryCacheIdentifier
    ): GoogleCalendarEventDto {

        return when (cacheIdentifier) {
            MemoryCacheIdentifier.ForceReload -> {
                api.getEvent(
                    calendarId = stufe.calendar.calendarId,
                    eventId = eventId
                )
            }
            MemoryCacheIdentifier.TryGetFromListCache, MemoryCacheIdentifier.TryGetFromHomeCache -> {
                getFromMemoryCache(stufe, eventId)
                    ?: api.getEvent(
                        calendarId = stufe.calendar.calendarId,
                        eventId = eventId
                    )
            }
        }
    }

    private fun saveToMemoryCache(stufe: SeesturmStufe, response: GoogleCalendarEventsDto) {
        aktivitaetenMemoryCache = aktivitaetenMemoryCache + (stufe to response)
    }

    private fun getFromMemoryCache(stufe: SeesturmStufe, eventId: String): GoogleCalendarEventDto? {
        val cachedEvent = aktivitaetenMemoryCache[stufe]?.items?.firstOrNull()
        return if (cachedEvent?.id == eventId) {
            cachedEvent
        }
        else {
            null
        }
    }
}