package ch.seesturm.pfadiseesturm.util

import android.net.Uri
import ch.seesturm.pfadiseesturm.data.auth.dto.OAuthApplicationConfig

object Constants {

    const val SEESTURM_BASE_URL = "https://seesturm.ch"

    // Seesturm REST API endpoints
    const val SEESTURM_API_BASE_URL = "$SEESTURM_BASE_URL/wp-json/seesturmAppCustomEndpoints/v2/"

    // adding delay to network requests
    const val MIN_ARTIFICIAL_DELAY: Long = 300L
    const val MAX_ARTIFICIAL_DELAY: Long = 600L

    // generic placeholder text
    const val PLACEHOLDER_TEXT = "Lorem ipsum odor amet, consectetuer adipiscing elit. Lobortis duis lacinia venenatis dapibus libero proin. Sit suscipit dictum curae bibendum aliquam. Ex diam magna lacinia fringilla id, risus quisque eros. Parturient hendrerit quisque torquent molestie sociosqu suscipit ex semper. Phasellus mus amet iaculis mollis cursus sit nisl. Nulla ac risus suspendisse magna accumsan maecenas. Maximus dictum ac ligula dolor maximus leo dapibus ac vestibulum. Dis adipiscing taciti ad facilisis, nostra massa. Semper ante sociosqu bibendum rhoncus suscipit nullam. Curabitur ante netus volutpat velit, finibus ante hendrerit."

    // sticky header key
    const val STICKY_HEADER_CONTENT_TYPE: String = "OffsetStickyHeader"

    // urls for mehr screen
    const val FEEDBACK_FORM_URL = "https://docs.google.com/forms/d/e/1FAIpQLSfT0fEhmPpLxrY4sUjkuYwbchMENu1a5pPwpe5NQ2kCqkYL1A/viewform?usp=sf_link"
    const val DATENSCHUTZERKLAERUNG_URL = "https://seesturm.ch/datenschutz/"

    // proto datastore file name
    const val DATA_STORE_FILE_NAME = "seesturm-preferences.json"

    // oauth configuration
    const val MIDATA_BASE_URL = "https://db.scout.ch"
    val OAUTH_APP_CONFIG = OAuthApplicationConfig(
        issuer = Uri.parse(MIDATA_BASE_URL),
        clientID = "amedEopij2UUJChtHzUulytzcq7cmjOQkqZ9i_T6uMQ",
        redirectUri = Uri.parse("https://seesturm.ch/oauth/app/callback"),
        scopes = listOf(
            "email",
            "name",
            "with_roles",
            "openid"
        )
    )
    val OAUTH_TOKEN_ENDPOINT: Uri = Uri.parse("https://seesturm.ch/wp-json/seesturmAppCustomEndpoints/v2/oauth/token")
    const val HITOBITO_APP_GROUP_ID = 12399

}