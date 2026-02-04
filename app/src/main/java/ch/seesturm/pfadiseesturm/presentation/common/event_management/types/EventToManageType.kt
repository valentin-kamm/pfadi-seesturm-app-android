package ch.seesturm.pfadiseesturm.presentation.common.event_management.types

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
sealed interface EventToManageType {
    @Serializable
    data class Aktivitaet(
        val stufe: SeesturmStufe,
        val mode: EventManagementMode
    ): EventToManageType
    @Serializable
    data object MultipleAktivitaeten: EventToManageType
    @Serializable
    data class Termin(
        val calendar: SeesturmCalendar,
        val mode: EventManagementMode
    ): EventToManageType

    val navigationTitle: String
        get() {
            return when (this) {
                is Aktivitaet -> {
                    when (this.mode) {
                        EventManagementMode.Insert -> "Neue ${this.stufe.aktivitaetDescription}"
                        is EventManagementMode.Update -> "${this.stufe.aktivitaetDescription} bearbeiten"
                    }
                }
                MultipleAktivitaeten -> "Neue Aktivität"
                is Termin -> {
                    when (this.mode) {
                        EventManagementMode.Insert -> "Neuer Anlass"
                        is EventManagementMode.Update -> "Anlass bearbeiten"
                    }
                }
            }
        }

    val titlePlaceholder: String
        get() = when (this) {
            is Aktivitaet -> stufe.aktivitaetDescription
            MultipleAktivitaeten -> "Titel der Aktivität"
            is Termin -> "Titel des Anlasses"
        }

    @Composable
    fun accentColor(isDarkTheme: Boolean): Color {
        return when (this) {
            is Aktivitaet -> this.stufe.highContrastColor(isDarkTheme)
            MultipleAktivitaeten -> Color.SEESTURM_GREEN
            is Termin -> if (this.calendar.isLeitungsteam) Color.SEESTURM_RED else Color.SEESTURM_GREEN
        }
    }

    @Composable
    fun onAccentColor(): Color {
        return when (this) {
            is Aktivitaet -> this.stufe.onHighContrastColor()
            MultipleAktivitaeten, is Termin -> Color.White
        }
    }
}

val EventToManageNavType = object : NavType<EventToManageType>(isNullableAllowed = false) {

    override fun put(bundle: SavedState, key: String, value: EventToManageType) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun get(bundle: SavedState, key: String): EventToManageType? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): EventToManageType {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: EventToManageType): String {
        return Uri.encode(Json.encodeToString(value))
    }
}