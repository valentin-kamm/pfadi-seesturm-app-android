package ch.seesturm.pfadiseesturm.presentation.common.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_RED

@Composable
fun MainSectionHeader(
    sectionTitle: String,
    icon: ImageVector,
    type: MainSectionHeaderType,
    modifier: Modifier = Modifier
) {
    val circleSize = 42.dp
    val iconSize = 0.7 * circleSize

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(circleSize)
                .background(color = Color.SEESTURM_RED, shape = CircleShape)
                .wrapContentWidth()
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .size(iconSize)
            )
        }

        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .weight(1f)
        )
        when (type) {
            MainSectionHeaderType.Blank -> { }
            is MainSectionHeaderType.Button -> {
                SeesturmButton(
                    type = SeesturmButtonType.Tertiary(
                        icon = SeesturmButtonIconType.Predefined(
                            icon = type.buttonIcon
                        )
                    ),
                    onClick = {
                        type.buttonAction?.invoke()
                    },
                    title = type.buttonTitle,
                    modifier = Modifier
                        .wrapContentWidth()
                )
            }
            is MainSectionHeaderType.Custom -> {
                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                ) {
                    type.content()
                }
            }
        }
    }
}

// type of section headers
sealed class MainSectionHeaderType {
    data object Blank: MainSectionHeaderType()
    data class Button(
        val buttonTitle: String? = null,
        val buttonIcon: ImageVector,
        val buttonAction: (() -> Unit)? = null
    ): MainSectionHeaderType()
    data class Custom(
        val content: @Composable () -> Unit
    ): MainSectionHeaderType()
}

@Preview
@Composable
private fun MainSectionHeaderPreview1() {
    MainSectionHeader(
        "Aktuell",
        icon = Icons.Default.Newspaper,
        type = MainSectionHeaderType.Button("Mehr", Icons.AutoMirrored.Default.ArrowForwardIos)
    )
}
@Preview
@Composable
private fun MainSectionHeaderPreview2() {
    MainSectionHeader(
        "Nächste Aktivität",
        icon = Icons.Default.Newspaper,
        type = MainSectionHeaderType.Custom(
            content = {
                DropdownButton(
                    title = "Stufe",
                    dropdown = { isShown, dismiss -> }
                )
            }
        )
    )
}