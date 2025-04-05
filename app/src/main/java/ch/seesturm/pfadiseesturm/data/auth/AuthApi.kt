package ch.seesturm.pfadiseesturm.data.auth

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import ch.seesturm.pfadiseesturm.data.auth.dto.HitobitoUserInfoDto
import ch.seesturm.pfadiseesturm.data.auth.dto.OAuthApplicationConfig
import ch.seesturm.pfadiseesturm.presentation.main.MainActivity
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmAppError
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import net.openid.appauth.AuthorizationException
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
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface AuthApi {

    val appConfig: OAuthApplicationConfig
    val context: Context
    val authService: AuthorizationService
    val firebaseAuth: FirebaseAuth

    suspend fun fetchConfiguration(): AuthorizationServiceConfiguration
    fun AuthorizationServiceConfiguration.setCustomTokenEndpoint(): AuthorizationServiceConfiguration
    fun getAuthIntent(configuration: AuthorizationServiceConfiguration): Intent
    fun processActivityResult(activityResult: ActivityResult): AuthorizationResponse
    suspend fun redeemCodeForTokens(authResponse: AuthorizationResponse): TokenResponse
    fun getHitobitoAccessToken(tokenResponse: TokenResponse): String
    @GET("oauth/userinfo")
    suspend fun readHitobitoUserInfo(
        @Header("Authorization") accessToken: String
    ): HitobitoUserInfoDto
    suspend fun authenticateWithFirebase(firebaseToken: String)
    fun signOutFromFirebase()
    suspend fun deleteFirebaseAccount()
    fun getCurrentUid(): String?
}

class AuthApiImpl(
    override val context: Context,
    override val firebaseAuth: FirebaseAuth
): AuthApi {

    override val appConfig: OAuthApplicationConfig
        get() = Constants.OAUTH_APP_CONFIG

    override val authService: AuthorizationService
        get() = AuthorizationService(context)

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.MIDATA_BASE_URL) // Replace with your base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val apiService: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    override suspend fun readHitobitoUserInfo(accessToken: String): HitobitoUserInfoDto {
        return apiService.readHitobitoUserInfo("Bearer $accessToken")
    }

    override suspend fun authenticateWithFirebase(firebaseToken: String) {
        firebaseAuth.signInWithCustomToken(firebaseToken).await()
    }

    override fun getHitobitoAccessToken(tokenResponse: TokenResponse): String {
        val accessToken = tokenResponse.accessToken
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw PfadiSeesturmAppError.AuthError("Access Token ist leer.")
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
                    continuation.resumeWithException(PfadiSeesturmAppError.AuthError("Code kann nicht gegen Token eingetauscht werden. Unbekannter Fehler."))
                }
                else {
                    continuation.resume(tokenResponse)
                }
            }
        }
    }

    override fun processActivityResult(activityResult: ActivityResult): AuthorizationResponse {
        if (activityResult.resultCode == Activity.RESULT_CANCELED) {
            throw PfadiSeesturmAppError.Cancelled("Die Operation wurde durch den Benutzer abgebrochen.")
        }
        val intent = activityResult.data
        if (activityResult.resultCode != Activity.RESULT_OK || intent == null) {
            throw PfadiSeesturmAppError.AuthError("Die Daten aus der Weiterleitung sind ungültig.")
        }

        val response = AuthorizationResponse.fromIntent(intent)
            ?: throw PfadiSeesturmAppError.AuthError("Die Daten aus der Weiterleitung sind ungültig.")

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

    override suspend fun fetchConfiguration(): AuthorizationServiceConfiguration {
        return suspendCoroutine { continuation ->
            AuthorizationServiceConfiguration.fetchFromIssuer(
                appConfig.issuer
            ) { serviceConfig, exception ->
                if (exception != null) {
                    continuation.resumeWithException(exception)
                }
                else if (serviceConfig == null) {
                    continuation.resumeWithException(PfadiSeesturmAppError.AuthError("OIDC provider (db.scout.ch) nicht erreichbar."))
                }
                else {
                    val updatedConfig = serviceConfig.setCustomTokenEndpoint()
                    continuation.resume(updatedConfig)
                }
            }
        }
    }

    override fun AuthorizationServiceConfiguration.setCustomTokenEndpoint(): AuthorizationServiceConfiguration {
        return AuthorizationServiceConfiguration(
            authorizationEndpoint,
            Constants.OAUTH_TOKEN_ENDPOINT,
            registrationEndpoint,
            endSessionEndpoint
        )
    }

    override fun signOutFromFirebase() {
        firebaseAuth.signOut()
    }

    override suspend fun deleteFirebaseAccount() {
        firebaseAuth.currentUser?.delete()?.await()
    }

    override fun getCurrentUid(): String? {
        return firebaseAuth.currentUser?.uid
    }
}