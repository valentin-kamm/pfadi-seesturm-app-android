package ch.seesturm.pfadiseesturm.presentation.common.forms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

sealed class FormItemContentType {
    data class Text(
        val title: String,
        val isLoading: Boolean = false,
        val textColor: FormItemTextContentColor = FormItemTextContentColor.Default,
    ): FormItemContentType()
    data class Custom(
        val content: @Composable () -> Unit,
        val contentPadding: PaddingValues = PaddingValues(0.dp)
    ): FormItemContentType()
}