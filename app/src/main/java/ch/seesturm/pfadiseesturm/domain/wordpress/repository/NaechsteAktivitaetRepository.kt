package ch.seesturm.pfadiseesturm.domain.wordpress.repository

import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventsDto
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe

interface NaechsteAktivitaetRepository {

    var aktivitaetenMemoryCache: Map<SeesturmStufe, GoogleCalendarEventsDto>
    suspend fun fetchNaechsteAktivitaet(stufe: SeesturmStufe): GoogleCalendarEventsDto
    suspend fun getAktivitaetById(stufe: SeesturmStufe, eventId: String, cacheIdentifier: MemoryCacheIdentifier): GoogleCalendarEventDto
}