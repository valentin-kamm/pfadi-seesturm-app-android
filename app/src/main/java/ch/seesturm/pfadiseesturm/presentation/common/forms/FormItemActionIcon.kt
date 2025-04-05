package ch.seesturm.pfadiseesturm.presentation.common.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_RED

@Composable
fun FormItemActionIcon(
    onClick: () -> Unit,
    backgroundColor: Color,
    icon: ImageVector,
    iconTint: Color = Color.White,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .background(backgroundColor)
            .fillMaxHeight()
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
private fun FormItemActionIconPreview() {
    FormItemActionIcon(
        onClick = {  },
        backgroundColor = Color.SEESTURM_RED,
        icon = Icons.Filled.Delete
    )
}