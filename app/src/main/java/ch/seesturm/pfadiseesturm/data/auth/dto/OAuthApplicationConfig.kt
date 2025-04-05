package ch.seesturm.pfadiseesturm.data.auth.dto

import android.net.Uri

data class OAuthApplicationConfig(
    val issuer: Uri,
    val clientID: String,
    val redirectUri: Uri,
    val scopes: List<String>
)