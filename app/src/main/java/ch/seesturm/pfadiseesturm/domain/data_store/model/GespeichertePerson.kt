package ch.seesturm.pfadiseesturm.domain.data_store.model

data class GespeichertePerson(
    val id: String,
    val vorname: String,
    val nachname: String,
    val pfadiname: String?,
    val swipeActionsRevealed: Boolean = false
) {
    val displayName: String
        get() = if (pfadiname == null) {
            "$vorname $nachname"
        }
        else {
            "$vorname $nachname / $pfadiname"
        }
}