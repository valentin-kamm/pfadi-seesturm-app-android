package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten

sealed class AktivitaetValidationStatusErrorType {
    data class TitleTextField(
        val message: String
    ): AktivitaetValidationStatusErrorType()
    data class Snackbar(
        val message: String
    ): AktivitaetValidationStatusErrorType()
}