package ch.seesturm.pfadiseesturm.util.navigation

import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.util.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.navigation.AppDestination.MainTabView.Destinations.Mehr.Destinations
import kotlinx.serialization.Serializable

@Serializable
sealed class AppDestination {

    @Serializable
    data object WelcomeScreen: AppDestination()

    @Serializable
    data object VersionCheckScreen: AppDestination()

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

                    // nested graph to share viewmodel
                    @Serializable
                    data class PhotosGraph(
                        val id: String,
                        val title: String
                    ): Destinations() {

                        @Serializable
                        data object Photos: Destinations()

                        @Serializable
                        data object PhotosSlider: Destinations()
                    }

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
                        val stufe: SeesturmStufe,
                        val openSheetUponNavigation: Boolean
                    ): Destinations()

                    @Serializable
                    data class Food(
                        val userId: String,
                        val calendar: SeesturmCalendar
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