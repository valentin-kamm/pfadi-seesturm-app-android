package ch.seesturm.pfadiseesturm.domain.auth.repository

import android.content.Intent
import androidx.activity.result.ActivityResult
import ch.seesturm.pfadiseesturm.data.auth.dto.HitobitoUserInfoDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressPostsDto
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser

interface AuthRepository {

    suspend fun getAuthIntent(): Intent
    suspend fun getValidatedHitobitoDetails(result: ActivityResult): Pair<HitobitoUserInfoDto, String>
    suspend fun authenticateWithFirebase(firebaseToken: String)
    fun signOutFromFirebase()
    suspend fun deleteFirebaseAccount()
    fun getCurrentUid(): String?
}