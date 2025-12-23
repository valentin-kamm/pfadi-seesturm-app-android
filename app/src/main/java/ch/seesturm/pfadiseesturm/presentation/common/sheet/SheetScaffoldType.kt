package ch.seesturm.pfadiseesturm.presentation.common.sheet

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

sealed class SheetScaffoldType {

    data object Blank: SheetScaffoldType()
    data class Title(
        val title: String
    ): SheetScaffoldType()
    data class TitleAndAction(
        val title: String,
        val actions: @Composable RowScope.() -> Unit
    ): SheetScaffoldType()

    val topBarTitle: String
        get() = when (this) {
            Blank -> ""
            is Title -> this.title
            is TitleAndAction -> this.title
        }

    val topBarActions: @Composable RowScope.() -> Unit
        get() = when (this) {
            Blank, is Title -> {{}}
            is TitleAndAction -> this.actions
        }
}