package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.templates

sealed class TemplateEditMode {
    data class Insert(
        val onSubmit: (String) -> Unit
    ): TemplateEditMode()
    data class Update(
        val description: String,
        val onSubmit: (String) -> Unit
    ): TemplateEditMode()

    val buttonTitle: String
        get() = when (this) {
            is Insert -> "Speichern"
            is Update -> "Aktualisieren"
        }
    val navigationTitle: String
        get() = when (this) {
            is Insert -> "Neue Vorlage"
            is Update -> "Vorlage bearbeiten"
        }
}