package ch.seesturm.pfadiseesturm.presentation.common.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED

@Composable
fun GroupedColumnItemActionIcon(
    onClick: () -> Unit,
    backgroundColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.White
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(horizontal = 8.dp)
            .fillMaxHeight()
            .wrapContentWidth()
    ) {
        Icon(
            imageVector = icon,
            tint = iconTint,
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun GroupedColumnItemActionIconPreview() {
    GroupedColumnItemActionIcon(
        onClick = {  },
        backgroundColor = Color.SEESTURM_RED,
        icon = Icons.Filled.Delete,
        modifier = Modifier
            .height(40.dp)
    )
}