package ch.seesturm.pfadiseesturm.presentation.common

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarView
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.ObserveAsEvents
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
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

    val coroutineScope = rememberCoroutineScope()

    val hazeState = remember { HazeState() }

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val sheetSnackbarHostState = remember {
        SnackbarHostState()
    }

    // show snackbars
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

    Box(
        contentAlignment = Alignment.BottomCenter
    ) {
        Scaffold(
            bottomBar = {
                Box(
                    modifier = Modifier
                        .then(
                            if (Build.VERSION.SDK_INT >= 30) {
                                Modifier
                                    .hazeEffect(hazeState, style = CupertinoMaterials.thin())
                            }
                            else {
                                Modifier
                            }
                        )
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
                    .then(
                        if (Build.VERSION.SDK_INT >= 30) {
                            Modifier
                                .hazeSource(hazeState)
                        }
                        else {
                            Modifier
                        }
                    )
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
                        sheetContent.content {
                            SnackbarHost(
                                hostState = sheetSnackbarHostState
                            ) { snackbarData ->
                                val snackbarEvent = snackbarData.visuals as? SeesturmSnackbarEvent
                                if (snackbarEvent != null) {
                                    SeesturmSnackbarView(
                                        snackbarData = snackbarData,
                                        event = snackbarEvent
                                    )
                                } else {
                                    Snackbar(
                                        snackbarData = snackbarData,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                }
                            }
                        }
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