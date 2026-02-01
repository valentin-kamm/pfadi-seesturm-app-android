package ch.seesturm.pfadiseesturm.presentation.common.event_management.types

import android.net.Uri
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
sealed interface EventManagementMode {
    @Serializable
    data object Insert: EventManagementMode
    @Serializable
    data class Update(
        val eventId: String
    ): EventManagementMode

    val verb: String
        get() = when (this) {
            Insert -> "veröffentlichen"
            is Update -> "aktualisieren"
        }

    val verbPassiv: String
        get() = when (this) {
            Insert -> "veröffentlicht"
            is Update -> "aktualisiert"
        }

    val nomen: String
        get() = when (this) {
            Insert -> "Veröffentlichen"
            is Update -> "Aktualisieren"
        }
}

val EventManagementModeNavType = object : NavType<EventManagementMode>(isNullableAllowed = false) {

    override fun put(bundle: SavedState, key: String, value: EventManagementMode) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun get(bundle: SavedState, key: String): EventManagementMode? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): EventManagementMode {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: EventManagementMode): String {
        return Uri.encode(Json.encodeToString(value))
    }
}