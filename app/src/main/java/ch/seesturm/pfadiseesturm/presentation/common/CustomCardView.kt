package ch.seesturm.pfadiseesturm.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme

@Composable
fun CustomCardView(
    modifier: Modifier = Modifier,
    shadowColor: Color = MaterialTheme.colorScheme.inverseSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(16.dp),
                clip = true,
                ambientColor = shadowColor.copy(alpha = 0.6f),
                spotColor = shadowColor.copy(alpha = 0.6f)
            )
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .then(
                if (onClick != null) {
                    Modifier
                        .clickable {
                            onClick()
                        }
                }
                else {
                    Modifier
                }
            )
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomCardViewPreview() {
    PfadiSeesturmTheme {
        CustomCardView(
            shadowColor = MaterialTheme.colorScheme.inverseSurface,
            backgroundColor = Color.White,
            modifier = Modifier
                .padding(16.dp),
            content = {
                Text(
                    text = "Hallo Halli Hallo Halli Hallo Hallo Halli Hallo Halli Hallo Hallo Halli Hallo Halli Hallo Hallo Halli Hallo Halli Hallo Hallo Halli Hallo Halli Hallo Hallo Halli Hallo Halli Hallo Hallo Halli Hallo Halli Hallo Hallo Halli Hallo Halli Hallo Hallo Halli Hallo Halli Hallo Hallo Halli Hallo Halli Hallo",
                    modifier = Modifier
                        .padding(16.dp)
                )
            }
        )
    }
}