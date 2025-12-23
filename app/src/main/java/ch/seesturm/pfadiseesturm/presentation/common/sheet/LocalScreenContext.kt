package ch.seesturm.pfadiseesturm.presentation.common.sheet

import androidx.compose.runtime.compositionLocalOf

val LocalScreenContext = compositionLocalOf<ScreenContext> {
    ScreenContext.Default
}