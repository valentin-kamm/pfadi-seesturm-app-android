package ch.seesturm.pfadiseesturm.data.auth.repository

import android.content.Intent
import androidx.activity.result.ActivityResult
import ch.seesturm.pfadiseesturm.data.auth.AuthApi
import ch.seesturm.pfadiseesturm.data.auth.dto.HitobitoUserInfoDto
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.auth.repository.AuthRepository
import ch.seesturm.pfadiseesturm.domain.fcf.repository.CloudFunctionsRepository
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmAppError
import java.time.ZonedDateTime

class AuthRepositoryImpl(
    private val api: AuthApi
): AuthRepository {

    override suspend fun getAuthIntent(): Intent {
        val configuration = api.fetchConfiguration()
        return api.getAuthIntent(configuration)
    }

    override suspend fun getValidatedHitobitoDetails(result: ActivityResult): Pair<HitobitoUserInfoDto, String> {
        val authResponse = api.processActivityResult(result)
        val tokenResponse = api.redeemCodeForTokens(authResponse)
        val accessToken = api.getHitobitoAccessToken(tokenResponse)
        val userInfo = api.readHitobitoUserInfo(accessToken)
        validatePermission(userInfo)
        return Pair(userInfo, accessToken)
    }

    override suspend fun authenticateWithFirebase(firebaseToken: String) {
        api.authenticateWithFirebase(firebaseToken)
    }

    private fun validatePermission(userInfo: HitobitoUserInfoDto) {
        val groupIdArray: List<Int> = userInfo.roles?.mapNotNull { it?.groupId } ?: emptyList()
        if (!groupIdArray.contains(Constants.HITOBITO_APP_GROUP_ID)) {
            throw PfadiSeesturmAppError.AuthError("Du hast keine Berechtigung, um dich bei der Pfadi Seesturm App anzumelden. Wende dich an die MiData-Addressverwalter der Pfadi Seesturm.")
        }
    }

    override fun signOutFromFirebase() {
        api.signOutFromFirebase()
    }

    override suspend fun deleteFirebaseAccount() {
        api.deleteFirebaseAccount()
    }

    override fun getCurrentUid(): String? {
        return api.getCurrentUid()
    }
}