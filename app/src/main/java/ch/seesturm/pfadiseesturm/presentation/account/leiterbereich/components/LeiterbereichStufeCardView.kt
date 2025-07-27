package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe

@Composable
fun LeiterbereichStufeCardView(
    cardWidth: Dp,
    stufe: SeesturmStufe,
    onButtonClick: () -> Unit,
    onNavigate: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {

    CustomCardView(
        onClick = {
            onNavigate()
        },
        modifier = modifier
            .widthIn(min = cardWidth)
    ) {
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier
                .padding(16.dp)
                .widthIn(min = cardWidth - 32.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .widthIn(min = cardWidth - 32.dp)
            ) {
                Image(
                    painter = painterResource(stufe.iconReference),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                )
                Text(
                    text = stufe.stufenName,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, hyphens = Hyphens.Auto),
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                SeesturmButton(
                    type = SeesturmButtonType.Secondary(
                        buttonColor = stufe.highContrastColor(isDarkTheme),
                        contentColor = stufe.onHighContrastColor(),
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Default.Add
                        )
                    ),
                    title = "Neue Aktivität",
                    onClick = {
                        onButtonClick()
                    },
                    modifier = Modifier
                        .padding(top = 8.dp),
                    isLoading = false,
                    allowHorizontalTextShrink = false
                )
            }
            Icon(
                Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .wrapContentWidth()
                    .alpha(0.4f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LeiterbereichStufeCardViewPreview1() {
    PfadiSeesturmTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SeesturmStufe.entries.forEach { stufe ->
                LeiterbereichStufeCardView(
                    cardWidth = 120.dp,
                    stufe = stufe,
                    onButtonClick = {},
                    onNavigate = {},
                    isDarkTheme = false
                )
            }
        }
    }
}
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun LeiterbereichStufeCardViewPreview2() {
    PfadiSeesturmTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SeesturmStufe.entries.forEach { stufe ->
                LeiterbereichStufeCardView(
                    cardWidth = 120.dp,
                    stufe = stufe,
                    onButtonClick = {},
                    onNavigate = {},
                    isDarkTheme = false
                )
            }
        }
    }
}