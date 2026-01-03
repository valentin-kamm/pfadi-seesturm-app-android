package ch.seesturm.pfadiseesturm.util

import ch.seesturm.pfadiseesturm.data.firestore.dto.FirebaseHitobitoUserDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventStartEndDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WeatherDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressDocumentDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toGoogleCalendarEvent
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toWeather
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toWordpressDocument
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.data_store.model.GespeichertePerson
import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetAnAbmeldung
import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetTemplate
import ch.seesturm.pfadiseesturm.domain.firestore.model.FoodOrder
import ch.seesturm.pfadiseesturm.domain.firestore.model.Schoepflialarm
import ch.seesturm.pfadiseesturm.domain.firestore.model.SchoepflialarmReaction
import ch.seesturm.pfadiseesturm.domain.wordpress.model.LeitungsteamMember
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressDocument
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPost
import ch.seesturm.pfadiseesturm.util.types.AktivitaetInteractionType
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import ch.seesturm.pfadiseesturm.util.types.SchoepflialarmReactionType
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import com.google.firebase.Timestamp
import com.google.gson.Gson
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

object DummyData {

    val oldDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(1739297798000), ZoneId.systemDefault())
    val mediumDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(1742297798000), ZoneId.systemDefault())
    val newDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(1749297798000), ZoneId.systemDefault())

    val oldDateFormatted = DateTimeUtil.shared.formatDate(date = oldDate, format = "dd.MM.yyyy", type = DateFormattingType.Absolute)
    val mediumDateFormatted = DateTimeUtil.shared.formatDate(date = mediumDate, format = "dd.MM.yyyy", type = DateFormattingType.Absolute)
    val newDateFormatted = DateTimeUtil.shared.formatDate(date = newDate, format = "dd.MM.yyyy", type = DateFormattingType.Absolute)

    val user1 = FirebaseHitobitoUser.from(
        FirebaseHitobitoUserDto(
            id = "123",
            created = Timestamp.now(),
            modified = Timestamp.now(),
            email = "test@test.ch",
            firstname = "Peter",
            lastname = "Müller",
            pfadiname = "Tarantula",
            role = "hitobito_user",
            profilePictureUrl = null,
        fcmToken = null
        )
    )

    val user2 = FirebaseHitobitoUser.from(
        FirebaseHitobitoUserDto(
            id = "456",
            created = Timestamp.now(),
            modified = Timestamp.now(),
            email = "test@test2.ch",
            firstname = "Maia",
            lastname = "Tanner",
            pfadiname = null,
            role = "hitobito_user",
            profilePictureUrl = null,
            fcmToken = null
        )
    )

    val user3 = FirebaseHitobitoUser.from(
        FirebaseHitobitoUserDto(
            id = "789",
            created = Timestamp.now(),
            modified = Timestamp.now(),
            email = "test@test3.ch",
            firstname = "Hans",
            lastname = "Blatter",
            pfadiname = "Elch",
            role = "hitobito_user",
            profilePictureUrl = "https://s3.eu-west-2.amazonaws.com/img.creativepool.com/files/candidate/portfolio/_w680/641887.jpg",
            fcmToken = null
        )
    )

    val documents = Json.decodeFromString<List<WordpressDocumentDto>>(
        """
            [
                  {
                    "id": "24644",
                    "thumbnailUrl": "https://seesturm.ch/wp-content/uploads/2025/05/Infobroschuere-pdf-212x300.jpg",
                    "thumbnailWidth": 212,
                    "thumbnailHeight": 300,
                    "title": "Infobroschüre Pfadi Seesturm",
                    "url": "https://seesturm.ch/wp-content/uploads/2025/05/Infobroschuere.pdf",
                    "published": "2025-05-12T19:08:28+00:00"
                  },
                  {
                    "id": "24261",
                    "thumbnailUrl": "https://seesturm.ch/wp-content/uploads/2024/12/Jahresprogramm_2025-pdf-212x300.jpg",
                    "thumbnailWidth": 212,
                    "thumbnailHeight": 300,
                    "title": "Jahresprogramm 2025",
                    "url": "https://seesturm.ch/wp-content/uploads/2024/12/Jahresprogramm_2025.pdf",
                    "published": "2024-12-17T20:13:55+00:00"
                  },
                  {
                    "id": "23410",
                    "thumbnailUrl": "https://seesturm.ch/wp-content/uploads/2024/05/Beitrittserkaerung-pdf-212x300.jpg",
                    "thumbnailWidth": 212,
                    "thumbnailHeight": 300,
                    "title": "Beitrittserklärung Pfadi Seesturm",
                    "url": "https://seesturm.ch/wp-content/uploads/2024/05/Beitrittserkaerung.pdf",
                    "published": "2024-05-07T19:53:53+00:00"
                  },
                  {
                    "id": "18896",
                    "thumbnailUrl": "https://seesturm.ch/wp-content/uploads/2022/04/190404_Infobroschuere-Pfadi-Thurgau-pdf-212x300.jpg",
                    "thumbnailWidth": 212,
                    "thumbnailHeight": 300,
                    "title": "Infobroschüre Pfadi Thurgau",
                    "url": "https://seesturm.ch/wp-content/uploads/2022/04/190404_Infobroschuere-Pfadi-Thurgau.pdf",
                    "published": "2022-04-22T13:26:20+00:00"
                  }
                ]
        """.trimIndent()
    ).map { it.toWordpressDocument() }

    val foodOrders: List<FoodOrder> = listOf(
        FoodOrder(
            id = UUID.randomUUID().toString(),
            itemDescription = "Döner",
            totalCount = 1,
            userIds = listOf("123"),
            users = listOf(user1),
            ordersString = "Döner 1x Test"
        ),
        FoodOrder(
            id = UUID.randomUUID().toString(),
            itemDescription = "Dürüm",
            totalCount = 2,
            userIds = listOf("123", "789"),
            users = listOf(user1, user3),
            ordersString = "Dürüm 2x Test"
        ),
        FoodOrder(
            id = UUID.randomUUID().toString(),
            itemDescription = "Pizza",
            totalCount = 1,
            userIds = listOf("456"),
            users = listOf(user2),
            ordersString = "Dürüm 1x Test"
        )
    )

    val schoepflialarmReaction1 = SchoepflialarmReaction(
        id = UUID.randomUUID().toString(),
        created = oldDate,
        modified = oldDate,
        createdFormatted = oldDateFormatted,
        modifiedFormatted = oldDateFormatted,
        user = user1,
        reaction = SchoepflialarmReactionType.Coming
    )
    val schoepflialarmReaction2 = SchoepflialarmReaction(
        id = UUID.randomUUID().toString(),
        created = mediumDate,
        modified = mediumDate,
        createdFormatted = mediumDateFormatted,
        modifiedFormatted = mediumDateFormatted,
        user = user2,
        reaction = SchoepflialarmReactionType.NotComing
    )
    val schoepflialarmReaction3 = SchoepflialarmReaction(
        id = UUID.randomUUID().toString(),
        created = newDate,
        modified = newDate,
        createdFormatted = newDateFormatted,
        modifiedFormatted = newDateFormatted,
        user = user3,
        reaction = SchoepflialarmReactionType.Coming
    )

    val schoepflialarm = Schoepflialarm(
        id = UUID.randomUUID().toString(),
        created = oldDate,
        modified = oldDate,
        createdFormatted = oldDateFormatted,
        modifiedFormatted = oldDateFormatted,
        message = "Testalarm für Preview in Android Studio und XCode (extra ein bisschen länger)",
        user = user1,
        reactions = listOf(schoepflialarmReaction1, schoepflialarmReaction2, schoepflialarmReaction3)
    )

    val oneDayEvent = GoogleCalendarEventDto(
        id = "049i70bbetjb6j9nqi9in866bl",
        summary = "Waldweihnachten \uD83C\uDF84",
        description = "Die traditionelle Waldweihnacht der Pfadi Seesturm kann dieses Jahr hoffentlich wieder in gewohnter Form stattfinden. Die genauen Zeiten werden später kommuniziert.",
        location = "im Wald",
        created = "2022-08-28T15:34:26.000Z",
        updated = "2022-08-28T15:34:26.247Z",
        start = GoogleCalendarEventStartEndDto(
            dateTime = "2022-12-17T15:00:00Z",
            date = null
        ),
        end = GoogleCalendarEventStartEndDto(
            dateTime = "2022-12-17T18:00:00Z",
            date = null
        )
    ).toGoogleCalendarEvent()

    val multiDayEvent = GoogleCalendarEventDto(
        id = "0nl482v21encap40tg8ecmomra",
        summary = "Wolfstufen-Weekend Wolfstufen-Weekend Wolfstufen-Weekend",
        description = "Ein erlebnisreiches Pfadiwochenende für alle Teilnehmenden der Wolfstufe",
        location = null,
        created = "2023-11-26T08:55:10.000Z",
        updated = "2023-11-26T08:55:10.887Z",
        start = GoogleCalendarEventStartEndDto(
            dateTime = "2024-02-24T10:00:00Z",
            date = null
        ),
        end = GoogleCalendarEventStartEndDto(
            dateTime = "2024-02-25T15:00:00Z",
            date = null
        )
    ).toGoogleCalendarEvent()

    val allDayOneDayEvent = GoogleCalendarEventDto(
        id = "4dai6m9r247vdl3t1oehi9arb0",
        summary = "Nationaler Pfadischnuppertag",
        description = null,
        location = null,
        created = "2024-11-16T14:49:15.000Z",
        updated = "2024-11-16T14:49:15.791Z",
        start = GoogleCalendarEventStartEndDto(
            dateTime = null,
            date = "2025-03-15"
        ),
        end = GoogleCalendarEventStartEndDto(
            dateTime = null,
            date = "2025-03-16"
        )
    ).toGoogleCalendarEvent()

    val allDayMultiDayEvent = GoogleCalendarEventDto(
        id = "1p5bqoco2c1nhejhv6h0jn72mk",
        summary = "Sommerlager Pfadi- und Piostufe",
        description = "Das alljährliche Sommerlager der Pfadi Seesturm ist eines der grössten Pfadi-Highlights. Sei auch du dabei und verbringe 11 abenteuerliche Tage im Zelt.",
        location = null,
        created = "2022-11-20T11:16:53.000Z",
        updated = "2022-11-20T11:16:53.083Z",
        start = GoogleCalendarEventStartEndDto(
            dateTime = null,
            date = "2023-09-24"
        ),
        end = GoogleCalendarEventStartEndDto(
            dateTime = null,
            date = "2023-10-05"
        )
    ).toGoogleCalendarEvent()

    val aktivitaetTemplate1 = AktivitaetTemplate(
        id = UUID.randomUUID().toString(),
        created = newDate,
        modified = newDate,
        stufe = SeesturmStufe.Pio,
        description = """
            <div>
            <div>
            <div><b>Anfang</b>: 10:00 Uhr, Pfadiheim</div>
            </div>
            <div><b>Ende</b>: 12:00 Uhr, Pfadiheim</div>
            <div><b>Motto</b>: Süess oder salzig?</div>
            <div><b>Mitnehmen</b>: Z’Trinke, z'Nüni, Finke</div>
            <div><b>Kleidung</b>: dem Wetter entsprechend</div>
            <div>&nbsp;</div>
            </div>
            <div style="caret-color: #000000; color: #000000; font-style: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: auto; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: auto; word-spacing: 0px; -webkit-tap-highlight-color: rgba(26, 26, 26, 0.3); -webkit-text-size-adjust: auto; -webkit-text-stroke-width: 0px; text-decoration: none;">
            <div>&nbsp;</div>
            <div><b>Fragen/Anmeldung:</b></div>
            <div>Dominique Vogt v/o Mulan</div>
            <div><a href="mailto:mulan@seesturm.ch" target="_blank" rel="noopener">mulan@seesturm.ch</a></div>
            <div>Oder direkt in der Pfadi Seesturm App</div>
            </div>
            <div>&nbsp;</div>
        """.trimIndent(),
        swipeActionsRevealed = false
    )
    val aktivitaetTemplate2 = AktivitaetTemplate(
        id = UUID.randomUUID().toString(),
        created = mediumDate,
        modified = mediumDate,
        stufe = SeesturmStufe.Pio,
        description = """
            <div>&nbsp;</div>
            <div>&nbsp;</div>
            <div><b>Melde dich jetzt für das Pfila an! Hier findest du die&nbsp;<a href="https://1drv.ms/b/c/14a946e93845aa27/ERY7Ge-INglIk4Iv3M7nnAUB2YLBbUgqPMB6Bdh_J2PUKg">Anmeldung fürs Pfila</a>.&nbsp;</b></div>
            <div><b>&nbsp;</b></div>
            <div><b>Anfang</b>: 10:00 Pfadiheim</div>
            <div><b>Ende</b>: 12:00 Pfadiheim</div>
            <div><b>Motto</b>: Tag der guten Tat!</div>
            <div><b>Mitnehmen</b>: Sackmesser, pro Kind einen 6er-Eierkarton leer, einen kleinen Plastiksack, Z' Trinken</div>
            <div><b>Anziehen</b>: Pfadikrawatte, Zeckenschutz, dem Wetter entsprechend</div>
            <div>&nbsp;</div>
            <div style="caret-color: #000000; color: #000000; font-style: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: auto; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: auto; word-spacing: 0px; -webkit-tap-highlight-color: rgba(26, 26, 26, 0.3); -webkit-text-size-adjust: auto; -webkit-text-stroke-width: 0px; text-decoration: none;">
            <div>
            <div>————————–</div>
            </div>
            <div><b>Fragen und Abmeldung:</b></div>
            <div>Ladina Kobler v/o Chili</div>
            <div>Tel: 078 734 53 85</div>
            <div>oder in der Pfadi Seesturm App</div>
            </div>
            <div>&nbsp;</div>
        """.trimIndent(),
        swipeActionsRevealed = false
    )

    val aktivitaet1 = GoogleCalendarEventDto(
        id = "17v15laf167s75oq47elh17a3t",
        summary = "Biberstufen-Aktivität",
        description = "Ob uns wohl der Pfadi-Chlaus dieses Jahr wieder viele Nüssli und Schöggeli bringt? Die genauen Zeiten werden später kommuniziert.",
        location = "Geiserparkplatz",
        created = "2022-08-28T15:25:45.701Z",
        updated = "2022-08-28T15:19:45.726Z",
        start = GoogleCalendarEventStartEndDto(
            dateTime = "2025-04-10T13:00:00Z",
            date = null
        ),
        end = GoogleCalendarEventStartEndDto(
            dateTime = "2025-04-10T15:00:00Z",
            date = null
        )
    ).toGoogleCalendarEvent()
    val aktivitaet2 = GoogleCalendarEventDto(
        id = "17v15laf167s75dfgdoq47elh17a3t",
        summary = "Biberstufen-Aktivität",
        description = "Ob uns wohl der Pfadi-Chlaus dieses Jahr wieder viele Nüssli und Schöggeli bringt? Die genauen Zeiten werden später kommuniziert.",
        location = "Geiserparkplatz",
        created = "2022-08-28T15:25:45.701Z",
        updated = "2022-08-28T15:19:45.726Z",
        start = GoogleCalendarEventStartEndDto(
            dateTime = "2025-04-16T13:00:00Z",
            date = null
        ),
        end = GoogleCalendarEventStartEndDto(
            dateTime = "2025-04-16T15:00:00Z",
            date = null
        )
    ).toGoogleCalendarEvent()

    val abmeldung1 = AktivitaetAnAbmeldung(
        id = "xcvxfdsfgdsf",
        eventId = "17v15laf167s75oq47elh17a3t",
        uid = null,
        vorname = "Seppli",
        nachname = "Meier",
        type = AktivitaetInteractionType.ABMELDEN,
        stufe = SeesturmStufe.Biber,
        created = ZonedDateTime.now(),
        modified = ZonedDateTime.now(),
        createdString = "Heute",
        modifiedString = "Morgen",
        pfadiname = null,
        bemerkung = null
    )
    val abmeldung2 = AktivitaetAnAbmeldung(
        id = "423wewerwer",
        eventId = "17v15laf167s75oq47elh17a3tsdfsf",
        uid = null,
        vorname = "Peter",
        nachname = "Fatzer",
        type = AktivitaetInteractionType.ANMELDEN,
        stufe = SeesturmStufe.Biber,
        created = ZonedDateTime.now(),
        modified = ZonedDateTime.now(),
        createdString = "Heute",
        modifiedString = "Morgen",
        pfadiname = null,
        bemerkung = null
    )
    val abmeldung3 = AktivitaetAnAbmeldung(
        id = "23423",
        eventId = "17v15laf167s75oq47elh17a3t",
        uid = null,
        vorname = "Hans",
        nachname = "Müller",
        type = AktivitaetInteractionType.ABMELDEN,
        stufe = SeesturmStufe.Wolf,
        created = ZonedDateTime.now(),
        modified = ZonedDateTime.now(),
        createdString = "Heute",
        modifiedString = "Morgen",
        pfadiname = null,
        bemerkung = null
    )

    val aktuellPost1 = WordpressPost(
        id = 2232566,
        publishedYear = "2023",
        publishedFormatted = "2023-06-28T16:29:56+00:00",
        modifiedFormatted = "2023-06-28T16:35:44+00:00",
        imageUrl = "https://seesturm.ch/wp-content/gallery/sola-2021-pfadi-piostufe/DSC1080.jpg",
        title = "Erinnerung = Anmeldung KaTre noch bis am 1. Juli",
        titleDecoded = "Erinnerung = Anmeldung KaTre noch bis am 1. Juli",
        content = "\n<p>Über 1000 Pfadis aus dem ganzen Thurgau treffen sich jährlich zum Kantonalen Pfaditreffen (KaTre) \u2013 ein Höhepunkt im Kalender der Pfadi Thurgau. Dieses Jahr findet der Anlass erstmals seit 2019 wieder statt, und zwar am Wochenende <strong>vom 23. und 24. September</strong> unter dem Motto <strong>«Die Piraten vom Bodamicus»</strong>.</p>\n\n\n\n<p>Das KaTre 2023 findet ganz in der Nähe statt, nämlich in <strong>Romanshorn direkt am schönen Bodensee</strong>. Es wird von der Pfadi Seesturm, gemeinsam mit den Pfadi-Abteilungen aus Arbon und Romanshorn, organisiert. Die Biber- und Wolfsstufe werden das KaTre am Sonntag besuchen, während die Pfadi- und Piostufe das ganze Wochenende «Pfadi pur» erleben dürfen. Weitere Informationen zum KaTre 2023 findet ihr unter <a rel=\"noreferrer noopener\" href=\"http: //www.katre.ch\" target=\"_blank\">www.katre.ch</a> oder in unserem Mail vom 2. Juni.</p>\n\n\n\n<p>Leider haben wir bisher erst sehr <strong>wenige Anmeldungen</strong> erhalten. Es würde uns sehr freuen, wenn sich noch möglichst viele Seestürmlerinnen und Seestürmler aller Stufen anmelden. Füllt dazu einfach das <a href=\"https: //seesturm.ch/wp-content/uploads/2023/06/KaTre-23__Anmeldetalon.pdf\" target=\"_blank\" rel=\"noreferrer noopener\">Anmeldeformular</a> aus und sendet es <strong>bis am 01. Juli</strong> an <a href=\"mailto: al@seesturm.ch\" target=\"_blank\" rel=\"noreferrer noopener\">al@seesturm.ch</a>.</p>\n\n\n\n<p>Danke!</p>\n",
        contentPlain = "Über 1000 Pfadis aus dem ganzen Thurgau treffen sich jährlich zum Kantonalen Pfaditreffen (KaTre) \u2013 ein Höhepunkt im Kalender der Pfadi Thurgau. Dieses Jahr findet der Anlass erstmals seit 2019 wieder statt, und zwar am Wochenende vom 23. und 24. September unter dem Motto «Die Piraten vom Bodamicus».\n\n\n\nDas KaTre 2023 findet ganz in der Nähe statt, nämlich in Romanshorn direkt am schönen Bodensee. Es wird von der Pfadi Seesturm, gemeinsam mit den Pfadi-Abteilungen aus Arbon und Romanshorn, organisiert. Die Biber- und Wolfsstufe werden das KaTre am Sonntag besuchen, während die Pfadi- und Piostufe das ganze Wochenende «Pfadi pur» erleben dürfen. Weitere Informationen zum KaTre 2023 findet ihr unter www.katre.ch oder in unserem Mail vom 2. Juni.\n\n\n\nLeider haben wir bisher erst sehr wenige Anmeldungen erhalten. Es würde uns sehr freuen, wenn sich noch möglichst viele Seestürmlerinnen und Seestürmler aller Stufen anmelden. Füllt dazu einfach das Anmeldeformular aus und sendet es bis am 01. Juli an al@seesturm.ch.\n\n\n\nDanke!",
        imageAspectRatio = 5568.0/3712.0,
        author = "seesturm"
    )
    val aktuellPost2 = WordpressPost(
        id = 224566,
        publishedYear = "2023",
        publishedFormatted = "2023-06-28T16:29:56+00:00",
        modifiedFormatted = "2023-06-28T16:35:44+00:00",
        imageUrl = "https://seesturm.ch/wp-content/uploads/2017/11/DSC_4041.sized_.jpg",
        title = "Erinnerung = Anmeldung KaTre noch bis am 1. Juli",
        titleDecoded = "Erinnerung = Anmeldung KaTre noch bis am 1. Juli",
        content = "\n<p>Über 1000 Pfadis aus dem ganzen Thurgau treffen sich jährlich zum Kantonalen Pfaditreffen (KaTre) \u2013 ein Höhepunkt im Kalender der Pfadi Thurgau. Dieses Jahr findet der Anlass erstmals seit 2019 wieder statt, und zwar am Wochenende <strong>vom 23. und 24. September</strong> unter dem Motto <strong>«Die Piraten vom Bodamicus»</strong>.</p>\n\n\n\n<p>Das KaTre 2023 findet ganz in der Nähe statt, nämlich in <strong>Romanshorn direkt am schönen Bodensee</strong>. Es wird von der Pfadi Seesturm, gemeinsam mit den Pfadi-Abteilungen aus Arbon und Romanshorn, organisiert. Die Biber- und Wolfsstufe werden das KaTre am Sonntag besuchen, während die Pfadi- und Piostufe das ganze Wochenende «Pfadi pur» erleben dürfen. Weitere Informationen zum KaTre 2023 findet ihr unter <a rel=\"noreferrer noopener\" href=\"http: //www.katre.ch\" target=\"_blank\">www.katre.ch</a> oder in unserem Mail vom 2. Juni.</p>\n\n\n\n<p>Leider haben wir bisher erst sehr <strong>wenige Anmeldungen</strong> erhalten. Es würde uns sehr freuen, wenn sich noch möglichst viele Seestürmlerinnen und Seestürmler aller Stufen anmelden. Füllt dazu einfach das <a href=\"https: //seesturm.ch/wp-content/uploads/2023/06/KaTre-23__Anmeldetalon.pdf\" target=\"_blank\" rel=\"noreferrer noopener\">Anmeldeformular</a> aus und sendet es <strong>bis am 01. Juli</strong> an <a href=\"mailto: al@seesturm.ch\" target=\"_blank\" rel=\"noreferrer noopener\">al@seesturm.ch</a>.</p>\n\n\n\n<p>Danke!</p>\n",
        contentPlain = "Über 1000 Pfadis aus dem ganzen Thurgau treffen sich jährlich zum Kantonalen Pfaditreffen (KaTre) \u2013 ein Höhepunkt im Kalender der Pfadi Thurgau. Dieses Jahr findet der Anlass erstmals seit 2019 wieder statt, und zwar am Wochenende vom 23. und 24. September unter dem Motto «Die Piraten vom Bodamicus».\n\n\n\nDas KaTre 2023 findet ganz in der Nähe statt, nämlich in Romanshorn direkt am schönen Bodensee. Es wird von der Pfadi Seesturm, gemeinsam mit den Pfadi-Abteilungen aus Arbon und Romanshorn, organisiert. Die Biber- und Wolfsstufe werden das KaTre am Sonntag besuchen, während die Pfadi- und Piostufe das ganze Wochenende «Pfadi pur» erleben dürfen. Weitere Informationen zum KaTre 2023 findet ihr unter www.katre.ch oder in unserem Mail vom 2. Juni.\n\n\n\nLeider haben wir bisher erst sehr wenige Anmeldungen erhalten. Es würde uns sehr freuen, wenn sich noch möglichst viele Seestürmlerinnen und Seestürmler aller Stufen anmelden. Füllt dazu einfach das Anmeldeformular aus und sendet es bis am 01. Juli an al@seesturm.ch.\n\n\n\nDanke!",
        imageAspectRatio = 5568.0/3712.0,
        author = "seesturm"
    )
    val aktuellPost3 = WordpressPost(
        id = 225166,
        publishedYear = "2023",
        publishedFormatted = "2023-06-28T16:29:56+00:00",
        modifiedFormatted = "2023-06-28T16:35:44+00:00",
        imageUrl = "",
        title = "Erinnerung = Anmeldung KaTre noch bis am 1. Juli",
        titleDecoded = "Erinnerung = Anmeldung KaTre noch bis am 1. Juli",
        content = "\n<p>Über 1000 Pfadis aus dem ganzen Thurgau treffen sich jährlich zum Kantonalen Pfaditreffen (KaTre) \u2013 ein Höhepunkt im Kalender der Pfadi Thurgau. Dieses Jahr findet der Anlass erstmals seit 2019 wieder statt, und zwar am Wochenende <strong>vom 23. und 24. September</strong> unter dem Motto <strong>«Die Piraten vom Bodamicus»</strong>.</p>\n\n\n\n<p>Das KaTre 2023 findet ganz in der Nähe statt, nämlich in <strong>Romanshorn direkt am schönen Bodensee</strong>. Es wird von der Pfadi Seesturm, gemeinsam mit den Pfadi-Abteilungen aus Arbon und Romanshorn, organisiert. Die Biber- und Wolfsstufe werden das KaTre am Sonntag besuchen, während die Pfadi- und Piostufe das ganze Wochenende «Pfadi pur» erleben dürfen. Weitere Informationen zum KaTre 2023 findet ihr unter <a rel=\"noreferrer noopener\" href=\"http: //www.katre.ch\" target=\"_blank\">www.katre.ch</a> oder in unserem Mail vom 2. Juni.</p>\n\n\n\n<p>Leider haben wir bisher erst sehr <strong>wenige Anmeldungen</strong> erhalten. Es würde uns sehr freuen, wenn sich noch möglichst viele Seestürmlerinnen und Seestürmler aller Stufen anmelden. Füllt dazu einfach das <a href=\"https: //seesturm.ch/wp-content/uploads/2023/06/KaTre-23__Anmeldetalon.pdf\" target=\"_blank\" rel=\"noreferrer noopener\">Anmeldeformular</a> aus und sendet es <strong>bis am 01. Juli</strong> an <a href=\"mailto: al@seesturm.ch\" target=\"_blank\" rel=\"noreferrer noopener\">al@seesturm.ch</a>.</p>\n\n\n\n<p>Danke!</p>\n",
        contentPlain = "Über 1000 Pfadis aus dem ganzen Thurgau treffen sich jährlich zum Kantonalen Pfaditreffen (KaTre) \u2013 ein Höhepunkt im Kalender der Pfadi Thurgau. Dieses Jahr findet der Anlass erstmals seit 2019 wieder statt, und zwar am Wochenende vom 23. und 24. September unter dem Motto «Die Piraten vom Bodamicus».\n\n\n\nDas KaTre 2023 findet ganz in der Nähe statt, nämlich in Romanshorn direkt am schönen Bodensee. Es wird von der Pfadi Seesturm, gemeinsam mit den Pfadi-Abteilungen aus Arbon und Romanshorn, organisiert. Die Biber- und Wolfsstufe werden das KaTre am Sonntag besuchen, während die Pfadi- und Piostufe das ganze Wochenende «Pfadi pur» erleben dürfen. Weitere Informationen zum KaTre 2023 findet ihr unter www.katre.ch oder in unserem Mail vom 2. Juni.\n\n\n\nLeider haben wir bisher erst sehr wenige Anmeldungen erhalten. Es würde uns sehr freuen, wenn sich noch möglichst viele Seestürmlerinnen und Seestürmler aller Stufen anmelden. Füllt dazu einfach das Anmeldeformular aus und sendet es bis am 01. Juli an al@seesturm.ch.\n\n\n\nDanke!",
        imageAspectRatio = 5568.0/3712.0,
        author = "seesturm"
    )

    val weather = Gson().fromJson(
        """
            {
              "attributionURL": "https://developer.apple.com/weatherkit/data-source-attribution/",
              "readTime": "2025-01-26T12:15:11Z",
              "daily": {
                "forecastStart": "2025-02-01T07:00:00Z",
                "forecastEnd": "2025-02-01T19:00:00Z",
                "conditionCode": "Clear",
                "temperatureMax": 4.41,
                "temperatureMin": 0.93,
                "precipitationAmount": 0,
                "precipitationChance": 0,
                "snowfallAmount": 0,
                "cloudCover": 0.81,
                "humidity": 0.81,
                "windDirection": 298,
                "windSpeed": 7.16,
                "sunrise": "2025-02-01T06:48:49Z",
                "sunset": "2025-02-01T16:24:22Z"
              },
              "hourly": [
                {
                  "forecastStart": "2025-02-01T05:00:00Z",
                  "cloudCover": 0.81,
                  "precipitationType": "snow",
                  "precipitationAmount": 0.09,
                  "snowfallAmount": 0.84,
                  "temperature": 0.53,
                  "windSpeed": 6.13,
                  "windGust": 10.9
                },
                {
                  "forecastStart": "2025-02-01T06:00:00Z",
                  "cloudCover": 0.79,
                  "precipitationType": "snow",
                  "precipitationAmount": 0.1,
                  "snowfallAmount": 0.98,
                  "temperature": 0.62,
                  "windSpeed": 6.43,
                  "windGust": 11.13
                },
                {
                  "forecastStart": "2025-02-01T07:00:00Z",
                  "cloudCover": 0.77,
                  "precipitationType": "snow",
                  "precipitationAmount": 0.1,
                  "snowfallAmount": 0.97,
                  "temperature": 0.93,
                  "windSpeed": 6.54,
                  "windGust": 11.26
                },
                {
                  "forecastStart": "2025-02-01T08:00:00Z",
                  "cloudCover": 0.76,
                  "precipitationType": "snow",
                  "precipitationAmount": 0.1,
                  "snowfallAmount": 0.93,
                  "temperature": 1.45,
                  "windSpeed": 6.54,
                  "windGust": 11.41
                },
                {
                  "forecastStart": "2025-02-01T09:00:00Z",
                  "cloudCover": 0.76,
                  "precipitationType": "snow",
                  "precipitationAmount": 0.09,
                  "snowfallAmount": 0.8,
                  "temperature": 2.1,
                  "windSpeed": 6.62,
                  "windGust": 11.84
                },
                {
                  "forecastStart": "2025-02-01T10:00:00Z",
                  "cloudCover": 0.75,
                  "precipitationType": "snow",
                  "precipitationAmount": 0.06,
                  "snowfallAmount": 0.49,
                  "temperature": 2.87,
                  "windSpeed": 6.91,
                  "windGust": 12.75
                },
                {
                  "forecastStart": "2025-02-01T11:00:00Z",
                  "cloudCover": 0.76,
                  "precipitationType": "snow",
                  "precipitationAmount": 0.02,
                  "snowfallAmount": 0.17,
                  "temperature": 3.59,
                  "windSpeed": 7.26,
                  "windGust": 13.81
                },
                {
                  "forecastStart": "2025-02-01T12:00:00Z",
                  "cloudCover": 0.76,
                  "precipitationType": "clear",
                  "precipitationAmount": 0,
                  "snowfallAmount": 0,
                  "temperature": 4.09,
                  "windSpeed": 7.52,
                  "windGust": 14.5
                },
                {
                  "forecastStart": "2025-02-01T13:00:00Z",
                  "cloudCover": 0.78,
                  "precipitationType": "clear",
                  "precipitationAmount": 0,
                  "snowfallAmount": 0,
                  "temperature": 4.34,
                  "windSpeed": 7.68,
                  "windGust": 14.62
                },
                {
                  "forecastStart": "2025-02-01T14:00:00Z",
                  "cloudCover": 0.8,
                  "precipitationType": "clear",
                  "precipitationAmount": 0,
                  "snowfallAmount": 0,
                  "temperature": 4.41,
                  "windSpeed": 7.79,
                  "windGust": 14.45
                },
                {
                  "forecastStart": "2025-02-01T15:00:00Z",
                  "cloudCover": 0.83,
                  "precipitationType": "clear",
                  "precipitationAmount": 0,
                  "snowfallAmount": 0,
                  "temperature": 4.26,
                  "windSpeed": 7.8,
                  "windGust": 14.16
                },
                {
                  "forecastStart": "2025-02-01T16:00:00Z",
                  "cloudCover": 0.87,
                  "precipitationType": "clear",
                  "precipitationAmount": 0,
                  "snowfallAmount": 0,
                  "temperature": 3.97,
                  "windSpeed": 7.56,
                  "windGust": 13.72
                },
                {
                  "forecastStart": "2025-02-01T17:00:00Z",
                  "cloudCover": 0.9,
                  "precipitationType": "clear",
                  "precipitationAmount": 0,
                  "snowfallAmount": 0,
                  "temperature": 3.6,
                  "windSpeed": 7.11,
                  "windGust": 13.15
                },
                {
                  "forecastStart": "2025-02-01T18:00:00Z",
                  "cloudCover": 0.93,
                  "precipitationType": "clear",
                  "precipitationAmount": 0,
                  "snowfallAmount": 0,
                  "temperature": 3.29,
                  "windSpeed": 6.66,
                  "windGust": 12.75
                }
              ]
            }
        """.trimIndent(),
        WeatherDto::class.java
    ).toWeather()

    val document1 = WordpressDocument(
        id = "123",
        thumbnailUrl = "https://seesturm.ch/wp-content/uploads/2022/04/190404_Infobroschuere-Pfadi-Thurgau-pdf-212x300.jpg",
        thumbnailWidth = 212,
        thumbnailHeight = 300,
        title = "Infobroschüre Pfadi Thurgau",
        documentUrl = "https://seesturm.ch/wp-content/uploads/2022/04/190404_Infobroschuere-Pfadi-Thurgau.pdf",
        published = oldDate,
        publishedFormatted = oldDateFormatted
    )

    val gespeichertePerson1 = GespeichertePerson(
        id = UUID.randomUUID().toString(),
        vorname = "Hans",
        nachname = "Meier",
        pfadiname = "Seppli"
    )
    val gespeichertePerson2 = GespeichertePerson(
        id = UUID.randomUUID().toString(),
        vorname = "Maria",
        nachname = "Müller",
        pfadiname = null
    )
    val gespeichertePerson3 = GespeichertePerson(
        id = UUID.randomUUID().toString(),
        vorname = "Peter",
        nachname = "Mustermann",
        pfadiname = null
    )

    val leitungsteamMember = LeitungsteamMember(
        name = "Test name / Pfadiname Pfadiname",
        job = "Stufenleitung Pfadistufe",
        contact = "xxx@yyy.ch",
        photo = "https://seesturm.ch/wp-content/uploads/2017/10/Wicky2021-scaled.jpg"
    )
}