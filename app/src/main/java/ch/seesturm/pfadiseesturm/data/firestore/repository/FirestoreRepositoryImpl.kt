package ch.seesturm.pfadiseesturm.data.firestore.repository

import ch.seesturm.pfadiseesturm.data.firestore.FirestoreApi
import ch.seesturm.pfadiseesturm.data.firestore.dto.FirestoreDto
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
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

    override suspend fun <T : FirestoreDto> performTransaction(
        document: FirestoreRepository.SeesturmFirestoreDocument,
        type: Class<T>,
        update: (T) -> T
    ) {
        api.performTransaction(
            document = documentReference(document),
            type = type,
            update = update
        )
    }

    override suspend fun <T : FirestoreDto> observeDocument(
        document: FirestoreRepository.SeesturmFirestoreDocument,
        type: Class<T>
    ): Flow<SeesturmResult<T, DataError.RemoteDatabase>> =
        api.observeDocument(
            document = documentReference(document),
            type = type
        )

    override fun <T : FirestoreDto> observeCollection(
        collection: FirestoreRepository.SeesturmFirestoreCollection,
        type: Class<T>,
        filter: ((Query) -> Query)?
    ): Flow<SeesturmResult<List<T>, DataError.RemoteDatabase>> =
        api.observeCollection(
            collection = collectionReference(collection),
            type = type,
            filter = filter
        )

    override suspend fun <T : FirestoreDto> insertDocument(
        item: T,
        collection: FirestoreRepository.SeesturmFirestoreCollection
    ) {
        api.insertDocument(
            item = item,
            collection = collectionReference(collection)
        )
    }

    override suspend fun <T : FirestoreDto> upsertDocument(
        item: T,
        document: FirestoreRepository.SeesturmFirestoreDocument,
        type: Class<T>
    ) {
        api.upsertDocument(
            item = item,
            document = documentReference(document),
            type = type
        )
    }

    override suspend fun <T : FirestoreDto> readDocument(document: FirestoreRepository.SeesturmFirestoreDocument, type: Class<T>): T =
        api.readDocument(
            document = documentReference(document),
            type = type
        )

    override suspend fun deleteDocument(document: FirestoreRepository.SeesturmFirestoreDocument) {
        api.deleteDocument(documentReference(document))
    }

    override suspend fun deleteDocuments(documents: List<FirestoreRepository.SeesturmFirestoreDocument>) {
        api.deleteDocuments(documents.map { documentReference(it) })
    }

    private fun collectionReference(collection: FirestoreRepository.SeesturmFirestoreCollection): CollectionReference {
        return when (collection) {
            FirestoreRepository.SeesturmFirestoreCollection.Users -> {
                db.collection("users")
            }
            FirestoreRepository.SeesturmFirestoreCollection.Leiterbereich -> {
                db.collection("leiterbereich")
            }
            FirestoreRepository.SeesturmFirestoreCollection.Abmeldungen -> {
                db.collection("abmeldungen")
            }
            FirestoreRepository.SeesturmFirestoreCollection.FoodOrders -> {
                db.collection("leiterbereich").document("food").collection("orders")
            }
        }
    }
    private fun documentReference(document: FirestoreRepository.SeesturmFirestoreDocument): DocumentReference {
        return when (document) {
            FirestoreRepository.SeesturmFirestoreDocument.Food -> {
                collectionReference(FirestoreRepository.SeesturmFirestoreCollection.Leiterbereich).document("food")
            }
            is FirestoreRepository.SeesturmFirestoreDocument.Order -> {
                collectionReference(FirestoreRepository.SeesturmFirestoreCollection.FoodOrders).document(document.id)
            }
            FirestoreRepository.SeesturmFirestoreDocument.Schoepflialarm -> {
                collectionReference(FirestoreRepository.SeesturmFirestoreCollection.Leiterbereich).document("schopflialarm")
            }
            is FirestoreRepository.SeesturmFirestoreDocument.User -> {
                collectionReference(FirestoreRepository.SeesturmFirestoreCollection.Users).document(document.id)
            }
            is FirestoreRepository.SeesturmFirestoreDocument.Abmeldung -> {
                collectionReference(FirestoreRepository.SeesturmFirestoreCollection.Abmeldungen).document(document.id)
            }
        }
    }
}
