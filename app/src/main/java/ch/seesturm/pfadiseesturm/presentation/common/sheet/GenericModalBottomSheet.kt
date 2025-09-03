package ch.seesturm.pfadiseesturm.presentation.common.sheet

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarHost
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarLocation
import com.composables.core.BottomSheetScope
import com.composables.core.ModalSheetProperties
import com.composables.core.rememberModalBottomSheetState

@Composable
fun <D> GenericModalBottomSheet(
    item: MutableState<D?>,
    detents: AllowedSheetDetents,
    type: SheetScaffoldType,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    enabled: Boolean = true,
    content: @Composable BottomSheetScope.(item: D, dismiss: () -> Unit, viewModelStoreOwner: ViewModelStoreOwner) -> Unit
) {

    val viewModelStore = remember { ViewModelStore() }

    val viewModelStoreOwner = remember {
        object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore = viewModelStore
        }
    }

    val density = LocalDensity.current

    var currentItem by remember { mutableStateOf<D?>(null) }

    val sheetState = rememberModalBottomSheetState(
        detents = detents.sheetDetents,
        initialDetent = ModalBottomSheetDetent.Hidden.sheetDetent
    )
    LaunchedEffect(item.value) {
        sheetState.targetDetent = if (item.value != null) {
            currentItem = item.value
            detents.defaultDetent.sheetDetent
        }
        else {
            ModalBottomSheetDetent.Hidden.sheetDetent
        }
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
            onDismiss = {
                currentItem = null
                item.value = null
                onDismiss?.invoke()
                viewModelStoreOwner.viewModelStore.clear()
            },
            modifier = modifier,
            enabled = enabled,
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
                currentItem?.let {
                    content(
                        it,
                        {
                            item.value = null
                        },
                        viewModelStoreOwner
                    )
                }
            }
        )
    }
}