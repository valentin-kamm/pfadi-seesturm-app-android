package ch.seesturm.pfadiseesturm.presentation.common.event_management.types

import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe

sealed interface EventPreviewType {
    data class Aktivitaet(
        val stufe: SeesturmStufe
    ): EventPreviewType
    data class MultipleAktivitaeten(
        val stufen: Set<SeesturmStufe>
    ): EventPreviewType
    data class Termin(
        val calendar: SeesturmCalendar
    ): EventPreviewType

    val navigationTitle: String
        get() = when (this) {
            is Aktivitaet -> "Vorschau ${stufe.aktivitaetDescription}"
            is MultipleAktivitaeten -> "Vorschau Aktivitäten"
            is Termin -> "Vorschau Anlass"
        }
}