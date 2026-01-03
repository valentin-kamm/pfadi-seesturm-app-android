package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressDocument
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.time.ZoneId

@Serializable
data class WordpressDocumentDto(
    val id: String,
    @SerializedName("thumbnail_url") val thumbnailUrl: String,
    @SerializedName("thumbnail_width") val thumbnailWidth: Int,
    @SerializedName("thumbnail_height") val thumbnailHeight: Int,
    val title: String,
    val url: String,
    val published: String
)

fun WordpressDocumentDto.toWordpressDocument(): WordpressDocument {

    val publishedDate = DateTimeUtil.shared.parseIsoDateWithOffset(published).atZone(ZoneId.systemDefault())

    return WordpressDocument(
        id = id,
        thumbnailUrl = thumbnailUrl,
        thumbnailWidth = thumbnailWidth,
        thumbnailHeight = thumbnailHeight,
        title = title,
        documentUrl = url,
        published = publishedDate,
        publishedFormatted = DateTimeUtil.shared.formatDate(
            date = publishedDate,
            format = "EEEE, d. MMMM yyyy",
            type = DateFormattingType.Relative(true)
        )
    )
}