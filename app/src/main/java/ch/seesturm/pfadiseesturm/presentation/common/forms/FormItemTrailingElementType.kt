package ch.seesturm.pfadiseesturm.presentation.common.forms

import androidx.compose.runtime.Composable

sealed class FormItemTrailingElementType {
    data object DisclosureIndicator: FormItemTrailingElementType()
    data object Blank: FormItemTrailingElementType()
    data class Custom(
        val content: @Composable () -> Unit
    ): FormItemTrailingElementType()
}