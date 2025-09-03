package ch.seesturm.pfadiseesturm.presentation.common.sheet

sealed interface ScreenContext {
    data object Default: ScreenContext
    data object ModalBottomSheet: ScreenContext
}