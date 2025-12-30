package ch.seesturm.pfadiseesturm.data.firestore

import ch.seesturm.pfadiseesturm.data.firestore.dto.FirestoreDto
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resumeWithException

interface FirestoreApi {

    suspend fun <T: FirestoreDto>insertDocument(item: T, collection: CollectionReference)
    suspend fun <T: FirestoreDto>upsertDocument(item: T, document: DocumentReference, type: Class<T>)
    suspend fun <T: FirestoreDto>readDocument(document: DocumentReference, type: Class<T>): T
    suspend fun <T: FirestoreDto>readCollection(collection: CollectionReference, type: Class<T>): List<T>
    suspend fun deleteDocument(document: DocumentReference)
    suspend fun deleteDocuments(documents: List<DocumentReference>)
    suspend fun deleteAllDocumentsInCollection(collection: CollectionReference)
    fun <T: FirestoreDto>observeDocument(document: DocumentReference, type: Class<T>): Flow<SeesturmResult<T, DataError.RemoteDatabase>>
    fun <T: FirestoreDto>observeCollection(collection: CollectionReference, type: Class<T>, filter: ((Query) -> Query)? = null): Flow<SeesturmResult<List<T>, DataError.RemoteDatabase>>
    suspend fun <T: FirestoreDto>performTransaction(document: DocumentReference, type: Class<T>, forceNewCreatedDate: Boolean, update: (T) -> T)
}

class FirestoreApiImpl(
    private val db: FirebaseFirestore
): FirestoreApi {

    override suspend fun <T : FirestoreDto> insertDocument(
        item: T,
        collection: CollectionReference
    ) {
        val docRef = collection.document()
        docRef.set(item).await()
    }

    override suspend fun <T : FirestoreDto> upsertDocument(
        item: T,
        document: DocumentReference,
        type: Class<T>
    ) {

        db.runTransaction { transaction ->

            val snapshot = transaction.get(document)

            if (!snapshot.exists()) {
                // document does not exist yet -> insert it
                transaction.set(document, item)
                return@runTransaction true
            }
            else {
                val existingItem = snapshot.toObject(type)
                    ?: throw IllegalArgumentException("Item cannot be parsed.")

                if (existingItem.contentEquals(item)) {
                    print("X")
                    // data has not changed -> do nothing
                    return@runTransaction true
                }
                else {
                    // document exists and data has changed -> perform update with merge, set timestamps correctly
                    item.created = existingItem.created
                    item.modified = null

                    transaction.set(document, item, SetOptions.merge())

                    return@runTransaction true
                }
            }
        }.await()
    }

    override suspend fun <T : FirestoreDto> readDocument(document: DocumentReference, type: Class<T>): T {
        return document.get().await().toObject(type)
            ?: throw IllegalArgumentException("Document does not exist or cannot be cast.")
    }

    override suspend fun <T: FirestoreDto>readCollection(collection: CollectionReference, type: Class<T>): List<T> {

        val snapshot = collection.get().await()
        return snapshot.documents.map { document ->
            document.toObject(type)
                ?: throw IllegalArgumentException("Document does not exist or cannot be cast.")
        }
    }

    override suspend fun deleteDocument(document: DocumentReference) {
        document.delete().await()
    }

    override suspend fun deleteDocuments(documents: List<DocumentReference>) {

        val batch = db.batch()
        for (document in documents) {
            batch.delete(document)
        }
        batch.commit().await()
    }

    override suspend fun deleteAllDocumentsInCollection(collection: CollectionReference) {
        val documents = collection.get().await().documents.map { it.reference }
        deleteDocuments(documents)
    }

    override fun <T : FirestoreDto> observeDocument(
        document: DocumentReference,
        type: Class<T>
    ): Flow<SeesturmResult<T, DataError.RemoteDatabase>> = callbackFlow {

        val listener = document.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(
                    SeesturmResult.Error(DataError.RemoteDatabase.READING_ERROR(message = error.message ?: "Unbekannter Fehler."))
                )
            }
            else if (snapshot == null) {
                trySend(SeesturmResult.Error(DataError.RemoteDatabase.DOCUMENT_DOES_NOT_EXIST))
            }
            else {
                try {
                    val data = snapshot.toObject(type)
                    if (data == null) {
                        trySend(SeesturmResult.Error(DataError.RemoteDatabase.DECODING_ERROR))
                    }
                    else {
                        trySend(SeesturmResult.Success(data))
                    }
                }
                catch (e: Exception) {
                    trySend(SeesturmResult.Error(DataError.RemoteDatabase.DECODING_ERROR))
                }
            }
        }
        awaitClose {
            listener.remove()
        }
    }

    override fun <T : FirestoreDto> observeCollection(
        collection: CollectionReference,
        type: Class<T>,
        filter: ((Query) -> Query)?
    ): Flow<SeesturmResult<List<T>, DataError.RemoteDatabase>> = callbackFlow {

        var query: Query = collection
        if (filter != null) {
            query = filter(query)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(
                    SeesturmResult.Error(DataError.RemoteDatabase.READING_ERROR(message = error.message ?: "Unbekannter Fehler"))
                )
            }
            else if (snapshot == null) {
                trySend(SeesturmResult.Error(DataError.RemoteDatabase.DOCUMENT_DOES_NOT_EXIST))
            }
            else {
                try {
                    val data: List<T> = snapshot.toObjects(type)
                    trySend(SeesturmResult.Success(data))
                }
                catch (e: Exception) {
                    trySend(SeesturmResult.Error(DataError.RemoteDatabase.DECODING_ERROR))
                }
            }
        }
        awaitClose {
            listener.remove()
        }
    }

    override suspend fun <T : FirestoreDto> performTransaction(
        document: DocumentReference,
        type: Class<T>,
        forceNewCreatedDate: Boolean,
        update: (T) -> T
    ) = suspendCancellableCoroutine<Unit> { continuation ->

        db.runTransaction { transaction ->

            val snapshot = transaction.get(document)
            val currentData = snapshot.toObject(type)
                ?: throw IllegalStateException("Das Dokument konnte nicht decodiert werden.")

            val newData = update(currentData)

            if (forceNewCreatedDate) {
                newData.created = null
                newData.modified = null
                transaction.set(document, newData)
            }
            else if (currentData.contentEquals(newData)) {
                return@runTransaction true
            }
            else {
                newData.created = currentData.created
                newData.modified = null
                transaction.set(document, newData)
            }
        }
        .addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
        .addOnSuccessListener {
            continuation.resume(Unit) { _, _, _ -> }
        }
    }
}