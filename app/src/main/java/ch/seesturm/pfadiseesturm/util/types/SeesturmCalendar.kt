package ch.seesturm.pfadiseesturm.util.types

import kotlinx.serialization.Serializable

@Serializable
enum class SeesturmCalendar(
    val calendarId: String,
    val subscriptionUrl: String,
    val httpSubscriptionUrl: String
) {
    TERMINE(
        calendarId = "app@seesturm.ch",
        subscriptionUrl = "webcal://calendar.google.com/calendar/ical/app%40seesturm.ch/public/basic.ics",
        httpSubscriptionUrl = "https://calendar.google.com/calendar/ical/app%40seesturm.ch/public/basic.ics",
    ),
    TERMINE_LEITUNGSTEAM(
        calendarId = "5975051a11bea77feba9a0990756ae350a8ddc6ec132f309c0a06311b8e45ae1@group.calendar.google.com",
        subscriptionUrl = "webcal://calendar.google.com/calendar/ical/5975051a11bea77feba9a0990756ae350a8ddc6ec132f309c0a06311b8e45ae1%40group.calendar.google.com/public/basic.ics",
        httpSubscriptionUrl = "https://calendar.google.com/calendar/ical/5975051a11bea77feba9a0990756ae350a8ddc6ec132f309c0a06311b8e45ae1%40group.calendar.google.com/public/basic.ics"
    ),
    AKTIVITAETEN_BIBERSTUFE(
        calendarId = "c_7520d8626a32cf6eb24bff379717bb5c8ea446bae7168377af224fc502f0c42a@group.calendar.google.com",
        subscriptionUrl = "webcal://calendar.google.com/calendar/ical/c_7520d8626a32cf6eb24bff379717bb5c8ea446bae7168377af224fc502f0c42a%40group.calendar.google.com/public/basic.ics",
        httpSubscriptionUrl = "https://calendar.google.com/calendar/ical/c_7520d8626a32cf6eb24bff379717bb5c8ea446bae7168377af224fc502f0c42a%40group.calendar.google.com/public/basic.ics"
    ),
    AKTIVITAETEN_WOLFSSTUFE(
        calendarId = "c_e0edfd55e958543f4a4a370fdadcb5cec167e6df847fe362af9c0feb04069a0a@group.calendar.google.com",
        subscriptionUrl = "webcal://calendar.google.com/calendar/ical/c_e0edfd55e958543f4a4a370fdadcb5cec167e6df847fe362af9c0feb04069a0a%40group.calendar.google.com/public/basic.ics",
        httpSubscriptionUrl = "https://calendar.google.com/calendar/ical/c_e0edfd55e958543f4a4a370fdadcb5cec167e6df847fe362af9c0feb04069a0a%40group.calendar.google.com/public/basic.ics"
    ),
    AKTIVITAETEN_PFADISTUFE(
        calendarId = "c_753fcf01c8730c92dfc6be4fac8c4aa894165cf451a993413303eaf016b1647e@group.calendar.google.com",
        subscriptionUrl = "webcal://calendar.google.com/calendar/ical/c_753fcf01c8730c92dfc6be4fac8c4aa894165cf451a993413303eaf016b1647e%40group.calendar.google.com/public/basic.ics",
        httpSubscriptionUrl = "https://calendar.google.com/calendar/ical/c_753fcf01c8730c92dfc6be4fac8c4aa894165cf451a993413303eaf016b1647e%40group.calendar.google.com/public/basic.ics"
    ),
    AKTIVITAETEN_PIOSTUFE(
        calendarId = "c_be80dc194bbf418bea3a613472f9811df8887e07332a363d6d1ed66056f87f25@group.calendar.google.com",
        subscriptionUrl = "webcal://calendar.google.com/calendar/ical/c_be80dc194bbf418bea3a613472f9811df8887e07332a363d6d1ed66056f87f25%40group.calendar.google.com/public/basic.ics",
        httpSubscriptionUrl = "https://calendar.google.com/calendar/ical/c_be80dc194bbf418bea3a613472f9811df8887e07332a363d6d1ed66056f87f25%40group.calendar.google.com/public/basic.ics"
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