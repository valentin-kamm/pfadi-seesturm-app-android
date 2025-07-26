package ch.seesturm.pfadiseesturm.util.types

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
enum class SeesturmAppTheme {
    Dark,
    Light,
    System;

    val description: String
        get() = when (this) {
            Dark -> "Dunkel"
            Light -> "Hell"
            System -> "System"
        }

    val isDarkTheme: Boolean
    @Composable
    get() = when (this) {
        Dark -> true
        Light -> false
        System -> isSystemInDarkTheme()
    }
}