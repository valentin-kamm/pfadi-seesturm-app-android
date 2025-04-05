package ch.seesturm.pfadiseesturm.presentation.common

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.ObserveAsEvents
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarView
import ch.seesturm.pfadiseesturm.presentation.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationScaffold(
    tabNavController: NavController,
    appStateViewModel: AppStateViewModel,
    content: @Composable (PaddingValues) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    ),
    sheetScaffoldScrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
) {

    val appState by appStateViewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val sheetSnackbarHostState = remember {
        SnackbarHostState()
    }

    // show snackbars
    val coroutineScope = rememberCoroutineScope()
    ObserveAsEvents(
        flow = SnackbarController.events,
        key1 = snackbarHostState
    ) { event ->
        // always triggers whenever I send a Snackbar
        coroutineScope.launch {
            // dismiss current snackbar
            snackbarHostState.currentSnackbarData?.dismiss()
            sheetSnackbarHostState.currentSnackbarData?.dismiss()
            // show new snackbar
            val snackbarHostToUse = if (event.showInSheetIfPossible && appStateViewModel.isSheetVisibile) {
                sheetSnackbarHostState
            }
            else {
                snackbarHostState
            }
            val snackbarResult = snackbarHostToUse.showSnackbar(
                visuals = event
            )
            if (snackbarResult == SnackbarResult.Dismissed) {
                event.onDismiss
            }
        }
    }

    // to blur the bottom bar
    val hazeState = remember { HazeState() }

    Box(
        contentAlignment = Alignment.BottomCenter
    ) {
        Scaffold(
            bottomBar = {
                Box(
                    modifier = Modifier
                        .hazeChild(hazeState, style = CupertinoMaterials.thin())
                ) {
                    MainBottomNavigationBar(
                        tabNavController = tabNavController
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState
                ) { snackbarData ->
                    val snackbarEvent = snackbarData.visuals as? SeesturmSnackbarEvent
                    if (snackbarEvent != null) {
                        SeesturmSnackbarView(
                            snackbarData = snackbarData,
                            event = snackbarEvent
                        )
                    }
                    else {
                        Snackbar(
                            snackbarData = snackbarData,
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .haze(hazeState)
            ) {
                content(innerPadding)
            }
        }
        appState.sheetContent?.let { sheetContent ->
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {
                    appStateViewModel.updateSheetContent(null)
                }
            ) {
                when (sheetContent) {
                    is BottomSheetContent.Custom -> {
                        sheetContent.content(
                            {
                                SnackbarHost(
                                    hostState = sheetSnackbarHostState
                                ) { snackbarData ->
                                    val snackbarEvent = snackbarData.visuals as? SeesturmSnackbarEvent
                                    if (snackbarEvent != null) {
                                        SeesturmSnackbarView(
                                            snackbarData = snackbarData,
                                            event = snackbarEvent
                                        )
                                    }
                                    else {
                                        Snackbar(
                                            snackbarData = snackbarData,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                    }
                                }
                            }
                        )
                    }
                    is BottomSheetContent.Scaffold -> {
                        Scaffold(
                            modifier = Modifier
                                .fillMaxHeight(sheetContent.sheetHeightPercentage),
                            topBar = {
                                if (sheetContent.title != null || sheetContent.actions != null) {
                                    TopAppBar(
                                        title = {
                                            if (sheetContent.title != null) {
                                                Text(
                                                    text = sheetContent.title,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        },
                                        colors = TopAppBarColors(
                                            containerColor = MaterialTheme.colorScheme.background,
                                            scrolledContainerColor = MaterialTheme.colorScheme.background,
                                            navigationIconContentColor = Color.SEESTURM_GREEN,
                                            titleContentColor = MaterialTheme.colorScheme.onBackground,
                                            actionIconContentColor = Color.SEESTURM_GREEN,
                                            subtitleContentColor = MaterialTheme.colorScheme.onBackground
                                        ),
                                        scrollBehavior = sheetScaffoldScrollBehavior,
                                        actions = {
                                            sheetContent.actions?.let { it() }
                                        }
                                    )
                                }
                            },
                            snackbarHost = {
                                SnackbarHost(
                                    hostState = sheetSnackbarHostState
                                ) { snackbarData ->
                                    val snackbarEvent = snackbarData.visuals as? SeesturmSnackbarEvent
                                    if (snackbarEvent != null) {
                                        SeesturmSnackbarView(
                                            snackbarData = snackbarData,
                                            event = snackbarEvent
                                        )
                                    }
                                    else {
                                        Snackbar(
                                            snackbarData = snackbarData,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                    }
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .nestedScroll(sheetScaffoldScrollBehavior.nestedScrollConnection)
                            ) {
                                sheetContent.content()
                            }
                        }
                    }
                }
            }
        }
    }
}

private typealias snackbarHost = @Composable () -> Unit

sealed class BottomSheetContent {
    data class Scaffold(
        val title: String?,
        val content: @Composable () -> Unit,
        val sheetHeightPercentage: Float = 0.95f,
        val actions: (@Composable RowScope.() -> Unit)? = null
    ): BottomSheetContent()
    data class Custom(
        val content: @Composable (snackbarHost) -> Unit
    ): BottomSheetContent()
}