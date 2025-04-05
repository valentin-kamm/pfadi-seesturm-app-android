package ch.seesturm.pfadiseesturm.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Color.SEESTURM_GREEN,
    background = Color(242, 242, 247),
    onBackground = Color.Black,
    primaryContainer = Color.White, // color for card views
    secondaryContainer = Color(229, 229, 234), // color for gray card views
    onSurfaceVariant = Color(214, 214, 214), // Color for placeholder text etc.
    inverseSurface = Color.SEESTURM_GREEN // default shadow for card views
)

private val DarkColorScheme = darkColorScheme(
    primary = Color.SEESTURM_GREEN,
    background = Color.Black,
    onBackground = Color.White,
    primaryContainer = Color(28, 28, 30), // Color for card views
    secondaryContainer = Color(44, 44, 46), // color for gray card views
    onSurfaceVariant = Color(64, 64, 64), // Color for placeholder text etc.
    inverseSurface = Color.Transparent // default shadow for card views
)

@Composable
fun PfadiSeesturmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}