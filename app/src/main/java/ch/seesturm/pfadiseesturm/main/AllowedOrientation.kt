package ch.seesturm.pfadiseesturm.main

sealed class AllowedOrientation {
    data object PortraitOnly: AllowedOrientation()
    data object All: AllowedOrientation()
}