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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_BLUE
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED

@Composable
fun SeesturmButton(
    type: SeesturmButtonType,
    onClick: (() -> Unit)?,
    title: String?,
    icon: SeesturmButtonIconType = SeesturmButtonIconType.None,
    colors: SeesturmButtonColor = SeesturmButtonColor.Predefined,
    isLoading: Boolean = false,
    enabled: Boolean = !isLoading,
    disabledAlpha: Float = 0.6f,
    iconSize: Dp = 18.dp,
    modifier: Modifier = Modifier,
    allowHorizontalTextShrink: Boolean = true
) {

    val contentColor = remember(type, colors) {
        when (colors) {
            SeesturmButtonColor.Predefined -> type.defaultContentColor
            is SeesturmButtonColor.Custom -> colors.contentColor
        }
    }
    val buttonColor: Color = remember(type, colors) {
        when (colors) {
            SeesturmButtonColor.Predefined -> type.defaultButtonColor
            is SeesturmButtonColor.Custom -> colors.buttonColor
        }
    }
    val contentAlpha: Float = remember(isLoading) {
        if (isLoading) {
            0f
        }
        else {
            1f
        }
    }

    when (type) {
        is SeesturmButtonType.Icon -> {
            IconButton(
                onClick = { onClick?.invoke() },
                enabled = enabled,
                colors = IconButtonColors(
                    containerColor = buttonColor,
                    disabledContainerColor = buttonColor.copy(alpha = disabledAlpha),
                    contentColor = contentColor,
                    disabledContentColor = contentColor
                ),
                modifier = modifier
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    when (icon) {
                        SeesturmButtonIconType.None -> {}
                        is SeesturmButtonIconType.Custom -> {
                            Image(
                                painter = icon.image,
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .alpha(contentAlpha)
                            )
                        }
                        is SeesturmButtonIconType.Predefined -> {
                            Icon(
                                imageVector = icon.icon,
                                contentDescription = null,
                                tint = contentColor,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .alpha(contentAlpha)
                            )
                        }
                    }
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = contentColor,
                            modifier = Modifier
                                .size(18.dp)
                        )
                    }
                }
            }
        }
        is SeesturmButtonType.Primary -> {
            Button(
                onClick = { onClick?.invoke() },
                enabled = enabled,
                colors = ButtonColors(
                    containerColor = buttonColor,
                    disabledContainerColor = buttonColor.copy(alpha = disabledAlpha),
                    contentColor = contentColor,
                    disabledContentColor = contentColor
                ),
                modifier = modifier
            ) {
                SeesturmButtonContent(
                    icon = icon,
                    contentColor = contentColor,
                    title = title,
                    isLoading = isLoading,
                    allowHorizontalTextShrink = allowHorizontalTextShrink,
                    iconSize = iconSize
                )
            }
        }
        is SeesturmButtonType.Secondary -> {
            Button(
                onClick = { onClick?.invoke() },
                colors = ButtonColors(
                    containerColor = buttonColor,
                    disabledContainerColor = buttonColor.copy(alpha = disabledAlpha),
                    contentColor = contentColor,
                    disabledContentColor = contentColor
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
                    icon = icon,
                    contentColor = contentColor,
                    title = title,
                    isLoading = isLoading,
                    allowHorizontalTextShrink = allowHorizontalTextShrink,
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
    iconSize: Dp,
    allowHorizontalTextShrink: Boolean
) {

    val contentAlpha: Float = remember(isLoading) {
        if (isLoading) {
            0f
        }
        else {
            1f
        }
    }

    Box(
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .alpha(contentAlpha)
        ) {
            if (title != null) {
                Text(
                    text = title,
                    color = contentColor,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .then(
                            if (allowHorizontalTextShrink) {
                                Modifier
                                    .weight(1f, fill = false)
                            }
                            else {
                                Modifier
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
                SeesturmButtonIconType.None -> { }
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
                    type = SeesturmButtonType.Primary,
                    colors = SeesturmButtonColor.Custom(
                        contentColor = Color.Black,
                        buttonColor = Color.SEESTURM_RED
                    ),
                    title = "Primary",
                    onClick = null,
                    icon = SeesturmButtonIconType.None
                )
                // with icon
                SeesturmButton(
                    type = SeesturmButtonType.Primary,
                    colors = SeesturmButtonColor.Custom(
                        contentColor = Color.Black,
                        buttonColor = Color.SEESTURM_RED
                    ),
                    icon = SeesturmButtonIconType.Predefined(
                        icon = Icons.Outlined.House
                    ),
                    title = "Primary",
                    onClick = null
                )
                // simple loading
                SeesturmButton(
                    type = SeesturmButtonType.Primary,
                    colors = SeesturmButtonColor.Predefined,
                    icon = SeesturmButtonIconType.None,
                    title = "Primary",
                    onClick = null,
                    isLoading = true
                )
                // icon loading
                SeesturmButton(
                    type = SeesturmButtonType.Primary,
                    icon = SeesturmButtonIconType.Predefined(
                        icon = Icons.Outlined.House
                    ),
                    title = "Primary",
                    onClick = null,
                    isLoading = true
                )
                // simple disabled
                SeesturmButton(
                    type = SeesturmButtonType.Primary,
                    title = "Primary",
                    onClick = null,
                    enabled = false
                )
                // icon disabled
                SeesturmButton(
                    type = SeesturmButtonType.Primary,
                    icon = SeesturmButtonIconType.Predefined(
                        icon = Icons.Outlined.House
                    ),
                    title = "Primary",
                    onClick = null,
                    enabled = false
                )
                // custom icon
                SeesturmButton(
                    type = SeesturmButtonType.Primary,
                    icon = SeesturmButtonIconType.Custom(
                        image = painterResource(R.drawable.midata_logo)
                    ),
                    colors = SeesturmButtonColor.Custom(
                        contentColor = Color.Black,
                        buttonColor = Color.SEESTURM_RED
                    ),
                    title = "Primary",
                    onClick = null
                )
                // custom icon disabled
                SeesturmButton(
                    type = SeesturmButtonType.Primary,
                    icon = SeesturmButtonIconType.Custom(
                        image = painterResource(R.drawable.midata_logo)
                    ),
                    title = "Primary",
                    onClick = null,
                    enabled = false
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // simple
                SeesturmButton(
                    type = SeesturmButtonType.Secondary,
                    colors = SeesturmButtonColor.Custom(
                        contentColor = Color.Black,
                        buttonColor = Color.SEESTURM_GREEN
                    ),
                    title = "Secondary",
                    onClick = null
                )
                // with icon
                SeesturmButton(
                    type = SeesturmButtonType.Secondary,
                    icon = SeesturmButtonIconType.Predefined(
                        icon = Icons.Outlined.House
                    ),
                    title = "Secondary",
                    onClick = null
                )
                // simple loading
                SeesturmButton(
                    type = SeesturmButtonType.Secondary,
                    colors = SeesturmButtonColor.Custom(
                        contentColor = Color.Black,
                        buttonColor = Color.SEESTURM_GREEN
                    ),
                    title = "Secondary",
                    onClick = null,
                    isLoading = true
                )
                // icon loading
                SeesturmButton(
                    type = SeesturmButtonType.Secondary,
                    icon = SeesturmButtonIconType.Predefined(
                        icon = Icons.Outlined.House
                    ),
                    title = "Secondary",
                    onClick = null,
                    isLoading = true
                )
                // simple disabled
                SeesturmButton(
                    type = SeesturmButtonType.Secondary,
                    colors = SeesturmButtonColor.Custom(
                        contentColor = Color.Black,
                        buttonColor = Color.SEESTURM_GREEN
                    ),
                    title = "Secondary",
                    onClick = null,
                    enabled = false
                )
                // icon disabled
                SeesturmButton(
                    type = SeesturmButtonType.Secondary,
                    icon = SeesturmButtonIconType.Predefined(
                        icon = Icons.Outlined.House
                    ),
                    title = "Secondary",
                    onClick = null,
                    enabled = false
                )
                // custom icon
                SeesturmButton(
                    type = SeesturmButtonType.Secondary,
                    icon = SeesturmButtonIconType.Custom(
                        image = painterResource(R.drawable.midata_logo)
                    ),
                    colors = SeesturmButtonColor.Custom(
                        contentColor = Color.Black,
                        buttonColor = Color.SEESTURM_GREEN
                    ),
                    title = "Secondary",
                    onClick = null
                )
                // custom icon disabled
                SeesturmButton(
                    type = SeesturmButtonType.Secondary,
                    icon = SeesturmButtonIconType.Custom(
                        image = painterResource(R.drawable.midata_logo)
                    ),
                    title = "Secondary",
                    onClick = null,
                    enabled = false
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // simple
                SeesturmButton(
                    type = SeesturmButtonType.Icon,
                    icon = SeesturmButtonIconType.Predefined(
                        icon = Icons.Outlined.Cake
                    ),
                    colors = SeesturmButtonColor.Custom(
                        buttonColor = Color.SEESTURM_BLUE,
                        contentColor = Color.Black
                    ),
                    title = null,
                    onClick = null
                )
                // simple loading
                SeesturmButton(
                    type = SeesturmButtonType.Icon,
                    icon = SeesturmButtonIconType.Predefined(
                        icon = Icons.Outlined.Cake
                    ),
                    isLoading = true,
                    title = null,
                    onClick = null
                )
                // simple disabled
                SeesturmButton(
                    type = SeesturmButtonType.Icon,
                    icon = SeesturmButtonIconType.Predefined(
                        icon = Icons.Outlined.Cake
                    ),
                    title = null,
                    onClick = null,
                    enabled = false
                )
                // custom icon
                SeesturmButton(
                    type = SeesturmButtonType.Icon,
                    colors = SeesturmButtonColor.Custom(
                        buttonColor = Color.SEESTURM_BLUE,
                        contentColor = Color.White
                    ),
                    icon = SeesturmButtonIconType.Custom(
                        image = painterResource(R.drawable.midata_logo)
                    ),
                    title = null,
                    onClick = null
                )
                // custom icon disabled
                SeesturmButton(
                    type = SeesturmButtonType.Icon,
                    icon = SeesturmButtonIconType.Custom(
                        image = painterResource(R.drawable.midata_logo)
                    ),
                    title = null,
                    onClick = null,
                    enabled = false
                )
                // custom icon loading
                SeesturmButton(
                    type = SeesturmButtonType.Icon,
                    icon = SeesturmButtonIconType.Custom(
                        image = painterResource(R.drawable.midata_logo)
                    ),
                    title = null,
                    onClick = null,
                    isLoading = true
                )
            }
        }
    }
}