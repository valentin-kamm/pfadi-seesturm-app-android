package ch.seesturm.pfadiseesturm.presentation.common.components

import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_BLUE
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.launchWebsite

@Composable
fun HtmlText(
    html: String,
    fontStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    textColor: Color,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    AndroidView(
        factory = { content ->
            TextView(content).apply {
                text = fromHtml(html)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, fontStyle.fontSize.value)
                setTextColor(textColor.toArgb())
                setLinkTextColor(Color.SEESTURM_BLUE.toArgb())
                // Enable link clicking with custom implementation
                movementMethod = CustomLinkMovementMethod { url ->
                    launchWebsite(url, context)
                }
            }
        },
        update = { textView ->
            textView.text = fromHtml(html)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontStyle.fontSize.value)
            textView.setTextColor(textColor.toArgb())
        },
        modifier = modifier
    )
}
private fun fromHtml(html: String): Spanned {
    return HtmlCompat.fromHtml(
        html,
        HtmlCompat.FROM_HTML_MODE_LEGACY
    )
}
class CustomLinkMovementMethod(
    private val onLinkClicked: (String) -> Unit
) : LinkMovementMethod() {
    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {

        if (event.action != MotionEvent.ACTION_UP) {
            return super.onTouchEvent(widget, buffer, event)
        }

        val x = event.x.toInt()
        val y = event.y.toInt()
        val layout = widget.layout
        val line = layout.getLineForVertical(y)
        val off = layout.getOffsetForHorizontal(line, x.toFloat())
        val links = buffer.getSpans(off, off, URLSpan::class.java)
        if (links.isNotEmpty()) {
            val link = links[0]
            onLinkClicked(link.url)
            return true
        }

        return super.onTouchEvent(widget, buffer, event)

    }
}

@Preview(showBackground = true)
@Composable
fun HtmlTextPreview() {
    HtmlText(
        html = """
            <p><a href="https: //forms.gle/USNBrDWz24esqaHi9">Umfrage</a> Morgen am <b>31. Juli</b> <i>findet</i> die 1. Augustfeier der <strong>Gemeinde</strong> statt, welche dieses Jahr von uns organisiert wird. Das Wetter sieht wunderbar aus und die Vorbereitungen laufen. Die <b>Feier</b> findet vor der Aula in Neukirch statt und das Programm ist folgendes: </p><ul class="wp-block-list"><li>ab 17.00 Uhr: Kinderplausch, Festwirtschaft und Barbetrieb</li><li>ab 20.00 Uhr: Musikgesellschaft Neukirch Egnach, Festansprache von Michael Loepfe / Stromboli, Fackelumzug und Livemusik von Soulkey</li></ul><p>Wir freuen uns riesig möglichst viele bekannte Gesichter begrüssen zu dürfen. </p>
        """.trimIndent(),
        textColor = MaterialTheme.colorScheme.onBackground
    )
}