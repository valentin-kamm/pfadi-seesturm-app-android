package ch.seesturm.pfadiseesturm.util.types

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
}