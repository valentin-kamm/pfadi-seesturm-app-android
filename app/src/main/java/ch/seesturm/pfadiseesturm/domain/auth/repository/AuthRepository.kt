package ch.seesturm.pfadiseesturm.domain.auth.repository

import android.content.Intent
import androidx.activity.result.ActivityResult
import ch.seesturm.pfadiseesturm.data.auth.FirebaseUserClaims
import ch.seesturm.pfadiseesturm.data.auth.dto.HitobitoUserInfoDto
import com.google.firebase.auth.FirebaseUser

typealias FirebaseAuthToken = String
typealias HitobitoAccessToken = String

interface AuthRepository {

    suspend fun getAuthIntent(): Intent
    suspend fun getHitobitoUserAndToken(result: ActivityResult): Pair<HitobitoUserInfoDto, HitobitoAccessToken>
    suspend fun authenticateWithFirebase(firebaseToken: FirebaseAuthToken): FirebaseUser
    fun signOutFromFirebase()
    suspend fun deleteFirebaseUserAccount()
    fun getCurrentUid(): String?
    fun getCurrentFirebaseUser(): FirebaseUser?
    suspend fun getCurrentFirebaseUserClaims(user: FirebaseUser): FirebaseUserClaims
}