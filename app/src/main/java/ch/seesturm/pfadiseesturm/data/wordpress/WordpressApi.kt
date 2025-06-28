package ch.seesturm.pfadiseesturm.data.wordpress

import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventsDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.LeitungsteamDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.MinimumRequiredAppBuild
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WeatherDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressDocumentDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressPhotoDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressPhotoGalleryDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressPostDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressPostsDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

interface WordpressBaseApi {

    @GET("aktuell/posts")
    suspend fun getPosts(
        @Query("start") start: Int,
        @Query("length") length: Int
    ): WordpressPostsDto
    @GET("aktuell/postById/{postId}")
    suspend fun getPost(
        @Path("postId") postId: Int
    ): WordpressPostDto

    @GET("events/byCalendarId")
    suspend fun getEventsByCalendarId(
        @Query("calendarId") calendarId: String,
        @Query("timeMin") timeMin: String? = null,
        @Query("maxResults") maxResults: Int? = null
    ): GoogleCalendarEventsDto
    @GET("events/byPageId")
    suspend fun getEventsByPageId(
        @Query("calendarId") calendarId: String,
        @Query("pageToken") pageToken: String,
        @Query("maxResults") maxResults: Int
    ): GoogleCalendarEventsDto
    @GET("events/byEventId")
    suspend fun getEvent(
        @Query("calendarId") calendarId: String,
        @Query("eventId") eventId: String
    ): GoogleCalendarEventDto

    @GET("weather")
    suspend fun getWeather(): WeatherDto

    @GET("photos/pfadijahre")
    suspend fun getPhotosPfadijahre(): List<WordpressPhotoGalleryDto>
    @GET("photos/albums/{pfadijahrId}")
    suspend fun getPhotosAlbums(
        @Path("pfadijahrId") id: String
    ): List<WordpressPhotoGalleryDto>
    @GET("photos/images/{albumId}")
    suspend fun getPhotos(
        @Path("albumId") id: String
    ): List<WordpressPhotoDto>

    @GET("documents/downloads")
    suspend fun getDocuments(): List<WordpressDocumentDto>
    @GET("documents/luuchtturm")
    suspend fun getLuuchtturm(): List<WordpressDocumentDto>

    @GET("leitungsteam/members")
    suspend fun getLeitungsteam(): List<LeitungsteamDto>

    @GET("minimumAppBuild")
    suspend fun getMinimumRequiredAppBuild(): MinimumRequiredAppBuild
}

interface WordpressApi {

    suspend fun getPosts(start: Int, length: Int): WordpressPostsDto
    suspend fun getPost(postId: Int): WordpressPostDto

    suspend fun getEvents(calendarId: String, includePast: Boolean, maxResults: Int): GoogleCalendarEventsDto
    suspend fun getEvents(calendarId: String, pageToken: String, maxResults: Int): GoogleCalendarEventsDto
    suspend fun getEvents(calendarId: String, timeMin: Instant): GoogleCalendarEventsDto
    suspend fun getEvent(calendarId: String, eventId: String): GoogleCalendarEventDto

    suspend fun getWeather(): WeatherDto

    suspend fun getPhotosPfadijahre(): List<WordpressPhotoGalleryDto>
    suspend fun getPhotosAlbums(id: String): List<WordpressPhotoGalleryDto>
    suspend fun getPhotos(id: String): List<WordpressPhotoDto>

    suspend fun getDocuments(): List<WordpressDocumentDto>
    suspend fun getLuuchtturm(): List<WordpressDocumentDto>

    suspend fun getLeitungsteam(): List<LeitungsteamDto>

    suspend fun getMinimumRequiredAppBuild(): MinimumRequiredAppBuild
}

class WordpressApiImpl(
    private val api: WordpressBaseApi
): WordpressApi {

    override suspend fun getPosts(start: Int, length: Int): WordpressPostsDto =
        api.getPosts(start, length)
    override suspend fun getPost(postId: Int): WordpressPostDto =
        api.getPost(postId)

    override suspend fun getEvents(
        calendarId: String,
        includePast: Boolean,
        maxResults: Int
    ): GoogleCalendarEventsDto {
        return if (!includePast) {
            api.getEventsByCalendarId(calendarId = calendarId, timeMin = getDateString(), maxResults = maxResults)
        }
        else {
            api.getEventsByCalendarId(calendarId = calendarId, maxResults = maxResults)
        }
    }
    override suspend fun getEvents(
        calendarId: String,
        pageToken: String,
        maxResults: Int
    ): GoogleCalendarEventsDto =
        api.getEventsByPageId(calendarId = calendarId, pageToken = pageToken, maxResults = maxResults)
    override suspend fun getEvents(calendarId: String, timeMin: Instant): GoogleCalendarEventsDto =
        api.getEventsByCalendarId(calendarId = calendarId, timeMin = getDateString(timeMin))
    override suspend fun getEvent(calendarId: String, eventId: String): GoogleCalendarEventDto =
        api.getEvent(calendarId, eventId)

    override suspend fun getWeather(): WeatherDto =
        api.getWeather()

    override suspend fun getPhotosPfadijahre(): List<WordpressPhotoGalleryDto> =
        api.getPhotosPfadijahre()
    override suspend fun getPhotosAlbums(id: String): List<WordpressPhotoGalleryDto> =
        api.getPhotosAlbums(id)
    override suspend fun getPhotos(id: String): List<WordpressPhotoDto> =
        api.getPhotos(id)

    override suspend fun getDocuments(): List<WordpressDocumentDto> =
        api.getDocuments()
    override suspend fun getLuuchtturm(): List<WordpressDocumentDto> =
        api.getLuuchtturm()

    override suspend fun getLeitungsteam(): List<LeitungsteamDto> =
        api.getLeitungsteam()

    override suspend fun getMinimumRequiredAppBuild(): MinimumRequiredAppBuild =
        api.getMinimumRequiredAppBuild()

    private fun getDateString(instant: Instant = Instant.now()): String {
        return DateTimeFormatter.ISO_INSTANT
            .withZone(ZoneOffset.UTC)
            .format(instant)
    }
}