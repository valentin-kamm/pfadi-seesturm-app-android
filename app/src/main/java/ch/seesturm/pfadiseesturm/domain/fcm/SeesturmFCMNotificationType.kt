package ch.seesturm.pfadiseesturm.domain.fcm

import ch.seesturm.pfadiseesturm.domain.fcm.model.SeesturmFCMNotificationContent
import ch.seesturm.pfadiseesturm.util.types.SchoepflialarmReactionType
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe

sealed class SeesturmFCMNotificationType {

    data class SchoepflialarmCustom(val userName: String, val body: String): SeesturmFCMNotificationType()
    data class SchoepflialarmGeneric(val userName: String): SeesturmFCMNotificationType()

    data class SchoepflialarmReactionGeneric(val userName: String, val type: SchoepflialarmReactionType): SeesturmFCMNotificationType()

    data class AktivitaetNew(val stufe: SeesturmStufe, val eventId: String): SeesturmFCMNotificationType()
    data class AktivitaetUpdate(val stufe: SeesturmStufe, val eventId: String): SeesturmFCMNotificationType()
    data class AktivitaetGeneric(val stufe: SeesturmStufe, val eventId: String): SeesturmFCMNotificationType()

    data class AktuellGeneric(val title: String, val body: String, val postId: String): SeesturmFCMNotificationType()

    val topic: SeesturmFCMNotificationTopic
        get() {
            return when (this) {
                is SchoepflialarmCustom, is SchoepflialarmGeneric -> SeesturmFCMNotificationTopic.Schoepflialarm
                is SchoepflialarmReactionGeneric -> SeesturmFCMNotificationTopic.SchoepflialarmReaction
                is AktivitaetGeneric -> stufe.aktivitaetNotificationTopic
                is AktivitaetNew -> stufe.aktivitaetNotificationTopic
                is AktivitaetUpdate -> stufe.aktivitaetNotificationTopic
                is AktuellGeneric -> SeesturmFCMNotificationTopic.Aktuell
            }
        }
    val content: SeesturmFCMNotificationContent
        get() {
            return when (this) {
                is SchoepflialarmCustom -> {
                    SeesturmFCMNotificationContent(
                        title = "$userName hat einen Schöpflialarm ausgelöst!",
                        body = body,
                        customKey = null
                    )
                }
                is SchoepflialarmGeneric -> {
                    SeesturmFCMNotificationContent(
                        title = "$userName hat einen Schöpflialarm ausgelöst!",
                        body = "Bitte umgehend im Schöpfli erscheinen.",
                        customKey = null
                    )
                }
                is SchoepflialarmReactionGeneric -> {
                    when (type) {
                        SchoepflialarmReactionType.Coming -> {
                            SeesturmFCMNotificationContent(
                                title = "Schöpflialarm",
                                body = "$userName ist auf dem Weg!",
                                customKey = null
                            )
                        }
                        SchoepflialarmReactionType.NotComing -> {
                            SeesturmFCMNotificationContent(
                                title = "Schöpflialarm",
                                body = "$userName kommt nicht!",
                                customKey = null
                            )
                        }
                        SchoepflialarmReactionType.AlreadyThere -> {
                            SeesturmFCMNotificationContent(
                                title = "Schöpflialarm",
                                body = "$userName ist schon da!",
                                customKey = null
                            )
                        }
                    }
                }
                is AktivitaetNew -> {
                    SeesturmFCMNotificationContent(
                        title = "${stufe.aktivitaetDescription} veröffentlicht",
                        body = "Die Infos zur ${stufe.aktivitaetDescription} sind online.",
                        customKey = eventId
                    )
                }
                is AktivitaetUpdate -> {
                    SeesturmFCMNotificationContent(
                        title = "${stufe.aktivitaetDescription} aktualisiert",
                        body = "Die Infos zur ${stufe.aktivitaetDescription} wurden aktualisiert.",
                        customKey = eventId
                    )
                }
                is AktivitaetGeneric -> {
                    SeesturmFCMNotificationContent(
                        title = "${stufe.aktivitaetDescription} veröffentlicht oder aktualisiert",
                        body = "Infos zur einer ${stufe.aktivitaetDescription} veröffentlicht oder aktualisiert.",
                        customKey = eventId
                    )
                }
                is AktuellGeneric -> {
                    SeesturmFCMNotificationContent(
                        title = title,
                        body = body,
                        customKey = postId
                    )
                }
            }
        }
}