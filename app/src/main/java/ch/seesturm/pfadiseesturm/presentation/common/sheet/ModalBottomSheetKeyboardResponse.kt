package ch.seesturm.pfadiseesturm.presentation.common.sheet

sealed interface ModalBottomSheetKeyboardResponse {
    data object GrowSheet: ModalBottomSheetKeyboardResponse
    data object ScrollContent: ModalBottomSheetKeyboardResponse
    data object None: ModalBottomSheetKeyboardResponse
}