package ch.seesturm.pfadiseesturm.data.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import ch.seesturm.pfadiseesturm.data.auth.dto.HitobitoUserInfoDto
import ch.seesturm.pfadiseesturm.data.auth.dto.OAuthApplicationConfig
import ch.seesturm.pfadiseesturm.domain.auth.repository.HitobitoAccessToken
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

typealias FirebaseUserClaims = Map<String, Any>

interface AuthApi {

    suspend fun fetchIssuerConfiguration(): AuthorizationServiceConfiguration
    fun getAuthIntent(configuration: AuthorizationServiceConfiguration): Intent
    fun processActivityResult(activityResult: ActivityResult): AuthorizationResponse
    suspend fun redeemCodeForTokens(authResponse: AuthorizationResponse): TokenResponse
    fun getHitobitoAccessToken(tokenResponse: TokenResponse): HitobitoAccessToken
    @GET("oauth/userinfo")
    suspend fun readHitobitoUserInfo(
        @Header("Authorization") accessToken: HitobitoAccessToken
    ): HitobitoUserInfoDto
    suspend fun authenticateWithFirebase(firebaseToken: String): FirebaseUser
    fun signOutFromFirebase()
    suspend fun deleteFirebaseUserAccount()
    fun getCurrentUid(): String?
    fun getCurrentFirebaseUser(): FirebaseUser?
    suspend fun getCurrentFirebaseUserClaims(user: FirebaseUser): FirebaseUserClaims
}

class AuthApiImpl(
    private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val authService: AuthorizationService = AuthorizationService(context),
    private val appConfig: OAuthApplicationConfig = Constants.OAUTH_APP_CONFIG
): AuthApi {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.MIDATA_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val apiService: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    override suspend fun readHitobitoUserInfo(accessToken: HitobitoAccessToken): HitobitoUserInfoDto {
        return apiService.readHitobitoUserInfo("Bearer $accessToken")
    }

    override fun getHitobitoAccessToken(tokenResponse: TokenResponse): HitobitoAccessToken {

        val accessToken = tokenResponse.accessToken
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw PfadiSeesturmError.AuthError("Hitobito Access Token ist leer.")
        }
        return accessToken
    }

    override suspend fun redeemCodeForTokens(authResponse: AuthorizationResponse): TokenResponse {

        return suspendCoroutine { continuation ->
            val tokenRequest = authResponse.createTokenExchangeRequest()
            authService.performTokenRequest(tokenRequest) { tokenResponse, exception ->
                if (exception != null) {
                    continuation.resumeWithException(exception)
                }
                else if (tokenResponse == null) {
                    continuation.resumeWithException(PfadiSeesturmError.AuthError("Code kann nicht gegen Token eingetauscht werden. Unbekannter Fehler."))
                }
                else {
                    continuation.resume(tokenResponse)
                }
            }
        }
    }

    override fun processActivityResult(activityResult: ActivityResult): AuthorizationResponse {

        if (activityResult.resultCode == Activity.RESULT_CANCELED) {
            throw PfadiSeesturmError.Cancelled("Die Operation wurde durch den Benutzer abgebrochen.")
        }
        val intent = activityResult.data
        if (activityResult.resultCode != Activity.RESULT_OK || intent == null) {
            throw PfadiSeesturmError.AuthError("Die Daten aus der Weiterleitung sind ungültig.")
        }

        val response = AuthorizationResponse.fromIntent(intent)
            ?: throw PfadiSeesturmError.AuthError("Die Daten aus der Weiterleitung sind ungültig.")

        return response
    }

    override fun getAuthIntent(configuration: AuthorizationServiceConfiguration): Intent {

        val request = AuthorizationRequest.Builder(
            configuration,
            appConfig.clientID,
            ResponseTypeValues.CODE,
            appConfig.redirectUri
        )
            .setPrompt("login")
            .setScopes(appConfig.scopes)
            .build()

        return authService.getAuthorizationRequestIntent(request)
    }

    override suspend fun fetchIssuerConfiguration(): AuthorizationServiceConfiguration {

        return suspendCoroutine { continuation ->
            AuthorizationServiceConfiguration.fetchFromIssuer(
                appConfig.issuer
            ) { serviceConfig, exception ->
                if (exception != null) {
                    continuation.resumeWithException(exception)
                }
                else if (serviceConfig == null) {
                    continuation.resumeWithException(PfadiSeesturmError.AuthError("OIDC provider (db.scout.ch) nicht erreichbar. Unbekannter Fehler."))
                }
                else {
                    val updatedConfig = serviceConfig.setCustomTokenEndpoint()
                    continuation.resume(updatedConfig)
                }
            }
        }
    }

    private fun AuthorizationServiceConfiguration.setCustomTokenEndpoint(): AuthorizationServiceConfiguration =
        AuthorizationServiceConfiguration(
            authorizationEndpoint,
            Constants.OAUTH_TOKEN_ENDPOINT,
            registrationEndpoint,
            endSessionEndpoint
        )

    override suspend fun authenticateWithFirebase(firebaseToken: String): FirebaseUser =
        firebaseAuth.signInWithCustomToken(firebaseToken).await().user
            ?: throw PfadiSeesturmError.AuthError("Bei der Anmeldung ist ein Fehler aufgetreten. Der Firebase-User ist leer.")

    override fun signOutFromFirebase() =
        firebaseAuth.signOut()

    override suspend fun deleteFirebaseUserAccount() {
        firebaseAuth.currentUser?.delete()?.await()
    }

    override fun getCurrentUid(): String? =
        firebaseAuth.currentUser?.uid

    override fun getCurrentFirebaseUser(): FirebaseUser? =
        firebaseAuth.currentUser

    override suspend fun getCurrentFirebaseUserClaims(user: FirebaseUser): FirebaseUserClaims {
        val authTokenResult = user.getIdToken(true).await()
        return authTokenResult.claims
    }
}