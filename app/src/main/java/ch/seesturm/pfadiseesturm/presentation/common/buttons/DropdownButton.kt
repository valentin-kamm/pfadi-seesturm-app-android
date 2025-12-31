package ch.seesturm.pfadiseesturm.presentation.common.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN

@Composable
fun DropdownButton(
    type: SeesturmButtonType = SeesturmButtonType.Secondary,
    title: String?,
    icon: SeesturmButtonIconType = SeesturmButtonIconType.Predefined(
        icon = Icons.Default.ArrowDropDown
    ),
    colors: SeesturmButtonColor = SeesturmButtonColor.Custom(
        buttonColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = Color.SEESTURM_GREEN
    ),
    isLoading: Boolean = false,
    enabled: Boolean = !isLoading,
    disabledAlpha: Float = 0.6f,
    iconSize: Dp = 18.dp,
    modifier: Modifier = Modifier,
    dropdown: @Composable (isShown: Boolean, dismiss: () -> Unit) -> Unit
) {

    var showMenu by rememberSaveable { mutableStateOf(false) }

    Box {
        SeesturmButton(
            type = type,
            onClick = {
                showMenu = !showMenu
            },
            title = title,
            icon = icon,
            colors = colors,
            isLoading = isLoading,
            enabled = enabled,
            disabledAlpha = disabledAlpha,
            iconSize = iconSize,
            modifier = modifier
        )
        dropdown(showMenu) {
            showMenu = false
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DropdownButtonPreview() {
    PfadiSeesturmTheme {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterHorizontally),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // simple
                DropdownButton(
                    title = "Dropdown",
                    dropdown = { _, _ -> }
                )
                // simple loading
                DropdownButton(
                    title = "Dropdown",
                    dropdown = { _, _ -> },
                    isLoading = true
                )
                // simple disabled
                DropdownButton(
                    title = "Dropdown",
                    dropdown = { _, _ -> },
                    enabled = false
                )
                // custom icon
                DropdownButton(
                    title = "Dropdown",
                    dropdown = { _, _ -> },
                    type = SeesturmButtonType.Secondary,
                    icon = SeesturmButtonIconType.Custom(
                        image = painterResource(R.drawable.midata_logo)
                    )
                )
                // custom icon disabled
                DropdownButton(
                    title = "Dropdown",
                    dropdown = { _, _ -> },
                    type = SeesturmButtonType.Secondary,
                    icon = SeesturmButtonIconType.Custom(
                        image = painterResource(R.drawable.midata_logo)
                    ),
                    enabled = false
                )
            }
        }
    }
}