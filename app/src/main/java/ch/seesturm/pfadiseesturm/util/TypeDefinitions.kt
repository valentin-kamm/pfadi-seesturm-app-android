 package ch.seesturm.pfadiseesturm.util


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_BLUE
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_YELLOW
import ch.seesturm.pfadiseesturm.util.state.ActionState
import com.google.rpc.context.AttributeContext.Auth
import kotlinx.serialization.Serializable
import net.openid.appauth.AuthState

 sealed class SeesturmAuthState {
     data class SignedOut(val state: ActionState<Unit>) : SeesturmAuthState()
     data class SignedInWithHitobito(val user: FirebaseHitobitoUser, val state: ActionState<Unit>) :
         SeesturmAuthState()

     val signInButtonIsLoading: Boolean
         get() = when (this) {
             is SignedOut -> {
                 when (this.state) {
                     is ActionState.Loading -> {
                         true
                     }

                     else -> {
                         false
                     }
                 }
             }

             else -> {
                 false
             }
         }
     val deleteAccountButtonLoading: Boolean
         get() = when (this) {
             is SignedInWithHitobito -> {
                 when (this.state) {
                     is ActionState.Loading -> {
                         true
                     }

                     else -> {
                         false
                     }
                 }
             }

             else -> {
                 false
             }
         }
 }

 // FCM notification topics
 @Serializable
 enum class SeesturmFCMNotificationTopic (
     val topic: String,
     val topicName: String
 ) {
     Schoepflialarm(
         topic = "schoepflialarm-v2",
         topicName = "Schöpflialarm"
     ),
     SchoepflialarmReaktion(
         topic = "schoepflialarmReaktion-v2",
         topicName = "Schöpflialarm Reaktionen"
     ),
     Aktuell(
         topic = "aktuell-v2",
         topicName = "Aktuell"
     ),
     BiberAktivitaeten(
         topic = "aktivitaetBiberstufe-v2",
         topicName = "Biberstufen-Aktivitäten"
     ),
     WolfAktivitaeten(
         topic = "aktivitaetWolfsstufe-v2",
         topicName = "Wolfsstufen-Aktivitäten"
     ),
     PfadiAktivitaeten(
         topic = "aktivitaetPfadistufe-v2",
         topicName = "Pfadistufen-Aktivitäten"
     ),
     PioAktivitaeten(
         topic = "aktivitaetPiostufe-v2",
         topicName = "Piostufen-Aktivitäten"
     )
 }

 @Serializable
 enum class SeesturmStufe(
    val id: Int,
    val stufenName: String,
    val aktivitaetDescription: String,
    val calendar: SeesturmCalendar,
    val iconReference: Int,
    val color: Color,
    val highContrastColor: @Composable () -> Color,
    val allowedAktivitaetInteractions: List<AktivitaetInteraction>
 ) {
     Biber(
         id = 0,
         stufenName = "Biberstufe",
         aktivitaetDescription = "Biberstufen-Aktivität",
         calendar = SeesturmCalendar.AKTIVITAETEN_BIBERSTUFE,
         iconReference = R.drawable.biber,
         color = Color.SEESTURM_RED,
         highContrastColor = { Color.SEESTURM_RED },
         allowedAktivitaetInteractions = listOf(AktivitaetInteraction.ABMELDEN, AktivitaetInteraction.ANMELDEN)
     ),
     Wolf(
         id = 1,
         stufenName = "Wolfsstufe",
         aktivitaetDescription = "Wolfsstufen-Aktivität",
         calendar = SeesturmCalendar.AKTIVITAETEN_WOLFSSTUFE,
         iconReference = R.drawable.wolf,
         color = Color.SEESTURM_YELLOW,
         highContrastColor = { MaterialTheme.colorScheme.onBackground },
         allowedAktivitaetInteractions = listOf(AktivitaetInteraction.ABMELDEN)
     ),
     Pfadi(
         id = 2,
         stufenName = "Pfadistufe",
         aktivitaetDescription = "Pfadistufen-Aktivität",
         calendar = SeesturmCalendar.AKTIVITAETEN_PFADISTUFE,
         iconReference = R.drawable.pfadi,
         color = Color.SEESTURM_BLUE,
         highContrastColor = { Color.SEESTURM_BLUE },
         allowedAktivitaetInteractions = listOf(AktivitaetInteraction.ABMELDEN)
     ),
     Pio(
         id = 3,
         stufenName = "Piostufe",
         aktivitaetDescription = "Piostufen-Aktivität",
         calendar = SeesturmCalendar.AKTIVITAETEN_PIOSTUFE,
         iconReference = R.drawable.pio,
         color = Color.SEESTURM_GREEN,
         highContrastColor = { Color.SEESTURM_GREEN },
         allowedAktivitaetInteractions = listOf(AktivitaetInteraction.ABMELDEN)
     );
     companion object {
         fun fromId(id: Int): SeesturmStufe {
             return SeesturmStufe.entries.find { it.id == id } ?: throw PfadiSeesturmAppError.UnknownStufe("Unbekannte Stufe.")
         }
     }
 }
 enum class AktivitaetInteraction(
     val id: Int,
     val nomen: String,
     val nomenMehrzahl: String,
     val verb: String,
     val taetigkeit: String,
     val icon: ImageVector,
     val color: Color
 ) {
    ANMELDEN(
        id = 1,
        nomen = "Anmeldung",
        nomenMehrzahl = "Anmeldungen",
        verb = "anmelden",
        taetigkeit = "Angemeldet",
        icon = Icons.Outlined.CheckCircle,
        color = Color.SEESTURM_GREEN
    ),
    ABMELDEN(
        id = 0,
        nomen = "Abmeldung",
        nomenMehrzahl = "Abmeldungen",
        verb = "abmelden",
        taetigkeit = "Abgemeldet",
        icon = Icons.Outlined.Cancel,
        color = Color.SEESTURM_RED
    );
    companion object {
        fun fromId(id: Int): AktivitaetInteraction {
            return entries.find { it.id == id } ?: throw PfadiSeesturmAppError.UnknownAktivitaetInteraction("Unbekannte An-/Abmelde-Art.")
        }
    }
 }

 // top app bar style
 enum class TopBarStyle {
     Large,
     Small
 }

 // used for caching in repositories
 @Serializable
 enum class MemoryCacheIdentifier {
     Push,
     List,
     Home
 }

 // calendars
 @Serializable
 enum class SeesturmCalendar(
     val calendarId: String,
     val subscriptionUrl: String
 ) {
     TERMINE(
         "app@seesturm.ch",
         "webcal://calendar.google.com/calendar/ical/app%40seesturm.ch/public/basic.ics"
     ),
     TERMINE_LEITUNGSTEAM(
         "5975051a11bea77feba9a0990756ae350a8ddc6ec132f309c0a06311b8e45ae1@group.calendar.google.com",
         "webcal://calendar.google.com/calendar/ical/5975051a11bea77feba9a0990756ae350a8ddc6ec132f309c0a06311b8e45ae1%40group.calendar.google.com/public/basic.ics"
     ),
     AKTIVITAETEN_BIBERSTUFE(
        "c_7520d8626a32cf6eb24bff379717bb5c8ea446bae7168377af224fc502f0c42a@group.calendar.google.com",
         "webcal://calendar.google.com/calendar/ical/c_7520d8626a32cf6eb24bff379717bb5c8ea446bae7168377af224fc502f0c42a%40group.calendar.google.com/public/basic.ics"
     ),
     AKTIVITAETEN_WOLFSSTUFE(
        "c_e0edfd55e958543f4a4a370fdadcb5cec167e6df847fe362af9c0feb04069a0a@group.calendar.google.com",
         "webcal://calendar.google.com/calendar/ical/c_e0edfd55e958543f4a4a370fdadcb5cec167e6df847fe362af9c0feb04069a0a%40group.calendar.google.com/public/basic.ics"
     ),
     AKTIVITAETEN_PFADISTUFE(
         "c_753fcf01c8730c92dfc6be4fac8c4aa894165cf451a993413303eaf016b1647e@group.calendar.google.com",
         "webcal://calendar.google.com/calendar/ical/c_753fcf01c8730c92dfc6be4fac8c4aa894165cf451a993413303eaf016b1647e%40group.calendar.google.com/public/basic.ics"
     ),
     AKTIVITAETEN_PIOSTUFE(
         "c_be80dc194bbf418bea3a613472f9811df8887e07332a363d6d1ed66056f87f25@group.calendar.google.com",
         "webcal://calendar.google.com/calendar/ical/c_be80dc194bbf418bea3a613472f9811df8887e07332a363d6d1ed66056f87f25%40group.calendar.google.com/public/basic.ics"
     );

     val isLeitungsteam: Boolean
         get() = when (this) {
             TERMINE_LEITUNGSTEAM -> {
                 true
             }
             else -> {
                 false
             }
         }
 }

 enum class WeatherCondition(
     val conditionCode: String,
     val description: String,
     val lightIconId: Int,
     val darkIconId: Int
 ) {
     BLIZZARD(
         conditionCode = "Blizzard",
         description = "Schneesturm",
         lightIconId = R.drawable.blizzard,
         darkIconId = R.drawable.blizzard_dark
     ),
     BLOWING_DUST(
         conditionCode = "BlowingDust",
         description = "Staub",
         lightIconId = R.drawable.blowing_dust,
         darkIconId = R.drawable.blowing_dust
     ),
     BLOWING_SNOW(
         conditionCode = "BlowingSnow",
         description = "Schneetreiben",
         lightIconId = R.drawable.blizzard,
         darkIconId = R.drawable.blizzard_dark
     ),
     BREEZY(
         conditionCode = "Breezy",
         description = "Leichter Wind",
         lightIconId = R.drawable.windy,
         darkIconId = R.drawable.windy_dark
     ),
     CLEAR(
         conditionCode = "Clear",
         description = "Wolkenlos",
         lightIconId = R.drawable.clear,
         darkIconId = R.drawable.clear
     ),
     CLOUDY(
         conditionCode = "Cloudy",
         description = "Bewölkt",
         lightIconId = R.drawable.cloudy,
         darkIconId = R.drawable.cloudy_dark
     ),
     DRIZZLE(
         conditionCode = "Drizzle",
         description = "Nieselregen",
         lightIconId = R.drawable.hail,
         darkIconId = R.drawable.hail_dark
     ),
     FLURRIES(
         conditionCode = "Flurries",
         description = "Schneegestöber",
         lightIconId = R.drawable.flurries,
         darkIconId = R.drawable.flurries_dark
     ),
     FOGGY(
         conditionCode = "Foggy",
         description = "Nebel",
         lightIconId = R.drawable.foggy,
         darkIconId = R.drawable.foggy_dark
     ),
     FREEZING_DRIZZLE(
         conditionCode = "FreezingDrizzle",
         description = "Gefrierender Nieselregen",
         lightIconId = R.drawable.freezing_drizzle,
         darkIconId = R.drawable.freezing_drizzle_dark
     ),
     FREEZING_RAIN(
         conditionCode = "FreezingRain",
         description = "Gefrierender Regen",
         lightIconId = R.drawable.freezing_drizzle,
         darkIconId = R.drawable.freezing_drizzle_dark
     ),
     FRIGID(
         conditionCode = "Frigid",
         description = "Kalt",
         lightIconId = R.drawable.frigid,
         darkIconId = R.drawable.frigid_dark
     ),
     HAIL(
         conditionCode = "Hail",
         description = "Hagel",
         lightIconId = R.drawable.hail,
         darkIconId = R.drawable.hail_dark
     ),
     HAZE(
         conditionCode = "Haze",
         description = "Dunst",
         lightIconId = R.drawable.foggy,
         darkIconId = R.drawable.foggy_dark
     ),
     HEAVY_RAIN(
         conditionCode = "HeavyRain",
         description = "Starker Regen",
         lightIconId = R.drawable.heavy_rain,
         darkIconId = R.drawable.heavy_rain_dark
     ),
     HEAVY_SNOW(
         conditionCode = "HeavySnow",
         description = "Starker Schneefall",
         lightIconId = R.drawable.snow,
         darkIconId = R.drawable.snow_dark
     ),
     HOT(
         conditionCode = "Hot",
         description = "Heiss",
         lightIconId = R.drawable.hot,
         darkIconId = R.drawable.hot
     ),
     HURRICANE(
         conditionCode = "Hurricane",
         description = "Orkan",
         lightIconId = R.drawable.hurricane,
         darkIconId = R.drawable.hurricane_dark
     ),
     ISOLATED_THUNDERSTORMS(
         conditionCode = "IsolatedThunderstorms",
         description = "Örtliche Gewitter",
         lightIconId = R.drawable.thunderstorms,
         darkIconId = R.drawable.thunderstorms_dark
     ),
     MOSTLY_CLEAR(
         conditionCode = "MostlyClear",
         description = "Meist wolkenlos",
         lightIconId = R.drawable.mostly_clear,
         darkIconId = R.drawable.mostly_clear_dark
     ),
     MOSTLY_CLOUDY(
         conditionCode = "MostlyCloudy",
         description = "Meist bewölkt",
         lightIconId = R.drawable.cloudy,
         darkIconId = R.drawable.cloudy_dark
     ),
     PARTLY_CLOUDY(
         conditionCode = "PartlyCloudy",
         description = "Teils bewölkt",
         lightIconId = R.drawable.mostly_clear,
         darkIconId = R.drawable.mostly_clear_dark
     ),
     RAIN(
         conditionCode = "Rain",
         description = "Regen",
         lightIconId = R.drawable.heavy_rain,
         darkIconId = R.drawable.heavy_rain_dark
     ),
     SCATTERED_THUNDERSTORMS(
         conditionCode = "ScatteredThunderstorms",
         description = "Vereinzelte Gewitter",
         lightIconId = R.drawable.thunderstorms,
         darkIconId = R.drawable.thunderstorms_dark
     ),
     SLEET(
         conditionCode = "Sleet",
         description = "Graupel",
         lightIconId = R.drawable.hail,
         darkIconId = R.drawable.hail_dark
     ),
     SMOKY(
         conditionCode = "Smoky",
         description = "Rauch",
         lightIconId = R.drawable.foggy,
         darkIconId = R.drawable.foggy_dark
     ),
     SNOW(
         conditionCode = "Snow",
         description = "Schnee",
         lightIconId = R.drawable.snow,
         darkIconId = R.drawable.snow_dark
     ),
     STRONG_STORMS(
         conditionCode = "StrongStorms",
         description = "Starkes Gewitter",
         lightIconId = R.drawable.thunderstorms,
         darkIconId = R.drawable.thunderstorms_dark
     ),
     SUN_FLURRIES(
         conditionCode = "SunFlurries",
         description = "Sonnengestöber",
         lightIconId = R.drawable.sun_flurries,
         darkIconId = R.drawable.sun_flurries_dark
     ),
     SUN_SHOWERS(
         conditionCode = "SunShowers",
         description = "Sonnenregen",
         lightIconId = R.drawable.sun_showers,
         darkIconId = R.drawable.sun_showers_dark
     ),
     THUNDERSTORMS(
         conditionCode = "Thunderstorms",
         description = "Gewitter",
         lightIconId = R.drawable.thunderstorms,
         darkIconId = R.drawable.thunderstorms_dark
     ),
     TROPICAL_STORM(
         conditionCode = "TropicalStorm",
         description = "Tropensturm",
         lightIconId = R.drawable.hurricane,
         darkIconId = R.drawable.hurricane_dark
     ),
     WINDY(
         conditionCode = "Windy",
         description = "Windig",
         lightIconId = R.drawable.windy,
         darkIconId = R.drawable.windy_dark
     ),
     WINTRY_MIX(
         conditionCode = "WintryMix",
         description = "Regen & Schnee",
         lightIconId = R.drawable.wintry_mix,
         darkIconId = R.drawable.wintry_mix_dark
     )
 }
 fun String.getWeatherConditionByCode(code: String): WeatherCondition {
     return requireNotNull(WeatherCondition.entries.find { it.conditionCode == code }) {
         throw PfadiSeesturmAppError.WeatherConditionError("Die Wetterbedingung ist unbekannt.")
     }
 }