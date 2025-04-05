package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressDocument
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import com.google.gson.annotations.SerializedName
import java.time.ZoneId

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
    return WordpressDocument(
        id = id,
        thumbnailUrl = thumbnailUrl,
        thumbnailWidth = thumbnailWidth,
        thumbnailHeight = thumbnailHeight,
        title = title,
        url = url,
        published = DateTimeUtil.shared.formatDate(
            date = DateTimeUtil.shared.parseIsoDateWithOffset(published).atZone(ZoneId.systemDefault()),
            format = "EEEE, d. MMMM yyyy",
            withRelativeDateFormatting = true
        )
    )
}