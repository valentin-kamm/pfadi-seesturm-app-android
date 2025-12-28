package ch.seesturm.pfadiseesturm.util

import android.net.Uri
import androidx.core.net.toUri
import ch.seesturm.pfadiseesturm.data.auth.dto.OAuthApplicationConfig

object Constants {

    const val WORDPRESS_API_BASE_URL = "https://seesturm.ch/wp-json/seesturmAppCustomEndpoints/v2/"

    // adding delay to network requests
    const val MIN_ARTIFICIAL_DELAY: Long = 300L
    const val MAX_ARTIFICIAL_DELAY: Long = 600L

    // sticky header key
    const val STICKY_HEADER_CONTENT_TYPE: String = "OffsetStickyHeader"

    const val FEEDBACK_FORM_URL = "https://docs.google.com/forms/d/e/1FAIpQLSfT0fEhmPpLxrY4sUjkuYwbchMENu1a5pPwpe5NQ2kCqkYL1A/viewform?usp=sf_link"
    const val DATENSCHUTZERKLAERUNG_URL = "https://seesturm.ch/datenschutz/"

    // proto datastore file name
    const val DATA_STORE_FILE_NAME = "seesturm-preferences.json"

    // oauth configuration
    const val MIDATA_BASE_URL = "https://db.scout.ch"
    val OAUTH_APP_CONFIG = OAuthApplicationConfig(
        issuer = MIDATA_BASE_URL.toUri(),
        clientID = "amedEopij2UUJChtHzUulytzcq7cmjOQkqZ9i_T6uMQ",
        redirectUri = "https://seesturm.ch/oauth/app/callback".toUri(),
        scopes = listOf(
            "email",
            "name",
            "with_roles",
            "openid"
        )
    )
    val OAUTH_TOKEN_ENDPOINT: Uri =
        "https://seesturm.ch/wp-json/seesturmAppCustomEndpoints/v2/oauth/token".toUri()
    const val HITOBITO_APP_GROUP_ID = 12399

    const val SCHOPFLI_LONGITUDE = 9.362085
    const val SCHOPFLI_LATITUDE = 47.530457
    const val SCHOPFLIALARM_MAX_DISTANCE = 100.0 // m
    const val SCHOPFLIALARM_MIN_PAUSE = 3600 // s = 1h

    const val PROFILE_PICTURE_SIZE: Float = 1024f
    const val PROFILE_PICTURE_COMPRESSION_QUALITY: Int = 75
}