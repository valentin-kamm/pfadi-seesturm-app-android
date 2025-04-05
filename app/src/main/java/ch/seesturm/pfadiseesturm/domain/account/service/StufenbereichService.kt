package ch.seesturm.pfadiseesturm.domain.account.service

import ch.seesturm.pfadiseesturm.data.firestore.dto.AktivitaetAnAbmeldungDto
import ch.seesturm.pfadiseesturm.data.firestore.dto.toAktivitaetAnAbmeldung
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toGoogleCalendarEvent
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toGoogleCalendarEvents
import ch.seesturm.pfadiseesturm.domain.fcf.model.CloudFunctionEventPayload
import ch.seesturm.pfadiseesturm.domain.fcf.model.toCloudFunctionEventPayloadDto
import ch.seesturm.pfadiseesturm.domain.fcf.repository.CloudFunctionsRepository
import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetAnAbmeldung
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEventWithAnAbmeldungen
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.AnlaesseRepository
import ch.seesturm.pfadiseesturm.domain.wordpress.service.WordpressService
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import java.time.ZonedDateTime

class StufenbereichService(
    private val termineRepository: AnlaesseRepository,
    private val firestoreRepository: FirestoreRepository,
    private val cloudFunctionsRepository: CloudFunctionsRepository
): WordpressService() {

    suspend fun addNewAktivitaet(
        event: CloudFunctionEventPayload,
        stufe: SeesturmStufe,
        withNotification: Boolean
    ): SeesturmResult<String, DataError.CloudFunctionsError> {

        return try {
            val payload = event.toCloudFunctionEventPayloadDto()
            val response = cloudFunctionsRepository.addEvent(
                calendar = stufe.calendar,
                event = payload
            )
            if (withNotification) {
                cloudFunctionsRepository.sendPushNotification()
            }
            SeesturmResult.Success(response.eventId)
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

    suspend fun updateExistingAktivitaet(
        eventId: String,
        event: CloudFunctionEventPayload,
        stufe: SeesturmStufe,
        withNotification: Boolean
    ): SeesturmResult<String, DataError.CloudFunctionsError> {

        return try {
            val payload = event.toCloudFunctionEventPayloadDto()
            val response = cloudFunctionsRepository.updateEvent(
                calendar = stufe.calendar,
                eventId = eventId,
                event = payload
            )
            if (withNotification) {
                cloudFunctionsRepository.sendPushNotification()
            }
            SeesturmResult.Success(response.eventId)
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

    suspend fun deleteAnAbmeldungen(aktivitaet: GoogleCalendarEventWithAnAbmeldungen): SeesturmResult<Unit, DataError.RemoteDatabase> {

        val documents = aktivitaet.anAbmeldungen.map { FirestoreRepository.SeesturmFirestoreDocument.Abmeldung(it.id) }

        return try {
            if (documents.isNotEmpty()) {
                firestoreRepository.deleteDocuments(documents)
            }
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.RemoteDatabase.DELETING_ERROR)
        }
    }

    suspend fun deleteAllPastAnAbmeldungen(stufe: SeesturmStufe, anAbmeldungen: List<AktivitaetAnAbmeldung>): SeesturmResult<Unit, DataError.RemoteDatabase> {
        return try {
            // get all events that lie in the future
            val eventsInFuture = termineRepository.getEvents(calendar = stufe.calendar, includePast = false, maxResults = 2500).items
            val excludedIds = eventsInFuture.map { it.id }
            // exclude anAbmeldungen of these events
            val abmeldungenToDelete = anAbmeldungen.filter { it.stufe == stufe }.filter { !excludedIds.contains(it.eventId) }
            val documentsToDelete = abmeldungenToDelete.map { FirestoreRepository.SeesturmFirestoreDocument.Abmeldung(it.id) }

            if (documentsToDelete.isNotEmpty()) {
                firestoreRepository.deleteDocuments(documentsToDelete)
            }
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.RemoteDatabase.DELETING_ERROR)
        }
    }

    suspend fun sendPushNotification(stufe: SeesturmStufe, aktivitaet: GoogleCalendarEvent): SeesturmResult<Unit, DataError.Messaging> {

        return try {
            cloudFunctionsRepository.sendPushNotification()
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.Messaging.UNKNOWN)
        }
    }

    suspend fun fetchEvents(stufe: SeesturmStufe, timeMin: ZonedDateTime): SeesturmResult<List<GoogleCalendarEvent>, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { termineRepository.getEvents(calendar = stufe.calendar, timeMin = timeMin.toInstant() ) },
            transform = { it.toGoogleCalendarEvents().items }
        )

    suspend fun fetchEvent(stufe: SeesturmStufe, eventId: String): SeesturmResult<GoogleCalendarEvent, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { termineRepository.getEvent(calendar = stufe.calendar, eventId = eventId, cacheIdentifier = MemoryCacheIdentifier.Push) },
            transform = { it.toGoogleCalendarEvent() }
        )

    fun observeAnAbmeldungen(stufe: SeesturmStufe): Flow<SeesturmResult<List<AktivitaetAnAbmeldung>, DataError.RemoteDatabase>> {
        return firestoreRepository.observeCollection(
            collection = FirestoreRepository.SeesturmFirestoreCollection.Abmeldungen,
            type = AktivitaetAnAbmeldungDto::class.java,
            filter = { query ->
                query.whereEqualTo("stufenId", stufe.id)
            }
        )
            .map { result ->
                when (result) {
                    is SeesturmResult.Error -> {
                        SeesturmResult.Error(result.error)
                    }
                    is SeesturmResult.Success -> {
                        val abmeldungen = result.data.map { it.toAktivitaetAnAbmeldung() }
                        SeesturmResult.Success(abmeldungen)
                    }
                }
            }
    }
}