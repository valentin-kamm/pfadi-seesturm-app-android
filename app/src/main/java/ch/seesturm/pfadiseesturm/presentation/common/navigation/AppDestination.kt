package ch.seesturm.pfadiseesturm.presentation.common.navigation

import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import kotlinx.serialization.Serializable

@Serializable
sealed class AppDestination {

    @Serializable
    data object Onboarding: AppDestination()

    @Serializable
    data object MainTabView: AppDestination() {

        @Serializable
        sealed class Destinations(val title: String) {

            @Serializable
            data object Home: Destinations(title = "Home") {
                @Serializable
                sealed class Destinations {

                    @Serializable
                    data object HomeRoot: Destinations()

                    @Serializable
                    data class AktuellDetail(
                        val postId: Int
                    ): Destinations()

                    @Serializable
                    data class AnlaesseDetail(
                        val calendar: SeesturmCalendar,
                        val eventId: String
                    ): Destinations()

                    @Serializable
                    data object PushNotifications: Destinations()

                    @Serializable
                    data class AktivitaetDetail(
                        val stufe: SeesturmStufe,
                        val eventId: String?
                    ): Destinations()

                    @Serializable
                    data object GespeichertePersonen: Destinations()
                }
            }

            @Serializable
            data object Aktuell: Destinations(title = "Aktuell") {
                @Serializable
                sealed class Destinations {

                    @Serializable
                    data object AktuellRoot: Destinations()

                    @Serializable
                    data class AktuellDetail(
                        val postId: Int
                    ): Destinations()

                    @Serializable
                    data object PushNotifications: Destinations()
                }
            }

            @Serializable
            data object Anlaesse: Destinations(title = "Anlässe") {
                @Serializable
                sealed class Destinations {

                    @Serializable
                    data object AnlaesseRoot: Destinations()

                    @Serializable
                    data class AnlaesseDetail(
                        val calendar: SeesturmCalendar,
                        val eventId: String
                    ) : Destinations()
                }
            }

            @Serializable
            data object Mehr: Destinations(title = "Mehr") {
                @Serializable
                sealed class Destinations {

                    @Serializable
                    data object MehrRoot: Destinations()

                    @Serializable
                    data object Dokumente: Destinations()

                    @Serializable
                    data object Leitungsteam: Destinations()

                    @Serializable
                    data object Luuchtturm: Destinations()

                    @Serializable
                    data object Pfadijahre: Destinations()

                    @Serializable
                    data class Albums(
                        val id: String,
                        val title: String
                    ): Destinations()

                    @Serializable
                    data class Photos(
                        val id: String,
                        val title: String
                    ): Destinations()

                    @Serializable
                    data object GespeichertePersonen: Destinations()

                    @Serializable
                    data object PushNotifications: Destinations()
                }
            }

            @Serializable
            data object Account: Destinations(title = "Account") {
                @Serializable
                sealed class Destinations {

                    @Serializable
                    data object AccountRoot: Destinations()

                    @Serializable
                    data object AccountTermine: Destinations()

                    @Serializable
                    data class AccountTermineDetail(
                        val cacheIdentifier: MemoryCacheIdentifier,
                        val calendar: SeesturmCalendar,
                        val eventId: String
                    ): Destinations()

                    @Serializable
                    data class Stufenbereich(
                        val stufe: SeesturmStufe
                    ): Destinations()

                    @Serializable
                    data class DisplayAktivitaet(
                        val stufe: SeesturmStufe,
                        val id: String
                    ): Destinations()

                    @Serializable
                    data class Food(
                        val userId: String,
                        val userDisplayNameShort: String,
                        val calendar: SeesturmCalendar
                    ): Destinations()

                    @Serializable
                    data class NewAktivitaet(
                        val stufe: SeesturmStufe,
                    ): Destinations()

                    @Serializable
                    data class UpdateAktivitaet(
                        val stufe: SeesturmStufe,
                        val id: String
                    ): Destinations()

                    @Serializable data class Templates(
                        val stufe: SeesturmStufe
                    ): Destinations()
                }
            }

            companion object {
                fun allInstances(): List<Destinations> = listOf(
                    Home,
                    Aktuell,
                    Anlaesse,
                    Mehr,
                    Account
                )
            }
        }
    }
}