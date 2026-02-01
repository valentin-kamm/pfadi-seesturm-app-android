package ch.seesturm.pfadiseesturm.presentation.common.sheet

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarHost
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarLocation
import ch.seesturm.pfadiseesturm.util.Binding
import com.composables.core.BottomSheetScope
import com.composables.core.ModalSheetProperties
import com.composables.core.rememberModalBottomSheetState

@Composable
fun SimpleModalBottomSheet(
    show: Binding<Boolean>,
    detents: SheetDetents,
    type: SheetScaffoldType,
    appStateViewModel: AppStateViewModel,
    keyboardResponse: ModalBottomSheetKeyboardResponse,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    enabled: Boolean = true,
    content: @Composable BottomSheetScope.(dismiss: () -> Unit, viewModelStoreOwner: ViewModelStoreOwner) -> Unit
) {

    val appState by appStateViewModel.state.collectAsStateWithLifecycle()

    val viewModelStore = remember { ViewModelStore() }

    val viewModelStoreOwner = remember {
        object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore = viewModelStore
        }
    }

    val density = LocalDensity.current

    val sheetState = rememberModalBottomSheetState(
        detents = detents.sheetDetents,
        initialDetent = ModalBottomSheetDetent.Hidden.sheetDetent
    )

    LaunchedEffect(show.get()) {
        sheetState.targetDetent = if (show.get()) {
            detents.defaultDetent.sheetDetent
        }
        else {
            ModalBottomSheetDetent.Hidden.sheetDetent
        }
    }

    fun dismiss() {
        show.set(false)
        onDismiss?.invoke()
        viewModelStoreOwner.viewModelStore.clear()
    }

    BoxWithConstraints {
        ModalBottomSheetContentView(
            sheetState = sheetState,
            detents = detents,
            type = type,
            properties = ModalSheetProperties(
                dismissOnBackPress = dismissOnBackPress,
                dismissOnClickOutside = dismissOnClickOutside
            ),
            onDismiss = { dismiss() },
            modifier = modifier,
            enabled = enabled,
            isDarkMode = appState.theme.isDarkTheme,
            snackbarHost = {
                SeesturmSnackbarHost(
                    location = SeesturmSnackbarLocation.Sheet,
                    modifier = Modifier
                        .padding(
                            bottom = sheetState.visibleAreaBottomPadding(
                                density = density,
                                maxHeight = maxHeight,
                                detents = detents
                            )
                        )
                )
            },
            content = {
                content(
                    { dismiss() },
                    viewModelStoreOwner
                )
            },
            keyboardResponse = keyboardResponse
        )
    }
}