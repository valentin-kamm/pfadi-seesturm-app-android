package ch.seesturm.pfadiseesturm.data.firestore.repository

import ch.seesturm.pfadiseesturm.data.firestore.FirestoreApi
import ch.seesturm.pfadiseesturm.data.firestore.dto.FirestoreDto
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository.SeesturmFirestoreCollection
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository.SeesturmFirestoreDocument
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow

class FirestoreRepositoryImpl(
    private val api: FirestoreApi,
    private val db: FirebaseFirestore
): FirestoreRepository {

    override fun <T : FirestoreDto> observeDocument(
        document: SeesturmFirestoreDocument,
        type: Class<T>
    ): Flow<SeesturmResult<T, DataError.RemoteDatabase>> =
        api.observeDocument(
            document = documentReference(document),
            type = type
        )

    override fun <T : FirestoreDto> observeCollection(
        collection: SeesturmFirestoreCollection,
        type: Class<T>,
        filter: ((Query) -> Query)?
    ): Flow<SeesturmResult<List<T>, DataError.RemoteDatabase>> =
        api.observeCollection(
            collection = collectionReference(collection),
            type = type,
            filter = filter
        )

    override suspend fun <T : FirestoreDto> performTransaction(
        document: SeesturmFirestoreDocument,
        type: Class<T>,
        forceNewCreatedDate: Boolean,
        update: (T) -> T
    ) {
        api.performTransaction(
            document = documentReference(document),
            type = type,
            forceNewCreatedDate = forceNewCreatedDate,
            update = update
        )
    }

    override suspend fun <T : FirestoreDto> insertDocument(
        item: T,
        collection: SeesturmFirestoreCollection
    ) {
        api.insertDocument(
            item = item,
            collection = collectionReference(collection)
        )
    }

    override suspend fun <T : FirestoreDto> upsertDocument(
        item: T,
        document: SeesturmFirestoreDocument,
        type: Class<T>
    ) {
        api.upsertDocument(
            item = item,
            document = documentReference(document),
            type = type
        )
    }

    override suspend fun <T : FirestoreDto> readDocument(document: SeesturmFirestoreDocument, type: Class<T>): T =
        api.readDocument(
            document = documentReference(document),
            type = type
        )

    override suspend fun deleteDocument(document: SeesturmFirestoreDocument) {
        api.deleteDocument(documentReference(document))
    }

    override suspend fun deleteDocuments(documents: List<SeesturmFirestoreDocument>) {
        api.deleteDocuments(documents.map { documentReference(it) })
    }

    override suspend fun deleteAllDocumentsInCollection(collection: SeesturmFirestoreCollection) {
        api.deleteAllDocumentsInCollection(collectionReference(collection))
    }

    private fun collectionReference(collection: SeesturmFirestoreCollection): CollectionReference {
        return when (collection) {
            SeesturmFirestoreCollection.Abmeldungen -> db.collection("abmeldungen")
            SeesturmFirestoreCollection.FoodOrders -> db.collection("leiterbereichFoodOrders").document("food").collection("orders")
            SeesturmFirestoreCollection.Schoepflialarm -> db.collection("schopflialarm")
            SeesturmFirestoreCollection.SchoepflialarmReactions -> documentReference(SeesturmFirestoreDocument.Schoepflialarm).collection("reactions")
            SeesturmFirestoreCollection.Users -> db.collection("users")
            SeesturmFirestoreCollection.AktivitaetTemplates -> db.collection("aktivitaetTemplates")
        }
    }
    private fun documentReference(document: SeesturmFirestoreDocument): DocumentReference {
        return when (document) {
            is SeesturmFirestoreDocument.Abmeldung -> collectionReference(SeesturmFirestoreCollection.Abmeldungen).document(document.id)
            is SeesturmFirestoreDocument.Order -> collectionReference(SeesturmFirestoreCollection.FoodOrders).document(document.id)
            SeesturmFirestoreDocument.Schoepflialarm -> collectionReference(SeesturmFirestoreCollection.Schoepflialarm).document("schopflialarm")
            is SeesturmFirestoreDocument.User -> collectionReference(SeesturmFirestoreCollection.Users).document(document.id)
            is SeesturmFirestoreDocument.AktivitaetTemplate -> collectionReference(SeesturmFirestoreCollection.AktivitaetTemplates).document(document.id)
        }
    }
}
