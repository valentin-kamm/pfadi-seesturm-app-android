package ch.seesturm.pfadiseesturm.domain.wordpress.service

import ch.seesturm.pfadiseesturm.data.firestore.dto.AktivitaetAnAbmeldungDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toGoogleCalendarEvent
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toGoogleCalendarEvents
import ch.seesturm.pfadiseesturm.domain.data_store.repository.SelectedStufenRepository
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.NaechsteAktivitaetRepository
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException

class NaechsteAktivitaetService(
    private val repository: NaechsteAktivitaetRepository,
    private val firestoreRepository: FirestoreRepository,
    private val selectedStufenRepository: SelectedStufenRepository
): WordpressService() {

    suspend fun sendAnAbmeldung(abmeldungDto: AktivitaetAnAbmeldungDto): SeesturmResult<Unit, DataError.RemoteDatabase> =
        try {
            firestoreRepository.insertDocument(
                item = abmeldungDto,
                collection = FirestoreRepository.SeesturmFirestoreCollection.Abmeldungen
            )
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.RemoteDatabase.SAVING_ERROR)
        }

    suspend fun fetchNaechsteAktivitaet(stufe: SeesturmStufe): SeesturmResult<GoogleCalendarEvent?, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.fetchNaechsteAktivitaet(stufe) },
            transform = { it.toGoogleCalendarEvents().items.firstOrNull() }
        )

    suspend fun getOrFetchAktivitaetById(stufe: SeesturmStufe, eventId: String): SeesturmResult<GoogleCalendarEvent, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getAktivitaetById(stufe = stufe, eventId = eventId, cacheIdentifier = MemoryCacheIdentifier.TryGetFromHomeCache) },
            transform = { it.toGoogleCalendarEvent() }
        )

    fun readSelectedStufen(): Flow<SeesturmResult<Set<SeesturmStufe>, DataError.Local>> =
        selectedStufenRepository.readStufen()
            .map<_, SeesturmResult<Set<SeesturmStufe>, DataError.Local>> { list ->
                SeesturmResult.Success(list)
            }
            .catch { e ->
                emit(
                    SeesturmResult.Error(
                        when (e) {
                            is SerializationException -> {
                                DataError.Local.READING_ERROR
                            }
                            else -> {
                                DataError.Local.UNKNOWN
                            }
                        }
                    )
                )
            }

    suspend fun deleteStufe(stufe: SeesturmStufe): SeesturmResult<Unit, DataError.Local> =
        try {
            selectedStufenRepository.deleteStufe(stufe)
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(
                when (e) {
                    is SerializationException -> {
                        DataError.Local.DELETING_ERROR
                    }
                    else -> {
                        DataError.Local.UNKNOWN
                    }
                }
            )
        }

    suspend fun addStufe(stufe: SeesturmStufe): SeesturmResult<Unit, DataError.Local> =
        try {
            selectedStufenRepository.insertStufe(stufe)
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(
                when (e) {
                    is SerializationException -> {
                        DataError.Local.SAVING_ERROR
                    }
                    else -> {
                        DataError.Local.UNKNOWN
                    }
                }
            )
        }
}