package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonType
import ch.seesturm.pfadiseesturm.util.SeesturmStufe

@Composable
fun LeiterbereichStufeCardView(
    cardWidth: Dp,
    stufe: SeesturmStufe,
    onButtonClick: () -> Unit,
    onNavigate: () -> Unit,
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
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                SeesturmButton(
                    type = SeesturmButtonType.Tertiary(
                        contentColor = stufe.highContrastColor(),
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Default.Add
                        )
                    ),
                    title = "Neue Aktivität",
                    onClick = {
                        onButtonClick()
                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
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

@Preview
@Composable
private fun LeiterbereichStufeCardViewPreview() {
    LeiterbereichStufeCardView(
        cardWidth = 120.dp,
        stufe = SeesturmStufe.Pfadi,
        onButtonClick = {},
        onNavigate = {}
    )
}