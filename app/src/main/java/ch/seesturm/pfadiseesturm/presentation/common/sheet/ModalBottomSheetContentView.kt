package ch.seesturm.pfadiseesturm.presentation.common.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import com.composables.core.BottomSheetScope
import com.composables.core.ModalBottomSheet
import com.composables.core.ModalBottomSheetState
import com.composables.core.ModalSheetProperties
import com.composables.core.Scrim
import com.composables.core.Sheet
import com.composables.core.rememberModalBottomSheetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetContentView(
    sheetState: ModalBottomSheetState,
    detents: SheetDetents,
    type: SheetScaffoldType,
    properties: ModalSheetProperties,
    onDismiss: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    snackbarHost: @Composable () -> Unit,
    content: @Composable BottomSheetScope.() -> Unit
) {

    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        ModalBottomSheet(
            state = sheetState,
            properties = properties,
            onDismiss = onDismiss
        ) {
            Scrim(scrimColor = BottomSheetDefaults.ScrimColor)
            Box(
                modifier = Modifier
                    .padding(
                        WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)
                            .asPaddingValues()
                    )
            ) {
                Sheet(
                    modifier = modifier
                        .fillMaxWidth(),
                    enabled = enabled,
                    shape = BottomSheetDefaults.ExpandedShape,
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    imeAware = true
                ) {
                    when (type) {
                        SheetScaffoldType.Blank -> {
                            Column(
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(detents.largestDetentMultiplier)
                            ) {
                                BottomSheetDefaults.DragHandle()
                                Scaffold(
                                    snackbarHost = snackbarHost,
                                    containerColor = Color.Transparent
                                ) { innerPadding ->
                                    Box(
                                        modifier = Modifier
                                            .padding(
                                                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                                                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                                                bottom = innerPadding.calculateBottomPadding(),
                                                top = 0.dp
                                            )
                                            .fillMaxSize()
                                    ) {
                                        content()
                                    }
                                }
                            }
                        }
                        is SheetScaffoldType.Title, is SheetScaffoldType.TitleAndAction -> {
                            Box(
                                contentAlignment = Alignment.TopCenter,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(detents.largestDetentMultiplier)
                            ) {
                                Scaffold(
                                    topBar = {
                                        TopAppBar(
                                            title = {
                                                Text(type.topBarTitle)
                                            },
                                            colors = TopAppBarColors(
                                                containerColor = MaterialTheme.colorScheme.surface,
                                                scrolledContainerColor = MaterialTheme.colorScheme.surface,
                                                navigationIconContentColor = Color.SEESTURM_GREEN,
                                                titleContentColor = MaterialTheme.colorScheme.onBackground,
                                                actionIconContentColor = Color.SEESTURM_GREEN,
                                                subtitleContentColor = MaterialTheme.colorScheme.onBackground
                                            ),
                                            actions = type.topBarActions,
                                            windowInsets = TopAppBarDefaults.windowInsets
                                                .only(WindowInsetsSides.Horizontal)
                                                .add(WindowInsets(top = 16.dp))
                                        )
                                    },
                                    snackbarHost = snackbarHost,
                                    containerColor = Color.Transparent
                                ) { innerPadding ->
                                    Box(
                                        modifier = Modifier
                                            .padding(innerPadding)
                                            .fillMaxSize()
                                    ) {
                                        content()
                                    }
                                }
                                BottomSheetDefaults.DragHandle()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SimpleModalBottomSheetPreview1() {
    PfadiSeesturmTheme {
        ModalBottomSheetContentView(
            sheetState = rememberModalBottomSheetState(
                initialDetent = ModalBottomSheetDetent.Medium.sheetDetent,
                detents = SheetDetents.All.sheetDetents
            ),
            detents = SheetDetents.All,
            type = SheetScaffoldType.Blank,
            properties = ModalSheetProperties(),
            onDismiss = {},
            modifier = Modifier,
            enabled = true,
            snackbarHost = {},
            content = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    repeat(25) {
                        item {
                            Text(
                                "Item #${(it + 1)}",
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}

@Preview
@Composable
private fun SimpleModalBottomSheetPreview2() {
    PfadiSeesturmTheme {
        ModalBottomSheetContentView(
            sheetState = rememberModalBottomSheetState(
                initialDetent = ModalBottomSheetDetent.Medium.sheetDetent,
                detents = SheetDetents.All.sheetDetents
            ),
            detents = SheetDetents.All,
            type = SheetScaffoldType.Title("Account"),
            properties = ModalSheetProperties(),
            onDismiss = {},
            modifier = Modifier,
            enabled = true,
            snackbarHost = {},
            content = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    repeat(25) {
                        item {
                            Text(
                                "Item #${(it + 1)}",
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}

@Preview
@Composable
private fun SimpleModalBottomSheetPreview3() {
    PfadiSeesturmTheme {
        ModalBottomSheetContentView(
            sheetState = rememberModalBottomSheetState(
                initialDetent = ModalBottomSheetDetent.Large.sheetDetent,
                detents = SheetDetents.LargeOnly.sheetDetents
            ),
            detents = SheetDetents.LargeOnly,
            type = SheetScaffoldType.TitleAndAction(
                title = "Account",
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Delete, null)
                    }
                }
            ),
            properties = ModalSheetProperties(),
            onDismiss = {},
            modifier = Modifier,
            enabled = true,
            snackbarHost = {},
            content = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    repeat(25) {
                        item {
                            Text(
                                "Item #${(it + 1)}",
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}