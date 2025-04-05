package ch.seesturm.pfadiseesturm.data.wordpress.repository

import androidx.datastore.core.DataStore
import ch.seesturm.pfadiseesturm.data.data_store.dao.SeesturmPreferencesDao
import ch.seesturm.pfadiseesturm.data.firestore.FirestoreApi
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventsDto
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.NaechsteAktivitaetRepository
import ch.seesturm.pfadiseesturm.util.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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

    override suspend fun getOrFetchAktivitaetById(
        eventId: String,
        stufe: SeesturmStufe,
        tryFetchingFromCache: Boolean
    ): GoogleCalendarEventDto {
        return if (tryFetchingFromCache) {
            getFromMemoryCache(stufe)?.items?.firstOrNull()
                ?: api.getEvent(
                    calendarId = stufe.calendar.calendarId,
                    eventId = eventId
                )
        }
        else {
            api.getEvent(
                calendarId = stufe.calendar.calendarId,
                eventId = eventId
            )
        }
    }

    private fun saveToMemoryCache(stufe: SeesturmStufe, response: GoogleCalendarEventsDto) {
        aktivitaetenMemoryCache = aktivitaetenMemoryCache + (stufe to response)
    }
    private fun getFromMemoryCache(stufe: SeesturmStufe): GoogleCalendarEventsDto? {
        return aktivitaetenMemoryCache[stufe]
    }
}