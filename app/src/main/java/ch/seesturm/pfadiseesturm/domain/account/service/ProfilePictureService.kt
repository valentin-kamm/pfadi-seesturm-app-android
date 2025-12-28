package ch.seesturm.pfadiseesturm.domain.account.service

import android.net.Uri
import ch.seesturm.pfadiseesturm.data.firestore.dto.FirebaseHitobitoUserDto
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import ch.seesturm.pfadiseesturm.domain.storage.model.DeleteStorageItem
import ch.seesturm.pfadiseesturm.domain.storage.model.ProfilePicture
import ch.seesturm.pfadiseesturm.domain.storage.model.UploadStorageItem
import ch.seesturm.pfadiseesturm.domain.storage.repository.StorageRepository
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult

class ProfilePictureService(
    private val storageRepository: StorageRepository,
    private val firestoreRepository: FirestoreRepository
) {

    suspend fun uploadProfilePicture(user: FirebaseHitobitoUser, picture: ProfilePicture): SeesturmResult<Uri, DataError.Storage> {

        try {
            val downloadUrl = storageRepository.uploadData(
                item = UploadStorageItem.ProfilePicture(
                    user = user,
                    data = picture
                )
            )
            firestoreRepository.performTransaction(
                document = FirestoreRepository.SeesturmFirestoreDocument.User(id = user.userId),
                type = FirebaseHitobitoUserDto::class.java,
                forceNewCreatedDate = false) { oldUser ->
                    FirebaseHitobitoUserDto.from(oldUser = oldUser, newProfilePictureUrl = downloadUrl.toString())
                }
            return SeesturmResult.Success(downloadUrl)
        }
        catch (e: PfadiSeesturmError) {
            return SeesturmResult.Error(DataError.Storage.UPLOAD(e.message))
        }
        catch (e: Exception) {
            return SeesturmResult.Error(DataError.Storage.UPLOAD("Beim Hochladen des Profilbilds ist ein unbekannter Fehler aufgetreten. ${e.message ?: ""}"))
        }
    }

    suspend fun deleteProfilePicture(user: FirebaseHitobitoUser): SeesturmResult<Unit, DataError.Storage> {

        try {
            storageRepository.deleteData(DeleteStorageItem.ProfilePicture(user))
            firestoreRepository.performTransaction(
                document = FirestoreRepository.SeesturmFirestoreDocument.User(id = user.userId),
                type = FirebaseHitobitoUserDto::class.java,
                forceNewCreatedDate = false) { oldUser ->
                    FirebaseHitobitoUserDto.from(oldUser, null)
                }
            return SeesturmResult.Success(Unit)
        }
        catch (e: PfadiSeesturmError) {
            return SeesturmResult.Error(DataError.Storage.DELETE(e.message))
        }
        catch (e: Exception) {
            return SeesturmResult.Error(DataError.Storage.DELETE("Beim Löschen des Profilbilds ist ein unbekannter Fehler aufgetreten."))
        }
    }
}