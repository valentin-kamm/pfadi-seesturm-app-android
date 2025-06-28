package ch.seesturm.pfadiseesturm.presentation.common.rich_text

import android.text.Html
import androidx.compose.ui.text.AnnotatedString
import com.mohamedrejeb.richeditor.model.RichTextState
import org.jsoup.parser.Parser

data class SeesturmRichTextState(
    val state: RichTextState,
    val onValueChanged: () -> Unit,
    val annotatedString: AnnotatedString = state.annotatedString // this is required in order to emit a flow even if the data has not changed
)

val RichTextState.isHTMLEmpty
    get() = Html.fromHtml(Parser.unescapeEntities(toHtml(), false), Html.FROM_HTML_MODE_LEGACY).toString().trim().isEmpty()

fun RichTextState.getUnescapedHtml(): String {
    return if (isHTMLEmpty) {
        ""
    } else {
        Parser.unescapeEntities(toHtml().trim(), false)
    }
}