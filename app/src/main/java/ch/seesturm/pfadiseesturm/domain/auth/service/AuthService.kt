package ch.seesturm.pfadiseesturm.domain.auth.service

import android.content.Intent
import androidx.activity.result.ActivityResult
import ch.seesturm.pfadiseesturm.data.auth.dto.toFirebaseHitobitoUserDto
import ch.seesturm.pfadiseesturm.data.firestore.dto.FirebaseHitobitoUserDto
import ch.seesturm.pfadiseesturm.data.firestore.dto.toFirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.auth.repository.AuthRepository
import ch.seesturm.pfadiseesturm.domain.fcf.repository.CloudFunctionsRepository
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import ch.seesturm.pfadiseesturm.domain.wordpress.model.Leitungsteam
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.AnlaesseRepository
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmAppError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.time.ZonedDateTime

class AuthService(
    private val repository: AuthRepository,
    private val cloudFunctionsRepository: CloudFunctionsRepository,
    private val firestoreRepository: FirestoreRepository,
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun startAuthFlow(): SeesturmResult<Intent, DataError.AuthError> {
        return try {
            val intent = repository.getAuthIntent()
            SeesturmResult.Success(intent)
        }
        catch (e: PfadiSeesturmAppError) {
            SeesturmResult.Error(DataError.AuthError.SIGN_IN_ERROR(e.message))
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.AuthError.SIGN_IN_ERROR("Unbekannter Fehler: ${e.message}"))
        }
    }

    suspend fun finishAuthFlow(activityResult: ActivityResult): SeesturmResult<FirebaseHitobitoUser, DataError.AuthError> {

        return try {
            val (userInfo, accessToken) = repository.getValidatedHitobitoDetails(activityResult)
            val firebaseAuthToken = cloudFunctionsRepository.getFirebaseAuthToken(
                userId = userInfo.sub,
                hitobitoAccessToken = accessToken
            )
            repository.authenticateWithFirebase(firebaseToken = firebaseAuthToken)
            val userDtoRequest = userInfo.toFirebaseHitobitoUserDto()
            upsertUser(userDtoRequest, id = userInfo.sub)
            val user = readUserFromFirestore(userInfo.sub)
            SeesturmResult.Success(user)
        }
        catch (e: PfadiSeesturmAppError) {
            when (e) {
                is PfadiSeesturmAppError.Cancelled -> {
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

    private suspend fun upsertUser(user: FirebaseHitobitoUserDto, id: String) {
        try {
            firestoreRepository.upsertDocument(
                item = user,
                document = FirestoreRepository.SeesturmFirestoreDocument.User(id),
                type = FirebaseHitobitoUserDto::class.java
            )
        }
        catch (e: Exception) {
            throw PfadiSeesturmAppError.AuthError("Der Benutzer konnte nicht in der Datenbank gespeichert werden")
        }
    }

    private suspend fun readUserFromFirestore(id: String): FirebaseHitobitoUser {
        val document = FirestoreRepository.SeesturmFirestoreDocument.User(id)
        val userDto = firestoreRepository.readDocument(
            document = document,
            type = FirebaseHitobitoUserDto::class.java
        )
        return userDto.toFirebaseHitobitoUser()
    }

    suspend fun reauthenticate(): SeesturmResult<FirebaseHitobitoUser, DataError.AuthError> {
        val firebaseUser = firebaseAuth.currentUser
        return if (firebaseUser != null && firebaseUser.isHitobitoUser()) {
            try {
                val user = readUserFromFirestore(firebaseUser.uid)
                SeesturmResult.Success(user)
            }
            catch (e: Exception) {
                SeesturmResult.Error(DataError.AuthError.SIGN_IN_ERROR("User konnte nicht von Firestore gelesen werden."))
            }
        }
        else {
            SeesturmResult.Error(DataError.AuthError.SIGN_IN_ERROR("Es ist kein Benutzer angemeldet. Neue Anmeldung nötig."))
        }
    }

    fun signOut(): SeesturmResult<Unit, DataError.AuthError> {
        return try {
            repository.signOutFromFirebase()
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.AuthError.SIGN_OUT_ERROR("Benutzer konnte nicht abgemeldet werden. Versuche es erneut."))
        }
    }

    suspend fun deleteUser(user: FirebaseHitobitoUser): SeesturmResult<Unit, DataError.AuthError> {
        return try {
            firestoreRepository.deleteDocument(FirestoreRepository.SeesturmFirestoreDocument.User(user.userId))
            repository.deleteFirebaseAccount()
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.AuthError.DELETE_ACCOUNT_ERROR("Der Account konnte nicht gelöscht werden. Versuche es später erneut."))
        }
    }

    fun getCurrentUid(): String? {
        return repository.getCurrentUid()
    }
}

private fun FirebaseUser.isHitobitoUser(): Boolean {
    return !isAnonymous && providerId == "firebase"
}