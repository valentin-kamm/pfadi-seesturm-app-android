package ch.seesturm.pfadiseesturm.presentation.common.rich_text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_BLUE
import ch.seesturm.pfadiseesturm.util.launchWebsite

@Composable
fun HtmlTextView(
    html: String,
    modifier: Modifier = Modifier,
    openLinks: Boolean = true,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge
) {

    val context = LocalContext.current

    Text(
        text = AnnotatedString.fromHtml(
            htmlString = html,
            linkStyles = TextLinkStyles(
                style = SpanStyle(
                    color = Color.SEESTURM_BLUE,
                    textDecoration = TextDecoration.Underline
                )
            ),
            linkInteractionListener = { linkAnnotation ->
                if (linkAnnotation is LinkAnnotation.Url && openLinks) {
                    launchWebsite(linkAnnotation.url, context)
                }
            }
        ),
        style = textStyle,
        color = textColor,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun HtmlTextViewPreview() {
    HtmlTextView(
        html = """
            <p><a href="https: //forms.gle/USNBrDWz24esqaHi9">Umfrage</a> Morgen am <b>31. Juli</b> <i>findet</i> die 1. Augustfeier der <strong>Gemeinde</strong> statt, welche dieses Jahr von uns organisiert wird. Das Wetter sieht wunderbar aus und die Vorbereitungen laufen. Die <b>Feier</b> findet vor der Aula in Neukirch statt und das Programm ist folgendes: </p><ul class="wp-block-list"><li>ab 17.00 Uhr: Kinderplausch, Festwirtschaft und Barbetrieb</li><li>ab 20.00 Uhr: Musikgesellschaft Neukirch Egnach, Festansprache von Michael Loepfe / Stromboli, Fackelumzug und Livemusik von Soulkey</li></ul><p>Wir freuen uns riesig möglichst viele bekannte Gesichter begrüssen zu dürfen. </p>
        """.trimIndent()
    )
}
