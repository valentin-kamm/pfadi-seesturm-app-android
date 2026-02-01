package ch.seesturm.pfadiseesturm.presentation.common.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ch.seesturm.pfadiseesturm.presentation.common.sheet.LocalScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ScreenContext

val Color.Companion.SEESTURM_GREEN: Color
    get() = Color(42, 100, 56)
val Color.Companion.SEESTURM_RED: Color
    get() = Color(202, 43, 54)
val Color.Companion.SEESTURM_BLUE: Color
    get() = Color(33, 84, 155)
val Color.Companion.SEESTURM_YELLOW: Color
    get() = Color(248, 213, 72)

@Composable
fun Color.Companion.cardOnCardBackgroundColor(isDarkTheme: Boolean): Color {
    if (isDarkTheme) {
        return MaterialTheme.colorScheme.secondaryContainer
    }
    else {
        return if (LocalScreenContext.current is ScreenContext.ModalBottomSheet) {
            MaterialTheme.colorScheme.tertiaryContainer
        }
        else {
            MaterialTheme.colorScheme.primaryContainer
        }
    }
}