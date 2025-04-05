package ch.seesturm.pfadiseesturm.presentation.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.outlined.House
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.presentation.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_BLUE
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_RED

@Composable
fun SeesturmButton(
    modifier: Modifier = Modifier,
    type: SeesturmButtonType,
    title: String?,
    onClick: (() -> Unit)? = null,
    isLoading: Boolean = false,
    enabled: Boolean = !isLoading
) {

    val disabledAlpha = 0.6f

    when (type) {
        is SeesturmButtonType.IconButton -> {
            IconButton(
                onClick = {
                    onClick?.invoke()
                },
                enabled = enabled,
                colors = IconButtonColors(
                    containerColor = type.buttonColor,
                    disabledContainerColor = type.buttonColor.copy(alpha = disabledAlpha),
                    contentColor = type.contentColor,
                    disabledContentColor = type.contentColor.copy(alpha = disabledAlpha)
                ),
                modifier = modifier
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    when (type.icon) {
                        SeesturmButtonIconType.None -> {
                            // nothing
                        }
                        is SeesturmButtonIconType.Custom -> {
                            Image(
                                painter = type.icon.image,
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .alpha(
                                        if (isLoading) {
                                            0f
                                        } else {
                                            1f
                                        }
                                    )
                            )
                        }
                        is SeesturmButtonIconType.Predefined -> {
                            Icon(
                                imageVector = type.icon.icon,
                                contentDescription = null,
                                tint = type.contentColor,
                                modifier = Modifier
                                    .alpha(
                                        if (isLoading) {
                                            0f
                                        } else {
                                            1f
                                        }
                                    )
                            )
                        }
                    }
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = type.contentColor,
                            modifier = Modifier
                                .size(18.dp)
                        )
                    }
                }
            }
        }
        is SeesturmButtonType.Primary -> {
            Button(
                onClick = {
                    onClick?.invoke()
                },
                enabled = enabled,
                colors = ButtonColors(
                    containerColor = type.buttonColor,
                    contentColor = type.contentColor,
                    disabledContainerColor = type.buttonColor.copy(alpha = disabledAlpha),
                    disabledContentColor = type.contentColor
                ),
                modifier = modifier
            ) {
                SeesturmButtonContent(
                    icon = type.icon,
                    contentColor = type.contentColor,
                    title = title,
                    enabled = enabled,
                    isLoading = isLoading
                )
            }
        }
        is SeesturmButtonType.Secondary -> {
            OutlinedButton(
                onClick = {
                    onClick?.invoke()
                },
                colors = ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = type.contentColor,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = type.contentColor.copy(alpha = disabledAlpha)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = type.borderColor().copy(
                        alpha = if (!enabled) {
                            disabledAlpha
                        }
                        else {
                            1.0f
                        }
                    )
                ),
                enabled = enabled,
                modifier = modifier
            ) {
                SeesturmButtonContent(
                    icon = type.icon,
                    contentColor = type.contentColor,
                    title = title,
                    enabled = enabled,
                    isLoading = isLoading
                )
            }
        }
        is SeesturmButtonType.Tertiary -> {
            TextButton(
                onClick = {
                    onClick?.invoke()
                },
                enabled = enabled,
                modifier = modifier
            ) {
                SeesturmButtonContent(
                    icon = type.icon,
                    contentColor = type.contentColor,
                    title = title,
                    enabled = enabled,
                    isLoading = isLoading
                )
            }
        }
    }
}

@Composable
private fun SeesturmButtonContent(
    icon: SeesturmButtonIconType,
    contentColor: Color,
    title: String?,
    enabled: Boolean,
    isLoading: Boolean
) {

    val disabledAlpha = 0.6f
    val iconSize = 18.dp

    Box(
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .alpha(
                    if (isLoading) {
                        0f
                    } else {
                        1f
                    }
                )
        ) {
            if (title != null) {
                Text(
                    text = title,
                    color = contentColor.copy(
                        alpha = if (!enabled) {
                            disabledAlpha
                        }
                        else {
                            1.0f
                        }
                    )
                )
            }
            when (icon) {
                is SeesturmButtonIconType.Custom -> {
                    Image(
                        painter = icon.image,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(contentColor.copy(
                            alpha = if (!enabled) {
                                disabledAlpha
                            }
                            else {
                                1.0f
                            }
                        )),
                        modifier = Modifier
                            .size(iconSize)
                    )
                }
                is SeesturmButtonIconType.Predefined -> {
                    Icon(
                        imageVector = icon.icon,
                        contentDescription = null,
                        tint = contentColor.copy(
                            alpha = if (!enabled) {
                                disabledAlpha
                            }
                            else {
                                1.0f
                            }
                        ),
                        modifier = Modifier
                            .size(iconSize)
                    )
                }
                SeesturmButtonIconType.None -> {
                    // nothing
                }
            }
        }
        if (isLoading) {
            CircularProgressIndicator(
                color = contentColor,
                modifier = Modifier
                    .size(iconSize)
            )
        }
    }
}

@Composable
fun DropdownButton(
    title: String?,
    dropdown: @Composable (isShown: Boolean, dismiss: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    buttonColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    icon: SeesturmButtonIconType = SeesturmButtonIconType.Predefined(
        icon = Icons.Default.ArrowDropDown
    ),
    isLoading: Boolean = false,
    enabled: Boolean = !isLoading,
    contentColor: Color = Color.SEESTURM_GREEN
) {

    var showMenu by rememberSaveable { mutableStateOf(false) }

    Box {
        SeesturmButton(
            type = SeesturmButtonType.Primary(
                buttonColor = buttonColor,
                contentColor = contentColor,
                icon = icon
            ),
            title = title,
            isLoading = isLoading,
            onClick = { showMenu = true },
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
fun CustomButtonPreview() {
    PfadiSeesturmTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SeesturmButton(
                    type = SeesturmButtonType.Primary(),
                    onClick = {},
                    title = "Test 1"
                )
                SeesturmButton(
                    type = SeesturmButtonType.Primary(
                        buttonColor = Color.SEESTURM_BLUE,
                        contentColor = Color.Black,
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Outlined.House
                        )
                    ),
                    enabled = false,
                    onClick = {},
                    title = "Test 2"
                )
                SeesturmButton(
                    type = SeesturmButtonType.Primary(
                        buttonColor = Color.SEESTURM_BLUE,
                        contentColor = Color.Black,
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Outlined.House
                        )
                    ),
                    isLoading = true,
                    onClick = {},
                    title = "Test 3"
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SeesturmButton(
                    type = SeesturmButtonType.Secondary(),
                    onClick = {},
                    title = "Test 1"
                )
                SeesturmButton(
                    type = SeesturmButtonType.Secondary(
                        contentColor = Color.SEESTURM_BLUE,
                        icon = SeesturmButtonIconType.Custom(
                            image = painterResource(R.drawable.midata_logo)
                        )
                    ),
                    enabled = false,
                    onClick = {},
                    title = "Test 2"
                )
                SeesturmButton(
                    type = SeesturmButtonType.Secondary(
                        contentColor = Color.SEESTURM_BLUE,
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Outlined.House
                        )
                    ),
                    isLoading = true,
                    onClick = {},
                    title = "Test 3"
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SeesturmButton(
                    type = SeesturmButtonType.Tertiary(),
                    onClick = {},
                    title = "Test 1"
                )
                SeesturmButton(
                    type = SeesturmButtonType.Tertiary(
                        contentColor = Color.SEESTURM_BLUE,
                        icon = SeesturmButtonIconType.Custom(
                            image = painterResource(R.drawable.midata_logo)
                        )
                    ),
                    onClick = {},
                    title = "Test 2"
                )
                SeesturmButton(
                    type = SeesturmButtonType.Tertiary(
                        contentColor = Color.SEESTURM_BLUE,
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Outlined.House
                        )
                    ),
                    isLoading = true,
                    onClick = {},
                    title = "Test 3"
                )
            }
            DropdownButton(
                title = "Dropdown",
                dropdown = { isShown, dismiss ->}
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SeesturmButton(
                    type = SeesturmButtonType.IconButton(
                        buttonColor = MaterialTheme.colorScheme.background,
                        contentColor = Color.SEESTURM_RED,
                        icon = SeesturmButtonIconType.Custom(
                            image = painterResource(R.drawable.logotabbar)
                        )
                    ),
                    title = null
                )
                SeesturmButton(
                    type = SeesturmButtonType.IconButton(
                        buttonColor = MaterialTheme.colorScheme.background,
                        contentColor = Color.SEESTURM_RED,
                        icon = SeesturmButtonIconType.Custom(
                            image = painterResource(R.drawable.logotabbar)
                        )
                    ),
                    title = null,
                    isLoading = true
                )
                SeesturmButton(
                    type = SeesturmButtonType.IconButton(
                        buttonColor = MaterialTheme.colorScheme.background,
                        contentColor = Color.SEESTURM_GREEN,
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Default.House
                        )
                    ),
                    title = null
                )
            }
        }
    }
}

sealed class SeesturmButtonType {
    data class Primary(
        val buttonColor: Color = Color.SEESTURM_RED,
        val contentColor: Color = Color.White,
        val icon: SeesturmButtonIconType = SeesturmButtonIconType.None
    ): SeesturmButtonType()
    data class Secondary(
        val borderColor: @Composable () -> Color = { MaterialTheme.colorScheme.onBackground },
        val contentColor: Color = Color.SEESTURM_GREEN,
        val icon: SeesturmButtonIconType = SeesturmButtonIconType.None
    ): SeesturmButtonType()
    data class Tertiary(
        val contentColor: Color = Color.SEESTURM_GREEN,
        val icon: SeesturmButtonIconType = SeesturmButtonIconType.None
    ): SeesturmButtonType()
    data class IconButton(
        val buttonColor: Color,
        val contentColor: Color,
        val icon: SeesturmButtonIconType
    ): SeesturmButtonType()
}
sealed class SeesturmButtonIconType {
    data object None: SeesturmButtonIconType()
    data class Custom(
        val image: Painter
    ): SeesturmButtonIconType()
    data class Predefined(
        val icon: ImageVector
    ): SeesturmButtonIconType()
}