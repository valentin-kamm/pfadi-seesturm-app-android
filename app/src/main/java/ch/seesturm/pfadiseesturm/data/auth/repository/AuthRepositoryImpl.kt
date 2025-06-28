package ch.seesturm.pfadiseesturm.data.auth.repository

import android.content.Intent
import androidx.activity.result.ActivityResult
import ch.seesturm.pfadiseesturm.data.auth.AuthApi
import ch.seesturm.pfadiseesturm.data.auth.FirebaseUserClaims
import ch.seesturm.pfadiseesturm.data.auth.dto.HitobitoUserInfoDto
import ch.seesturm.pfadiseesturm.domain.auth.repository.AuthRepository
import ch.seesturm.pfadiseesturm.domain.auth.repository.FirebaseAuthToken
import ch.seesturm.pfadiseesturm.domain.auth.repository.HitobitoAccessToken
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmAppError
import com.google.firebase.auth.FirebaseUser

class AuthRepositoryImpl(
    private val api: AuthApi
): AuthRepository {

    override suspend fun getAuthIntent(): Intent {

        val configuration = api.fetchIssuerConfiguration()
        return api.getAuthIntent(configuration)
    }

    override suspend fun getHitobitoUserAndToken(result: ActivityResult): Pair<HitobitoUserInfoDto, HitobitoAccessToken> {

        val authResponse = api.processActivityResult(result)
        val tokenResponse = api.redeemCodeForTokens(authResponse)
        val accessToken = api.getHitobitoAccessToken(tokenResponse)
        val userInfo = api.readHitobitoUserInfo(accessToken)
        validatePermission(userInfo)
        return Pair(userInfo, accessToken)
    }

    private fun validatePermission(userInfo: HitobitoUserInfoDto) {

        val groupIdArray: List<Int> = userInfo.roles?.mapNotNull { it?.groupId } ?: emptyList()
        if (!groupIdArray.contains(Constants.HITOBITO_APP_GROUP_ID)) {
            throw PfadiSeesturmAppError.AuthError("Du hast keine Berechtigung, um dich bei der Pfadi Seesturm App anzumelden. Wende dich an die MiData-Addressverwalter der Pfadi Seesturm.")
        }
    }

    override suspend fun authenticateWithFirebase(firebaseToken: FirebaseAuthToken): FirebaseUser {
        return api.authenticateWithFirebase(firebaseToken)
    }

    override fun signOutFromFirebase() =
        api.signOutFromFirebase()

    override suspend fun deleteFirebaseUserAccount() =
        api.deleteFirebaseUserAccount()

    override fun getCurrentUid(): String? =
        api.getCurrentUid()

    override suspend fun getCurrentFirebaseUserClaims(user: FirebaseUser): FirebaseUserClaims =
        api.getCurrentFirebaseUserClaims(user)

    override fun getCurrentFirebaseUser(): FirebaseUser? =
        api.getCurrentFirebaseUser()
}