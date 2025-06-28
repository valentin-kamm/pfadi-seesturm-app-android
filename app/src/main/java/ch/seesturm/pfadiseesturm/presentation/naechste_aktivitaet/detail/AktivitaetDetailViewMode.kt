package ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail

import ch.seesturm.pfadiseesturm.util.types.AktivitaetInteractionType

sealed class AktivitaetDetailViewMode {
    data object ViewOnly: AktivitaetDetailViewMode()
    data class Interactive(
        val onNavigateToPushNotifications: () -> Unit,
        val onOpenSheet: (AktivitaetInteractionType) -> Unit,
    ): AktivitaetDetailViewMode()
}