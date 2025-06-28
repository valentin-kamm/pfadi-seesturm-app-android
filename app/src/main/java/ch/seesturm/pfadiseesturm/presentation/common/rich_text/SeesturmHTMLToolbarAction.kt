package ch.seesturm.pfadiseesturm.presentation.common.rich_text

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.FormatStrikethrough
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material.icons.outlined.Link
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import com.mohamedrejeb.richeditor.model.RichTextState

enum class SeesturmHTMLToolbarAction(
    val icon: ImageVector
) {

    Bold(icon = Icons.Outlined.FormatBold),
    Italic(icon = Icons.Outlined.FormatItalic),
    Underline(icon = Icons.Outlined.FormatUnderlined),
    Strikethrough(icon = Icons.Outlined.FormatStrikethrough),
    Link(icon = Icons.Outlined.Link),
    OrderedList(icon = Icons.Outlined.FormatListNumbered),
    UnorderedList(icon = Icons.AutoMirrored.Outlined.FormatListBulleted);

    companion object {
        val actionGroups: List<List<SeesturmHTMLToolbarAction>> = listOf(
            listOf(Bold, Italic, Underline, Strikethrough),
            listOf(Link),
            listOf(OrderedList, UnorderedList)
        )
    }

    val buttonTint: Color
        get() = Color.SEESTURM_GREEN

    fun isSelected(state: RichTextState): Boolean {
        return when (this) {
            Bold -> state.currentSpanStyle.fontWeight == FontWeight.Bold
            Italic -> state.currentSpanStyle.fontStyle == FontStyle.Italic
            Underline -> state.currentSpanStyle.textDecoration == TextDecoration.Underline
            Strikethrough -> state.currentSpanStyle.textDecoration == TextDecoration.LineThrough
            Link -> state.isLink
            OrderedList -> state.isOrderedList
            UnorderedList -> state.isUnorderedList
        }
    }

    fun performButtonAction(state: RichTextState) {
        when (this) {
            Bold -> {
                state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
            }
            Italic -> {
                state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
            }
            Underline -> {
                state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
            }
            Strikethrough -> {
                state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
            }
            Link -> {
                // do nothing (handled differently)
            }
            OrderedList -> {
                state.toggleOrderedList()
            }
            UnorderedList -> {
                state.toggleUnorderedList()
            }
        }
    }
}
