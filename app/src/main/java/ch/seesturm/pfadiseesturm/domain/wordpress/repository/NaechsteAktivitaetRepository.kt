package ch.seesturm.pfadiseesturm.domain.wordpress.repository

import ch.seesturm.pfadiseesturm.data.data_store.dao.GespeichertePersonDao
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventsDto
import ch.seesturm.pfadiseesturm.util.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import kotlinx.coroutines.flow.Flow

interface NaechsteAktivitaetRepository {

    var aktivitaetenMemoryCache: Map<SeesturmStufe, GoogleCalendarEventsDto>

    suspend fun fetchNaechsteAktivitaet(
        stufe: SeesturmStufe,
    ): GoogleCalendarEventsDto

    /*
    suspend fun getOrFetchNaechsteAktivitaet(
        stufe: SeesturmStufe,
        cacheIdentifier: MemoryCacheIdentifier
    ): GoogleCalendarEventsDto

     */

    suspend fun getOrFetchAktivitaetById(
        eventId: String,
        stufe: SeesturmStufe,
        tryFetchingFromCache: Boolean = true
    ): GoogleCalendarEventDto
}