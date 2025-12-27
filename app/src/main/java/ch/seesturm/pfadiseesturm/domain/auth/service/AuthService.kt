package ch.seesturm.pfadiseesturm.domain.auth.service

import android.content.Intent
import androidx.activity.result.ActivityResult
import ch.seesturm.pfadiseesturm.data.auth.dto.toFirebaseHitobitoUserDto
import ch.seesturm.pfadiseesturm.data.firestore.dto.FirebaseHitobitoUserDto
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.auth.repository.AuthRepository
import ch.seesturm.pfadiseesturm.domain.fcf.repository.CloudFunctionsRepository
import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.domain.fcm.repository.FCMRepository
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import ch.seesturm.pfadiseesturm.domain.storage.model.DeleteStorageItem
import ch.seesturm.pfadiseesturm.domain.storage.repository.StorageRepository
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.types.FirebaseHitobitoUserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthService(
    private val authRepository: AuthRepository,
    private val cloudFunctionsRepository: CloudFunctionsRepository,
    private val firestoreRepository: FirestoreRepository,
    private val fcmRepository: FCMRepository,
    private val storageRepository: StorageRepository
) {

    suspend fun startAuthFlow(): SeesturmResult<Intent, DataError.AuthError> =
        try {
            val intent = authRepository.getAuthIntent()
            SeesturmResult.Success(intent)
        }
        catch (e: PfadiSeesturmError) {
            SeesturmResult.Error(DataError.AuthError.SIGN_IN_ERROR(e.message))
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.AuthError.SIGN_IN_ERROR("Unbekannter Fehler: ${e.message}"))
        }

    suspend fun finishAuthFlow(activityResult: ActivityResult): SeesturmResult<FirebaseHitobitoUser, DataError.AuthError> {

        return try {
            val (userInfo, hitobitoAccessToken) = authRepository.getHitobitoUserAndToken(activityResult)
            val firebaseAuthToken = cloudFunctionsRepository.getFirebaseAuthToken(
                userId = userInfo.sub,
                hitobitoAccessToken = hitobitoAccessToken
            )
            val firebaseUser = authRepository.authenticateWithFirebase(firebaseAuthToken)
            val firebaseUserClaims = authRepository.getCurrentFirebaseUserClaims(firebaseUser)
            val firebaseUserRole = FirebaseHitobitoUserRole.fromClaims(firebaseUserClaims)
            val fcmToken = fcmRepository.getCurrentFCMToken()
            val firebaseUserDto = userInfo.toFirebaseHitobitoUserDto(firebaseUserRole.role).copy(
                fcmToken = fcmToken
            )
            upsertUser(firebaseUserDto, id = userInfo.sub)
            val firebaseHitobitoUserDto = firestoreRepository.readDocument(
                document = FirestoreRepository.SeesturmFirestoreDocument.User(id = userInfo.sub),
                type = FirebaseHitobitoUserDto::class.java
            )
            val firebaseHitobitoUser = FirebaseHitobitoUser.from(firebaseHitobitoUserDto)
            fcmRepository.subscribeToTopic(SeesturmFCMNotificationTopic.Schoepflialarm)
            SeesturmResult.Success(firebaseHitobitoUser)
        }
        catch (e: PfadiSeesturmError) {
            when (e) {
                is PfadiSeesturmError.Cancelled -> {
                    SeesturmResult.Error(DataError.AuthError.CANCELLED)
                }
                else -> {
                    SeesturmResult.Error(DataError.AuthError.SIGN_IN_ERROR(e.message))
                }
            }
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.AuthError.SIGN_IN_ERROR("Unbekannter Fehler: ${e.message}"))
        }
    }

    suspend fun reauthenticateWithHitobito(resubscribeToSchoepflialarm: Boolean): SeesturmResult<FirebaseHitobitoUser, DataError.AuthError> {

        val firebaseUser = authRepository.getCurrentFirebaseUser()
            ?: return SeesturmResult.Error(DataError.AuthError.SIGN_IN_ERROR("Es ist kein Benutzer angemeldet. Neue Anmeldung nötig."))

        return try {
            val claims = authRepository.getCurrentFirebaseUserClaims(firebaseUser)
            FirebaseHitobitoUserRole.fromClaims(claims)

            if (resubscribeToSchoepflialarm) {
                CoroutineScope(Dispatchers.Default).launch {
                    try {
                        fcmRepository.subscribeToTopic(SeesturmFCMNotificationTopic.Schoepflialarm)
                    }
                    catch (e: Exception) {
                        // do nothing
                    }
                }
            }

            val firebaseHitobitoUserDto = firestoreRepository.readDocument(
                document = FirestoreRepository.SeesturmFirestoreDocument.User(id = firebaseUser.uid),
                type = FirebaseHitobitoUserDto::class.java
            )
            val firebaseHitobitoUser = FirebaseHitobitoUser.from(firebaseHitobitoUserDto)

            val fcmToken = fcmRepository.getCurrentFCMToken()
            fcmRepository.updateFCMToken(firebaseUser.uid, fcmToken)
            SeesturmResult.Success(firebaseHitobitoUser)
        }
        catch (e: PfadiSeesturmError) {
            SeesturmResult.Error(DataError.AuthError.SIGN_IN_ERROR("Die Anmeldung ist fehlgeschlagen. Versuche es erneut oder kontaktiere den Admin. ${e.message}"))
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.AuthError.SIGN_IN_ERROR("Bei der Anmeldung ist ein unbekannter Fehler aufgetreten. Versuche es erneut. ${e.message}"))
        }
    }

    private suspend fun upsertUser(user: FirebaseHitobitoUserDto, id: String) {
        try {
            firestoreRepository.upsertDocument(
                item = user,
                document = FirestoreRepository.SeesturmFirestoreDocument.User(id),
                type = FirebaseHitobitoUserDto::class.java
            )
        }
        catch (e: Exception) {
            throw PfadiSeesturmError.AuthError("Der Benutzer konnte nicht in der Datenbank gespeichert werden")
        }
    }

    suspend fun signOut(): SeesturmResult<Unit, DataError.AuthError> {
        return try {
            fcmRepository.unsubscribeFromTopic(SeesturmFCMNotificationTopic.Schoepflialarm)
            fcmRepository.unsubscribeFromTopic(SeesturmFCMNotificationTopic.SchoepflialarmReaction)
            authRepository.signOutFromFirebase()
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.AuthError.SIGN_OUT_ERROR("Benutzer konnte nicht abgemeldet werden. Versuche es erneut."))
        }
    }

    suspend fun deleteAccount(user: FirebaseHitobitoUser): SeesturmResult<Unit, DataError.AuthError> {
        return try {
            fcmRepository.unsubscribeFromTopic(SeesturmFCMNotificationTopic.Schoepflialarm)
            fcmRepository.unsubscribeFromTopic(SeesturmFCMNotificationTopic.SchoepflialarmReaction)
            storageRepository.deleteData(DeleteStorageItem.ProfilePicture(user))
            firestoreRepository.deleteDocument(FirestoreRepository.SeesturmFirestoreDocument.User(user.userId))
            authRepository.deleteFirebaseUserAccount()
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.AuthError.DELETE_ACCOUNT_ERROR("Der Account konnte nicht gelöscht werden. Versuche es später erneut."))
        }
    }

    suspend fun isCurrentUserHitobitoUser(): Boolean {

        val firebaseUser = authRepository.getCurrentFirebaseUser() ?: return false

        try {
            val claims = authRepository.getCurrentFirebaseUserClaims(firebaseUser)
            FirebaseHitobitoUserRole.fromClaims(claims)
            return true
        }
        catch (e: Exception) {
            return false
        }
    }

    fun getCurrentUid(): String? =
        authRepository.getCurrentUid()
}