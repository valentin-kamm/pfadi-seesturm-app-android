package ch.seesturm.pfadiseesturm.util.types

import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmError

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

val String.getWeatherCondition: WeatherCondition
    get() {
        return requireNotNull(WeatherCondition.entries.find { it.conditionCode == this }) {
            throw PfadiSeesturmError.WeatherConditionError("Die Wetterbedingung ist unbekannt.")
        }
    }