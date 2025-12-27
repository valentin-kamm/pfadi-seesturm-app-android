package ch.seesturm.pfadiseesturm.main

import ch.seesturm.pfadiseesturm.util.types.SeesturmAppTheme

data class AppState(
    val theme: SeesturmAppTheme = SeesturmAppTheme.System,
    val showAppVersionCheckOverlay: Boolean = false,
    val allowedOrientation: AllowedOrientation = AllowedOrientation.PortraitOnly
)
