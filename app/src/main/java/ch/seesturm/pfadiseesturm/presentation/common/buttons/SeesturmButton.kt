package ch.seesturm.pfadiseesturm.presentation.common.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.House
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_BLUE

@Composable
fun SeesturmButton(
    type: SeesturmButtonType,
    title: String?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    isLoading: Boolean = false,
    enabled: Boolean = !isLoading,
    disabledAlpha: Float = 0.6f,
    iconSize: Dp = 18.dp
) {

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
                    disabledContentColor = type.contentColor
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
                                    .padding(8.dp)
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
                                    .padding(4.dp)
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
                    isLoading = isLoading,
                    iconSize = iconSize
                )
            }
        }
        is SeesturmButtonType.Secondary -> {
            Button(
                onClick = {
                    onClick?.invoke()
                },
                colors = ButtonColors(
                    containerColor = type.buttonColor,
                    contentColor = type.contentColor,
                    disabledContainerColor = type.buttonColor.copy(alpha = disabledAlpha),
                    disabledContentColor = type.contentColor
                ),
                enabled = enabled,
                modifier = modifier
                    .height(32.dp),
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 6.dp
                )
            ) {
                SeesturmButtonContent(
                    icon = type.icon,
                    contentColor = type.contentColor,
                    title = title,
                    isLoading = isLoading,
                    iconSize = iconSize
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
    isLoading: Boolean,
    iconSize: Dp
) {

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
                    color = contentColor
                )
            }
            when (icon) {
                is SeesturmButtonIconType.Custom -> {
                    Image(
                        painter = icon.image,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(contentColor),
                        modifier = Modifier
                            .size(iconSize)
                    )
                }
                is SeesturmButtonIconType.Predefined -> {
                    Icon(
                        imageVector = icon.icon,
                        contentDescription = null,
                        tint = contentColor,
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

@Preview(showBackground = true)
@Composable
private fun CustomButtonPreview() {
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
                SeesturmButton(
                    type = SeesturmButtonType.Primary(
                        contentColor = Color.Black
                    ),
                    title = "Primary",
                    onClick = {},
                )
                // with icon
                SeesturmButton(
                    type = SeesturmButtonType.Primary(
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Outlined.House
                        ),
                        contentColor = Color.Black
                    ),
                    title = "Primary",
                    onClick = {},
                )
                // simple loading
                SeesturmButton(
                    type = SeesturmButtonType.Primary(
                        contentColor = Color.Black
                    ),
                    title = "Primary",
                    onClick = {},
                    isLoading = true
                )
                // icon loading
                SeesturmButton(
                    type = SeesturmButtonType.Primary(
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Outlined.House
                        ),
                        contentColor = Color.Black
                    ),
                    title = "Primary",
                    onClick = {},
                    isLoading = true
                )
                // simple disabled
                SeesturmButton(
                    type = SeesturmButtonType.Primary(
                        contentColor = Color.Black
                    ),
                    title = "Primary",
                    onClick = {},
                    enabled = false
                )
                // icon disabled
                SeesturmButton(
                    type = SeesturmButtonType.Primary(
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Outlined.House
                        ),
                        contentColor = Color.Black
                    ),
                    title = "Primary",
                    onClick = {},
                    enabled = false
                )
                // custom icon
                SeesturmButton(
                    type = SeesturmButtonType.Primary(
                        icon = SeesturmButtonIconType.Custom(
                            image = painterResource(R.drawable.midata_logo)
                        ),
                        contentColor = Color.Black
                    ),
                    title = "Primary",
                    onClick = {}
                )
                // custom icon disabled
                SeesturmButton(
                    type = SeesturmButtonType.Primary(
                        icon = SeesturmButtonIconType.Custom(
                            image = painterResource(R.drawable.midata_logo)
                        ),
                        contentColor = Color.Black
                    ),
                    title = "Primary",
                    onClick = {},
                    enabled = false
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // simple
                SeesturmButton(
                    type = SeesturmButtonType.Secondary(
                        contentColor = Color.Black
                    ),
                    title = "Secondary",
                    onClick = {},
                )
                // with icon
                SeesturmButton(
                    type = SeesturmButtonType.Secondary(
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Outlined.House
                        ),
                        contentColor = Color.Black
                    ),
                    title = "Secondary",
                    onClick = {},
                )
                // simple loading
                SeesturmButton(
                    type = SeesturmButtonType.Secondary(
                        contentColor = Color.Black
                    ),
                    title = "Secondary",
                    onClick = {},
                    isLoading = true
                )
                // icon loading
                SeesturmButton(
                    type = SeesturmButtonType.Secondary(
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Outlined.House
                        ),
                        contentColor = Color.Black
                    ),
                    title = "Secondary",
                    onClick = {},
                    isLoading = true
                )
                // simple disabled
                SeesturmButton(
                    type = SeesturmButtonType.Secondary(
                        contentColor = Color.Black
                    ),
                    title = "Secondary",
                    onClick = {},
                    enabled = false
                )
                // icon disabled
                SeesturmButton(
                    type = SeesturmButtonType.Secondary(
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Outlined.House
                        ),
                        contentColor = Color.Black
                    ),
                    title = "Secondary",
                    onClick = {},
                    enabled = false
                )
                // custom icon
                SeesturmButton(
                    type = SeesturmButtonType.Secondary(
                        icon = SeesturmButtonIconType.Custom(
                            image = painterResource(R.drawable.midata_logo)
                        ),
                        contentColor = Color.Black
                    ),
                    title = "Secondary",
                    onClick = {}
                )
                // custom icon disabled
                SeesturmButton(
                    type = SeesturmButtonType.Secondary(
                        icon = SeesturmButtonIconType.Custom(
                            image = painterResource(R.drawable.midata_logo)
                        ),
                        contentColor = Color.Black
                    ),
                    title = "Secondary",
                    onClick = {},
                    enabled = false
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // simple
                SeesturmButton(
                    type = SeesturmButtonType.IconButton(
                        buttonColor = Color.SEESTURM_BLUE,
                        contentColor = Color.Black,
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Outlined.Cake
                        )
                    ),
                    title = null,
                    onClick = {},
                )
                // simple loading
                SeesturmButton(
                    type = SeesturmButtonType.IconButton(
                        buttonColor = Color.SEESTURM_BLUE,
                        contentColor = Color.Black,
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Outlined.Cake
                        )
                    ),
                    isLoading = true,
                    title = null,
                    onClick = {},
                )
                // simple disabled
                SeesturmButton(
                    type = SeesturmButtonType.IconButton(
                        buttonColor = Color.SEESTURM_BLUE,
                        contentColor = Color.Black,
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Outlined.Cake
                        )
                    ),
                    title = null,
                    onClick = {},
                    enabled = false
                )
                // custom icon
                SeesturmButton(
                    type = SeesturmButtonType.IconButton(
                        buttonColor = Color.SEESTURM_BLUE,
                        contentColor = Color.Black,
                        icon = SeesturmButtonIconType.Custom(
                            image = painterResource(R.drawable.midata_logo)
                        )
                    ),
                    title = null,
                    onClick = {},
                )
                // custom icon disabled
                SeesturmButton(
                    type = SeesturmButtonType.IconButton(
                        buttonColor = Color.SEESTURM_BLUE,
                        contentColor = Color.Black,
                        icon = SeesturmButtonIconType.Custom(
                            image = painterResource(R.drawable.midata_logo)
                        )
                    ),
                    title = null,
                    onClick = {},
                    enabled = false
                )
                // custom icon loading
                SeesturmButton(
                    type = SeesturmButtonType.IconButton(
                        buttonColor = Color.SEESTURM_BLUE,
                        contentColor = Color.Black,
                        icon = SeesturmButtonIconType.Custom(
                            image = painterResource(R.drawable.midata_logo)
                        )
                    ),
                    title = null,
                    onClick = {},
                    isLoading = true
                )
            }
        }
    }
}