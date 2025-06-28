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
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN

@Composable
fun DropdownButton(
    title: String?,
    dropdown: @Composable (isShown: Boolean, dismiss: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    buttonColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = Color.SEESTURM_GREEN,
    icon: SeesturmButtonIconType = SeesturmButtonIconType.Predefined(
        icon = Icons.Default.ArrowDropDown
    ),
    isLoading: Boolean = false,
    enabled: Boolean = !isLoading
) {

    var showMenu by rememberSaveable { mutableStateOf(false) }

    Box {
        SeesturmButton(
            type = SeesturmButtonType.Secondary(
                buttonColor = buttonColor,
                contentColor = contentColor,
                icon = icon
            ),
            title = title,
            isLoading = isLoading,
            onClick = {
                showMenu = !showMenu
            },
            enabled = enabled,
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
                    icon = SeesturmButtonIconType.Custom(
                        image = painterResource(R.drawable.midata_logo)
                    )
                )
                // custom icon disabled
                DropdownButton(
                    title = "Dropdown",
                    dropdown = { _, _ -> },
                    icon = SeesturmButtonIconType.Custom(
                        image = painterResource(R.drawable.midata_logo)
                    ),
                    enabled = false
                )
            }
        }
    }
}