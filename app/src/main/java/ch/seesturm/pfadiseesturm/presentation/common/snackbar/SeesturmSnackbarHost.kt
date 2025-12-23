package ch.seesturm.pfadiseesturm.presentation.common.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import ch.seesturm.pfadiseesturm.util.ObserveAsEvents
import kotlinx.coroutines.launch

@Composable
fun SeesturmSnackbarHost(
    location: SeesturmSnackbarLocation,
    modifier: Modifier = Modifier,
    type: SeesturmSnackbarHostType = SeesturmSnackbarHostType.Default,
) {

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    fun showSnackbar(visuals: SeesturmSnackbarVisuals) {

        // dismiss current snackbar
        snackbarHostState.currentSnackbarData?.dismiss()

        // show new snackbar, but only if location matches
        if (visuals.location == location) {
            coroutineScope.launch {
                val snackbarResult = snackbarHostState.showSnackbar(visuals)
                if (snackbarResult == SnackbarResult.Dismissed) {
                    visuals.onDismiss()
                }
            }
        }
    }

    when (type) {
        SeesturmSnackbarHostType.Default -> {
            ObserveAsEvents(
                flow = SnackbarController.events,
                key1 = snackbarHostState
            ) { visuals ->
                showSnackbar(visuals)
            }
        }
        is SeesturmSnackbarHostType.StaticInfoSnackbar -> {
            LaunchedEffect(type) {
                showSnackbar(
                    visuals = SeesturmSnackbar.Info(
                        message = type.message,
                        onDismiss = {},
                        location = SeesturmSnackbarLocation.Default,
                        allowManualDismiss = false,
                        duration = SnackbarDuration.Indefinite
                    ).visuals
                )
            }
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier
    ) { snackbarData ->

        val seesturmSnackbarVisuals = snackbarData.visuals as? SeesturmSnackbarVisuals

        if (seesturmSnackbarVisuals != null) {
            SeesturmSnackbarView(
                data = snackbarData,
                visuals = seesturmSnackbarVisuals
            )
        }
    }
}