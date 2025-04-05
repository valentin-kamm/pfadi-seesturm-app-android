package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPost
import com.google.gson.annotations.SerializedName
import java.time.ZoneId

data class WordpressPostDto(
    val author: String,
    val content: String,
    @SerializedName("content_plain") val contentPlain: String,
    val id: Int,
    @SerializedName("image_height") val imageHeight: Int,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("image_width") val imageWidth: Int,
    val modified: String,
    val published: String,
    val title: String,
    @SerializedName("title_decoded") val titleDecoded: String
)

fun WordpressPostDto.toWordpressPost(): WordpressPost {
    return WordpressPost(
        id = id,
        publishedYear = DateTimeUtil.shared.formatDate(
            date = DateTimeUtil.shared.parseIsoDateWithOffset(published).atZone(ZoneId.systemDefault()),
            format = "yyyy",
            withRelativeDateFormatting = false
        ),
        published = DateTimeUtil.shared.formatDate(
            date = DateTimeUtil.shared.parseIsoDateWithOffset(published).atZone(ZoneId.systemDefault()),
            format = "EEEE, d. MMMM yyyy",
            withRelativeDateFormatting = true
        ),
        modified = DateTimeUtil.shared.formatDate(
            date = DateTimeUtil.shared.parseIsoDateWithOffset(modified).atZone(ZoneId.systemDefault()),
            format = "EEEE, d. MMMM yyyy",
            withRelativeDateFormatting = true
        ),
        imageUrl = imageUrl,
        title = title,
        titleDecoded = titleDecoded,
        content = content,
        contentPlain = contentPlain,
        aspectRatio = if (imageWidth.toDouble() < imageHeight.toDouble()) 1.0 else imageWidth.toDouble() / imageHeight.toDouble(),
        author = author
    )
}