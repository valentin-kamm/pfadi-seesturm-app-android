package ch.seesturm.pfadiseesturm.domain.fcm

import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmError
import kotlinx.serialization.Serializable

@Serializable
enum class SeesturmFCMNotificationTopic (
    val topic: String,
    val topicName: String
) {
    Schoepflialarm(
        topic = "schoepflialarm-2.0",
        topicName = "Schöpflialarm"
    ),
    SchoepflialarmReaction(
        topic = "schoepflialarmReaction-2.0",
        topicName = "Schöpflialarm Reaktionen"
    ),
    Aktuell(
        topic = "aktuell-2.0",
        topicName = "Aktuell"
    ),
    BiberAktivitaeten(
        topic = "aktivitaetBiberstufe-2.0",
        topicName = "Biberstufen-Aktivitäten"
    ),
    WolfAktivitaeten(
        topic = "aktivitaetWolfsstufe-2.0",
        topicName = "Wolfsstufen-Aktivitäten"
    ),
    PfadiAktivitaeten(
        topic = "aktivitaetPfadistufe-2.0",
        topicName = "Pfadistufen-Aktivitäten"
    ),
    PioAktivitaeten(
        topic = "aktivitaetPiostufe-2.0",
        topicName = "Piostufen-Aktivitäten"
    );

    val displayForAuthenticatedHitobitoUserOnly: Boolean
        get() = when (this) {
            Schoepflialarm, SchoepflialarmReaction -> true
            else -> false
        }

    companion object {
        fun fromTopicString(topic: String): SeesturmFCMNotificationTopic {
            return entries.firstOrNull { it.topic == topic }
                ?: throw PfadiSeesturmError.UnknownNotificationTopic("Die Push-Nachricht kann keinen Thema zugeordnet werden.")
        }
    }

    val targetTab: AppDestination.MainTabView.Destinations
        get() {
            return when (this) {
                Schoepflialarm, SchoepflialarmReaction -> AppDestination.MainTabView.Destinations.Account
                Aktuell -> AppDestination.MainTabView.Destinations.Aktuell
                BiberAktivitaeten, WolfAktivitaeten, PfadiAktivitaeten, PioAktivitaeten -> AppDestination.MainTabView.Destinations.Home
            }
        }
}