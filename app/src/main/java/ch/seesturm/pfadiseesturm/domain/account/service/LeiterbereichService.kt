package ch.seesturm.pfadiseesturm.domain.account.service

import ch.seesturm.pfadiseesturm.data.firestore.dto.FirebaseHitobitoUserDto
import ch.seesturm.pfadiseesturm.data.firestore.dto.FoodOrderDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toGoogleCalendarEvents
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.data_store.repository.SelectedStufenRepository
import ch.seesturm.pfadiseesturm.domain.firestore.model.FoodOrder
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import ch.seesturm.pfadiseesturm.domain.storage.model.DeleteStorageItem
import ch.seesturm.pfadiseesturm.domain.storage.model.JPGData
import ch.seesturm.pfadiseesturm.domain.storage.model.UploadStorageItem
import ch.seesturm.pfadiseesturm.domain.storage.repository.StorageRepository
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.AnlaesseRepository
import ch.seesturm.pfadiseesturm.domain.wordpress.service.WordpressService
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException

class LeiterbereichService(
    private val termineRepository: AnlaesseRepository,
    private val firestoreRepository: FirestoreRepository,
    private val selectedStufenRepository: SelectedStufenRepository,
    private val storageRepository: StorageRepository
): WordpressService() {

    suspend fun fetchNext3Events(calendar: SeesturmCalendar): SeesturmResult<List<GoogleCalendarEvent>, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { termineRepository.getNextThreeEvents(calendar) },
            transform = { it.toGoogleCalendarEvents().items }
        )

    fun observeFoodOrders(): Flow<SeesturmResult<List<FoodOrderDto>, DataError.RemoteDatabase>> {
        return firestoreRepository.observeCollection(
            collection = FirestoreRepository.SeesturmFirestoreCollection.FoodOrders,
            type = FoodOrderDto::class.java
        )
    }

    fun observeUsers(): Flow<SeesturmResult<List<FirebaseHitobitoUser>, DataError.RemoteDatabase>> {
        return firestoreRepository.observeCollection(
            collection = FirestoreRepository.SeesturmFirestoreCollection.Users,
            type = FirebaseHitobitoUserDto::class.java
        )
            .map { result ->
                when (result) {
                    is SeesturmResult.Error -> {
                        SeesturmResult.Error(result.error)
                    }
                    is SeesturmResult.Success -> {
                        val users = result.data.map { FirebaseHitobitoUser.create(it) }
                        SeesturmResult.Success(users)
                    }
                }
            }
    }

    suspend fun addNewFoodOrder(order: FoodOrderDto): SeesturmResult<Unit, DataError.RemoteDatabase> {
        return try {
            firestoreRepository.insertDocument(
                item = order,
                collection = FirestoreRepository.SeesturmFirestoreCollection.FoodOrders
            )
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.RemoteDatabase.SAVING_ERROR)
        }
    }
    suspend fun deleteFromExistingOrder(userId: String, orderId: String): SeesturmResult<Unit, DataError.RemoteDatabase> {
        return try {
            firestoreRepository.performTransaction(
                document = FirestoreRepository.SeesturmFirestoreDocument.Order(id = orderId),
                type = FoodOrderDto::class.java,
                forceNewCreatedDate = false,
                update = { oldOrder ->
                    val newUserList = oldOrder.userIds.toMutableList()
                    newUserList.remove(userId)
                    oldOrder.copy(
                        userIds = newUserList
                    )
                }
            )
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.RemoteDatabase.DELETING_ERROR)
        }
    }
    suspend fun addToExistingOrder(userId: String, orderId: String): SeesturmResult<Unit, DataError.RemoteDatabase> {
        return try {
            firestoreRepository.performTransaction(
                document = FirestoreRepository.SeesturmFirestoreDocument.Order(id = orderId),
                type = FoodOrderDto::class.java,
                forceNewCreatedDate = false,
                update = { oldOrder ->
                    val newUserList = oldOrder.userIds.toMutableList()
                    newUserList.add(userId)
                    oldOrder.copy(
                        userIds = newUserList,
                        modified = null
                    )
                }
            )
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.RemoteDatabase.SAVING_ERROR)
        }
    }
    suspend fun deleteAllOrders(orders: List<FoodOrder>): SeesturmResult<Unit, DataError.RemoteDatabase> =
        try {
            val documents = orders.map { FirestoreRepository.SeesturmFirestoreDocument.Order(id = it.id) }
            firestoreRepository.deleteDocuments(documents)
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.RemoteDatabase.DELETING_ERROR)
        }

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

    suspend fun uploadProfilePicture(data: JPGData, userId: String): SeesturmResult<Unit, DataError.Storage> {

        return try {
            val downloadUri = storageRepository.uploadData(
                item = UploadStorageItem.ProfilePicture(
                    data = data,
                    userId = userId
                )
            )
            firestoreRepository.performTransaction(
                document = FirestoreRepository.SeesturmFirestoreDocument.User(userId),
                type = FirebaseHitobitoUserDto::class.java,
                forceNewCreatedDate = false,
                update = { oldUser ->
                    FirebaseHitobitoUserDto.copyAndUpdateProfilePicture(oldUser, downloadUri.toString())
                }
            )
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.Storage.UPLOADING_ERROR)
        }
    }

    suspend fun deleteProfilePicture(userId: String): SeesturmResult<Unit, DataError.Storage> {

        return try {
            storageRepository.deleteData(DeleteStorageItem.ProfilePicture(userId))
            firestoreRepository.performTransaction(
                document = FirestoreRepository.SeesturmFirestoreDocument.User(userId),
                type = FirebaseHitobitoUserDto::class.java,
                forceNewCreatedDate = false,
                update = { oldUser ->
                    FirebaseHitobitoUserDto.copyAndUpdateProfilePicture(oldUser, null)
                }
            )
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.Storage.DELETING_ERROR)
        }
    }
}