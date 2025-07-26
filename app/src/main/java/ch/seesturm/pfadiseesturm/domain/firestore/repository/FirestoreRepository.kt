package ch.seesturm.pfadiseesturm.domain.firestore.repository

import ch.seesturm.pfadiseesturm.data.firestore.dto.FirestoreDto
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow

interface FirestoreRepository {

    suspend fun <T: FirestoreDto>insertDocument(item: T, collection: SeesturmFirestoreCollection)
    suspend fun <T: FirestoreDto>upsertDocument(item: T, document: SeesturmFirestoreDocument, type: Class<T>)
    suspend fun <T: FirestoreDto>readDocument(document: SeesturmFirestoreDocument, type: Class<T>): T
    suspend fun <T: FirestoreDto>readCollection(collection: SeesturmFirestoreCollection, type: Class<T>): List<T>
    suspend fun deleteDocument(document: SeesturmFirestoreDocument)
    suspend fun deleteDocuments(documents: List<SeesturmFirestoreDocument>)
    suspend fun deleteAllDocumentsInCollection(collection: SeesturmFirestoreCollection)
    fun <T: FirestoreDto>observeDocument(document: SeesturmFirestoreDocument, type: Class<T>): Flow<SeesturmResult<T, DataError.RemoteDatabase>>
    fun <T: FirestoreDto>observeCollection(collection: SeesturmFirestoreCollection, type: Class<T>, filter: ((Query) -> Query)? = null): Flow<SeesturmResult<List<T>, DataError.RemoteDatabase>>
    suspend fun <T: FirestoreDto>performTransaction(document: SeesturmFirestoreDocument, type: Class<T>, forceNewCreatedDate: Boolean, update: (T) -> T)

    sealed class SeesturmFirestoreCollection {
        data object Users: SeesturmFirestoreCollection()
        data object Abmeldungen: SeesturmFirestoreCollection()
        data object FoodOrders: SeesturmFirestoreCollection()
        data object Schoepflialarm: SeesturmFirestoreCollection()
        data object SchoepflialarmReactions: SeesturmFirestoreCollection()
        data object AktivitaetTemplates: SeesturmFirestoreCollection()
    }

    sealed class SeesturmFirestoreDocument {
        data class User(val id: String): SeesturmFirestoreDocument()
        data class Abmeldung(val id: String): SeesturmFirestoreDocument()
        data object Schoepflialarm: SeesturmFirestoreDocument()
        data class Order(val id: String): SeesturmFirestoreDocument()
        data class AktivitaetTemplate(val id: String): SeesturmFirestoreDocument()
    }
}