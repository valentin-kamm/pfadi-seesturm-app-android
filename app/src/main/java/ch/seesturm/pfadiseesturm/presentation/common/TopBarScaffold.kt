package ch.seesturm.pfadiseesturm.presentation.common

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarView
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun TopBarScaffold(
    topBarStyle: TopBarStyle,
    modifier: Modifier = Modifier,
    hideTopBar: Boolean = false,
    title: String? = null,
    navigationAction: TopBarNavigationIcon = TopBarNavigationIcon.None,
    actions: @Composable RowScope.() -> Unit = {},
    staticSnackbar: TopBarScaffoldStaticSnackbarType = TopBarScaffoldStaticSnackbarType.None,
    floatingActionButton: @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
    content: @Composable (PaddingValues) -> Unit,
) {

    val hazeState = remember { HazeState() }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    if (staticSnackbar is TopBarScaffoldStaticSnackbarType.Show) {
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                // dismiss current snackbar
                snackbarHostState.currentSnackbarData?.dismiss()
                // show new snackbar
                val snackbarResult = snackbarHostState.showSnackbar(
                    visuals = staticSnackbar.snackbarEvent
                )
                if (snackbarResult == SnackbarResult.Dismissed) {
                    staticSnackbar.snackbarEvent.onDismiss
                }
            }
        }
    }

    Scaffold(
        topBar = {
            if (!hideTopBar) {
                when (topBarStyle) {
                    TopBarStyle.Large -> {
                        LargeTopAppBar(
                            title = {
                                if (title != null) {
                                    Text(
                                        text = title,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = LocalTextStyle.current.copy(hyphens = Hyphens.Auto)
                                    )
                                }
                            },
                            navigationIcon = {
                                when (navigationAction) {
                                    is TopBarNavigationIcon.Back -> {
                                        IconButton(
                                            onClick = navigationAction.onNavigateBack
                                        ) {
                                            Icon(
                                                Icons.AutoMirrored.Outlined.ArrowBack,
                                                contentDescription = "Zurück",
                                                tint = Color.SEESTURM_GREEN
                                            )
                                        }
                                    }
                                    is TopBarNavigationIcon.Close -> {
                                        IconButton(
                                            onClick = navigationAction.onClose
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Schliessen",
                                                tint = Color.SEESTURM_GREEN
                                            )
                                        }
                                    }
                                    TopBarNavigationIcon.None -> {}
                                }
                            },
                            colors = TopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                scrolledContainerColor = if (Build.VERSION.SDK_INT >= 30) {
                                    Color.Transparent
                                }
                                else {
                                    MaterialTheme.colorScheme.primaryContainer
                                },
                                navigationIconContentColor = Color.SEESTURM_GREEN,
                                titleContentColor = MaterialTheme.colorScheme.onBackground,
                                actionIconContentColor = Color.SEESTURM_GREEN,
                                subtitleContentColor = MaterialTheme.colorScheme.onBackground
                            ),
                            scrollBehavior = scrollBehavior,
                            actions = actions,
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
                        )
                    }
                    TopBarStyle.Small -> {
                        TopAppBar(
                            title = {
                                if (title != null) {
                                    Text(
                                        text = title,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = LocalTextStyle.current.copy(hyphens = Hyphens.Auto)
                                    )
                                }
                            },
                            navigationIcon = {
                                when (navigationAction) {
                                    is TopBarNavigationIcon.Back -> {
                                        IconButton(
                                            onClick = navigationAction.onNavigateBack,
                                        ) {
                                            Icon(
                                                Icons.AutoMirrored.Outlined.ArrowBack,
                                                contentDescription = "Zurück",
                                                tint = Color.SEESTURM_GREEN
                                            )
                                        }
                                    }
                                    is TopBarNavigationIcon.Close -> {
                                        IconButton(
                                            onClick = navigationAction.onClose,
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Schliessen",
                                                tint = Color.SEESTURM_GREEN
                                            )
                                        }
                                    }
                                    TopBarNavigationIcon.None -> {}
                                }
                            },
                            colors = TopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                scrolledContainerColor = if (Build.VERSION.SDK_INT >= 30) {
                                    Color.Transparent
                                }
                                else {
                                    MaterialTheme.colorScheme.primaryContainer
                                },
                                navigationIconContentColor = Color.SEESTURM_GREEN,
                                titleContentColor = MaterialTheme.colorScheme.onBackground,
                                actionIconContentColor = Color.SEESTURM_GREEN,
                                subtitleContentColor = MaterialTheme.colorScheme.onBackground
                            ),
                            scrollBehavior = scrollBehavior,
                            actions = actions,
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
                        )
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(
                        bottom = if (staticSnackbar is TopBarScaffoldStaticSnackbarType.Show) {
                            staticSnackbar.additionalBottomPadding
                        }
                        else {
                            0.dp
                        }
                    )
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
        },
        floatingActionButton = floatingActionButton,
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = if (Build.VERSION.SDK_INT < 30) {
            WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
        }
        else {
            ScaffoldDefaults.contentWindowInsets
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
}

sealed class TopBarNavigationIcon {
    data object None: TopBarNavigationIcon()
    data class Back(
        val onNavigateBack: () -> Unit
    ): TopBarNavigationIcon()
    data class Close(
        val onClose: () -> Unit
    ): TopBarNavigationIcon()
}