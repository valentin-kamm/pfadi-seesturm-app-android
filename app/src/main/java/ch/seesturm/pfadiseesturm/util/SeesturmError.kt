package ch.seesturm.pfadiseesturm.util


sealed interface SeesturmError {
    val defaultMessage: String
}

sealed interface DataError: SeesturmError {

    sealed class Network(override val defaultMessage: String) : DataError {
        data object UNKNOWN : Network("Unbekannter Netzwerkfehler.")
        data object INVALID_DATE : Network("Ungültiges Datumsformat in den vom Server bereitgestellten Daten.")
        data object INVALID_DATA : Network("Die vom Server bereitgestellten Daten sind ungültig.")
        data object IO_EXCEPTION : Network("Überprüfe deine Internetverbindung und versuche es erneut.")
        data object INVALID_WEATHER_CONDITION : Network("Die Wetterbedingung ist unbekannt.")
        data class INVALID_REQUEST(
            val httpCode: Int,
            val errorMessage: String?
        ) : Network(
            "Die Operation ist mit dem Code $httpCode fehlgeschlagen${if (errorMessage != null) " ($errorMessage)." else "."}"
        )
    }

    sealed class Local(override val defaultMessage: String) : DataError {
        data object UNKNOWN : Local("Unbekannter Fehler beim Bearbeiten der Daten.")
        data object SAVING_ERROR : Local("Die Daten sind fehlerhaft und konnten nicht gespeichert werden.")
        data object READING_ERROR : Local("Die Daten sind fehlerhaft und konnten nicht gelesen werden.")
        data object DELETING_ERROR : Local("Der Datensatz konnte nicht gelöscht werden.")
        data object INVALID_DATE : Local("Ungültiges Datumsformat.")
        data object INVALID_FORM_INPUT : Local("Die eingegebenen Daten sind unvollständig.")
    }

    sealed class RemoteDatabase(override val defaultMessage: String) : DataError {
        data object UNKNOWN : RemoteDatabase("Unbekannter Fehler beim Bearbeiten der Daten.")
        data object SAVING_ERROR : RemoteDatabase("Die Daten konnten nicht gespeichert werden.")
        data object DECODING_ERROR: RemoteDatabase("Die Daten sind ungültig und können nicht decodiert werden.")
        data class READING_ERROR(
            val message: String
        ): RemoteDatabase("Die Daten konnten nicht gelesen werden.")
        data object DOCUMENT_DOES_NOT_EXIST: RemoteDatabase("Dokument existiert nicht")
        data object DELETING_ERROR: RemoteDatabase("Dokument konnte nicht gelöscht werden.")
    }

    sealed class Messaging(
        override val defaultMessage: String
    ): DataError {
        data object UNKNOWN :
            Messaging("Beim Bearbeiten von Push-Nachrichten ist ein unbekannter Fehler aufgetreten.")
        data object PERMISSION_ERROR :
            Messaging("Um diese Funktion nutzen zu können, musst du Push-Nachrichten in den Einstellungen aktivieren.")
        data class SUBSCRIPTION_FAILED(
            val topic: SeesturmFCMNotificationTopic
        ) : Messaging("Anmeldung für ${topic.topicName} fehlgeschlagen.")
        data class UNSUBSCRIPTION_FAILED(
            val topic: SeesturmFCMNotificationTopic
        ) : Messaging("Abmeldung von ${topic.topicName} fehlgeschlagen.")
        data class DATA_SAVING_ERROR(
            val topic: SeesturmFCMNotificationTopic
        ): Messaging("Einstellung für ${topic.topicName} konnte nicht auf dem Gerät gespeichert werden.")
        data object DATA_READING_ERROR: Messaging("Einstellung für Push-Nachrichten konnten nicht abgerufen werden.")
    }

    sealed class AuthError(override val defaultMessage: String) : DataError {
        data class UNKNOWN(
            val message: String
        ) : AuthError(message)
        data class SIGN_IN_ERROR(
            val message: String
        ) : AuthError(message)
        data class SIGN_OUT_ERROR(
            val message: String
        ): AuthError(message)
        data class DELETE_ACCOUNT_ERROR(
            val message: String
        ): AuthError(message)
        data object CANCELLED: AuthError("Die Operation wurde durch den Benutzer abgebrochen.")
    }

    sealed class CloudFunctionsError(override val defaultMessage: String): DataError {
        data object INVALID_DATA: CloudFunctionsError("Die gesendeten oder empfangenen Daten sind ungültig.")
        data class UNKNOWN(
            val message: String
        ): CloudFunctionsError("Unbekannter Fehler: $message")
    }
}

sealed class PfadiSeesturmAppError(override val message: String): Exception(message) {
    class DateError(message: String): PfadiSeesturmAppError(message)
    class WeatherConditionError(message: String): PfadiSeesturmAppError(message)
    class InvalidFormInput(message: String): PfadiSeesturmAppError(message)
    class MessagingPermissionError(message: String): PfadiSeesturmAppError(message)
    class UnknownStufe(message: String): PfadiSeesturmAppError(message)
    class UnknownAktivitaetInteraction(message: String): PfadiSeesturmAppError(message)
    class AuthError(message: String): PfadiSeesturmAppError(message)
    class Cancelled(message: String): PfadiSeesturmAppError(message)
}