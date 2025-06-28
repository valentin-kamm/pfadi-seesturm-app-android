package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPost
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import com.google.gson.annotations.SerializedName
import java.time.ZoneId

data class WordpressPostDto(
    val id: Int,
    val title: String,
    @SerializedName("title_decoded") val titleDecoded: String,
    val content: String,
    @SerializedName("content_plain") val contentPlain: String,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("image_height") val imageHeight: Int,
    @SerializedName("image_width") val imageWidth: Int,
    val author: String,
    val modified: String,
    val published: String
)

fun WordpressPostDto.toWordpressPost(): WordpressPost {

    val publishedDate = DateTimeUtil.shared.parseIsoDateWithOffset(published).atZone(ZoneId.systemDefault())
    val modifiedDate = DateTimeUtil.shared.parseIsoDateWithOffset(modified).atZone(ZoneId.systemDefault())

    return WordpressPost(
        id = id,
        publishedYear = DateTimeUtil.shared.formatDate(
            date = publishedDate,
            format = "yyyy",
            type = DateFormattingType.Absolute
        ),
        publishedFormatted = DateTimeUtil.shared.formatDate(
            date = publishedDate,
            format = "EEEE, d. MMMM yyyy",
            type = DateFormattingType.Relative(true)
        ),
        modifiedFormatted = DateTimeUtil.shared.formatDate(
            date = modifiedDate,
            format = "EEEE, d. MMMM yyyy",
            type = DateFormattingType.Relative(true)
        ),
        imageUrl = imageUrl,
        title = title,
        titleDecoded = titleDecoded,
        content = content,
        contentPlain = contentPlain,
        imageAspectRatio = if (imageWidth.toDouble() < imageHeight.toDouble()) 1.0 else imageWidth.toDouble() / imageHeight.toDouble(),
        author = author
    )
}