package ch.seesturm.pfadiseesturm.presentation.common.forms

import androidx.compose.ui.graphics.Color

sealed class FormItemTextContentColor {
    data class Custom(
        val color: Color
    ): FormItemTextContentColor()
    data object Default: FormItemTextContentColor()
}