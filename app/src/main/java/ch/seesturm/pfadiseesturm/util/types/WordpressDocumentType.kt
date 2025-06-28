package ch.seesturm.pfadiseesturm.util.types

enum class WordpressDocumentType {
    Documents,
    Luuchtturm;

    val title: String
        get() = when (this) {
            Documents -> "Dokumente"
            Luuchtturm -> "Lüüchtturm"
        }
}