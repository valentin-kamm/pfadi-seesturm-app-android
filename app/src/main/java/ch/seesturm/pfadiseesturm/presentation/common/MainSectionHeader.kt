package ch.seesturm.pfadiseesturm.presentation.common


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
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import ch.seesturm.pfadiseesturm.presentation.common.buttons.DropdownButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.types.stufenDropdownText

@Composable
fun MainSectionHeader(
    sectionTitle: String,
    icon: ImageVector,
    type: MainSectionHeaderType,
    modifier: Modifier = Modifier,
    circleSize: Dp = 42.dp,
    iconSize: Dp = 0.7 * circleSize
) {

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
                imageVector = icon,
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
        if (type is MainSectionHeaderType.Button) {
            SeesturmButton(
                type = SeesturmButtonType.Secondary(
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
        else if (type is MainSectionHeaderType.StufenButton) {
            DropdownButton(
                title = type.selectedStufen.stufenDropdownText,
                enabled = type.enabled,
                dropdown = { isShown, dismiss ->
                    ThemedDropdownMenu(
                        expanded = isShown,
                        onDismissRequest = {
                            dismiss()
                        }
                    ) {
                        SeesturmStufe.entries.sortedBy { it.id }.forEach { stufe ->
                            ThemedDropdownMenuItem(
                                text = { Text(stufe.stufenName) },
                                onClick = {
                                    type.onToggle(stufe)
                                    dismiss()
                                },
                                trailingIcon = {
                                    if (type.selectedStufen.contains(stufe)) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainSectionHeaderPreview1() {
    PfadiSeesturmTheme {
        MainSectionHeader(
            "Aktuell",
            icon = Icons.Default.Newspaper,
            type = MainSectionHeaderType.Blank
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun MainSectionHeaderPreview2() {
    PfadiSeesturmTheme {
        MainSectionHeader(
            "Aktuell",
            icon = Icons.Default.Newspaper,
            type = MainSectionHeaderType.Button(
                buttonTitle = "Mehr",
                buttonIcon = Icons.AutoMirrored.Default.ArrowForwardIos
            )
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun MainSectionHeaderPreview3() {
    PfadiSeesturmTheme {
        MainSectionHeader(
            "Aktuell",
            icon = Icons.Default.Newspaper,
            type = MainSectionHeaderType.StufenButton(
                selectedStufen = listOf(SeesturmStufe.Biber, SeesturmStufe.Wolf),
                enabled = true,
                onToggle = {}
            )
        )
    }
}