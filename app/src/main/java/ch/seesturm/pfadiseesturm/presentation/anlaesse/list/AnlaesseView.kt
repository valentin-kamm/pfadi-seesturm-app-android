package ch.seesturm.pfadiseesturm.presentation.anlaesse.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.seesturm.pfadiseesturm.domain.wordpress.model.groupedByYearAndMonth
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.main.AuthViewModel
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.AnlassCardView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.AnlassLoadingCardView
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.TopBarNavigationIcon
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.buttons.CalendarSubscriptionButton
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeaderMode
import ch.seesturm.pfadiseesturm.presentation.common.lists.rememberStickyHeaderOffsets
import ch.seesturm.pfadiseesturm.presentation.common.lists.seesturmStickyHeader
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.InfiniteScrollUiState
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnlaesseView(
    viewModel: AnlaesseViewModel,
    authViewModel: AuthViewModel,
    appStateViewModel: AppStateViewModel,
    calendar: SeesturmCalendar,
    onAddEvent: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    bottomNavigationInnerPadding: PaddingValues,
    onNavigateToDetail: (SeesturmCalendar, String) -> Unit
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val appState by appStateViewModel.state.collectAsStateWithLifecycle()

    AnlaesseContentView(
        uiState = uiState,
        hasMoreEvents = viewModel.hasMoreEvents,
        calendar = calendar,
        onGetInitialEvents = { isPullToRefresh ->
            viewModel.getInitialEvents(isPullToRefresh)
        },
        onGetMoreEvents = {
            viewModel.getMoreEvents()
        },
        onNavigateBack = onNavigateBack,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        onNavigateToDetail = onNavigateToDetail,
        eventsLastUpdated = uiState.lastUpdated,
        canEditEvents = authState.isAdminSignedIn,
        onAddEvent = onAddEvent,
        isDarkTheme = appState.theme.isDarkTheme
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnlaesseContentView(
    uiState: AnlaesseListState,
    hasMoreEvents: Boolean,
    calendar: SeesturmCalendar,
    onGetInitialEvents: (Boolean) -> Unit,
    onGetMoreEvents: () -> Unit,
    bottomNavigationInnerPadding: PaddingValues,
    onNavigateToDetail: (SeesturmCalendar, String) -> Unit,
    onAddEvent: () -> Unit,
    eventsLastUpdated: String,
    canEditEvents: Boolean,
    isDarkTheme: Boolean,
    onNavigateBack: (() -> Unit)? = null,
    columnState: LazyListState = rememberLazyListState(),
    refreshState: PullToRefreshState = rememberPullToRefreshState()
) {

    TopBarScaffold(
        topBarStyle = TopBarStyle.Large,
        title = if (calendar.isLeitungsteam) "Termine Leitungsteam" else "Anlässe",
        navigationAction = if (onNavigateBack != null) {
            TopBarNavigationIcon.Back { onNavigateBack() }
        }
        else {
            TopBarNavigationIcon.None
        },
        actions = {
            CalendarSubscriptionButton(calendar)
            if (canEditEvents) {
                IconButton(
                    onClick = onAddEvent
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = if (calendar.isLeitungsteam) Color.SEESTURM_RED else Color.SEESTURM_GREEN
                    )
                }
            }
        }
    ) { topBarInnerPadding ->

        val combinedPadding = bottomNavigationInnerPadding.intersectWith(
            other = topBarInnerPadding,
            layoutDirection = LayoutDirection.Ltr,
            additionalBottomPadding = 16.dp
        )

        val stickyOffsets = rememberStickyHeaderOffsets(columnState, 0)

        LazyColumn(
            state = columnState,
            userScrollEnabled = !uiState.result.scrollingDisabled,
            contentPadding = combinedPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .pullToRefresh(
                    isRefreshing = uiState.refreshing,
                    state = refreshState,
                    onRefresh = {
                        onGetInitialEvents(true)
                    }
                )
        ) {
            when (val localState = uiState.result) {
                InfiniteScrollUiState.Loading -> {
                    stickyHeader {
                        BasicListHeader(
                            mode = BasicListHeaderMode.Loading
                        )
                    }
                    items(
                        count = 3,
                        key = { index ->
                            "Loading Cell 1.$index"
                        }
                    ) { _ ->
                        AnlassLoadingCardView(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        )
                    }
                    stickyHeader {
                        BasicListHeader(
                            mode = BasicListHeaderMode.Loading
                        )
                    }
                    items(
                        count = 5,
                        key = { index ->
                            "Loading Cell 2.$index"
                        }
                    ) {
                        AnlassLoadingCardView(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        )
                    }
                }
                is InfiniteScrollUiState.Error -> {
                    item(
                        key = "AnlässeErrorCell"
                    ) {
                        ErrorCardView(
                            errorTitle = "Ein Fehler ist aufgetreten",
                            errorDescription = localState.message,
                            modifier = Modifier
                                .padding(16.dp)
                                .animateItem()
                        ) {
                            onGetInitialEvents(false)
                        }
                    }
                }
                is InfiniteScrollUiState.Success -> {
                    if (localState.data.isNotEmpty()) {

                        localState.data.groupedByYearAndMonth.forEachIndexed { _, (month, events) ->

                            val headerTitle = DateTimeUtil.shared.formatDate(
                                date = month,
                                format = "MMMM yyyy",
                                type = DateFormattingType.Absolute
                            )

                            seesturmStickyHeader(
                                uniqueKey = headerTitle,
                                stickyOffsets = stickyOffsets
                            ) { _ ->
                                BasicListHeader(
                                    mode = BasicListHeaderMode.Normal(headerTitle),
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.background)
                                )
                            }

                            itemsIndexed(
                                events,
                                key = { _, event ->
                                    event.id
                                }
                            ) { _, item ->
                                AnlassCardView(
                                    event = item,
                                    calendar = calendar,
                                    onClick = {
                                        onNavigateToDetail(
                                            calendar,
                                            item.id
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .animateItem(),
                                    isDarkTheme = isDarkTheme
                                )
                            }
                        }
                        if (hasMoreEvents) {
                            when (val localSubState = localState.subState) {
                                InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Success, InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Loading -> {
                                    item(
                                        key = "LoadingMoreCell"
                                    ) {
                                        AnlassLoadingCardView(
                                            onAppear = {
                                                if (localSubState.infiniteScrollTaskShouldRun) {
                                                    onGetMoreEvents()
                                                }
                                            },
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .animateItem()
                                        )
                                    }
                                }
                                is InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Error -> {
                                    item(
                                        key = "LoadingMoreErrorCell"
                                    ) {
                                        ErrorCardView(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .animateItem(),
                                            errorTitle = "Ein Fehler ist aufgetreten",
                                            errorDescription = localSubState.message
                                        ) {
                                            onGetMoreEvents()
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Text(
                                text = "Stand Kalender: $eventsLastUpdated\n(Alle gezeigten Zeiten in MEZ/MESZ)",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 32.dp, bottom = 16.dp)
                                    .alpha(0.4f)
                                    .fillMaxWidth()
                            )
                        }
                    }
                    else {
                        item(
                            key = "KeineBevorstehendenAnlaesseCell"
                        ) {
                            Text(
                                "Keine bevorstehenden Anlässe",
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 60.dp, bottom = 75.dp)
                                    .padding(horizontal = 16.dp)
                                    .alpha(0.4f)
                                    .animateItem()
                            )
                        }
                    }
                }
            }
        }

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = combinedPadding.calculateTopPadding())
        ) {
            PullToRefreshDefaults.Indicator(
                state = refreshState,
                isRefreshing = uiState.refreshing,
                color = Color.SEESTURM_GREEN,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Loading")
@Composable
private fun AnlaesseViewPreview1() {
    PfadiSeesturmTheme {
        AnlaesseContentView(
            uiState = AnlaesseListState(
                result = InfiniteScrollUiState.Loading
            ),
            hasMoreEvents = true,
            calendar = SeesturmCalendar.TERMINE,
            onGetInitialEvents = {},
            onGetMoreEvents = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onNavigateToDetail = { _, _ ->},
            onNavigateBack = {},
            eventsLastUpdated = "",
            canEditEvents = true,
            onAddEvent = {},
            isDarkTheme = false
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Error")
@Composable
private fun AnlaesseViewPreview2() {
    PfadiSeesturmTheme {
        AnlaesseContentView(
            uiState = AnlaesseListState(
                result = InfiniteScrollUiState.Error("Schwerer Fehler")
            ),
            hasMoreEvents = true,
            calendar = SeesturmCalendar.TERMINE,
            onGetInitialEvents = {},
            onGetMoreEvents = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onNavigateToDetail = { _, _ ->},
            onNavigateBack = {},
            eventsLastUpdated = "",
            canEditEvents = false,
            onAddEvent = {},
            isDarkTheme = false
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Empty")
@Composable
private fun AnlaesseViewPreview3() {
    PfadiSeesturmTheme {
        AnlaesseContentView(
            uiState = AnlaesseListState(
                result = InfiniteScrollUiState.Success(emptyList(), InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Success)
            ),
            hasMoreEvents = true,
            calendar = SeesturmCalendar.TERMINE,
            onGetInitialEvents = {},
            onGetMoreEvents = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onNavigateToDetail = { _, _ ->},
            onNavigateBack = {},
            eventsLastUpdated = "",
            canEditEvents = true,
            onAddEvent = {},
            isDarkTheme = false
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Success with more posts")
@Composable
private fun AnlaesseViewPreview4() {
    PfadiSeesturmTheme {
        AnlaesseContentView(
            uiState = AnlaesseListState(
                result = InfiniteScrollUiState.Success(
                    data = listOf(DummyData.oneDayEvent, DummyData.allDayMultiDayEvent, DummyData.multiDayEvent),
                    subState = InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Loading
                )
            ),
            hasMoreEvents = true,
            calendar = SeesturmCalendar.TERMINE,
            onGetInitialEvents = {},
            onGetMoreEvents = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onNavigateToDetail = { _, _ ->},
            onNavigateBack = {},
            eventsLastUpdated = "",
            canEditEvents = false,
            onAddEvent = {},
            isDarkTheme = false
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Success with more posts error")
@Composable
private fun AnlaesseViewPreview5() {
    PfadiSeesturmTheme {
        AnlaesseContentView(
            uiState = AnlaesseListState(
                result = InfiniteScrollUiState.Success(
                    data = listOf(DummyData.oneDayEvent, DummyData.allDayMultiDayEvent, DummyData.multiDayEvent),
                    subState = InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Error("Schwerer Fehler")
                )
            ),
            hasMoreEvents = true,
            calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM,
            onGetInitialEvents = {},
            onGetMoreEvents = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onNavigateToDetail = { _, _ ->},
            onNavigateBack = {},
            eventsLastUpdated = "",
            canEditEvents = true,
            onAddEvent = {},
            isDarkTheme = false
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Success without more posts")
@Composable
private fun AnlaesseViewPreview6() {
    PfadiSeesturmTheme {
        AnlaesseContentView(
            uiState = AnlaesseListState(
                result = InfiniteScrollUiState.Success(
                    data = listOf(DummyData.oneDayEvent, DummyData.allDayMultiDayEvent, DummyData.multiDayEvent, DummyData.allDayOneDayEvent),
                    subState = InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Success
                )
            ),
            hasMoreEvents = false,
            calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM,
            onGetInitialEvents = {},
            onGetMoreEvents = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onNavigateToDetail = { _, _ ->},
            onNavigateBack = {},
            eventsLastUpdated = "",
            canEditEvents = false,
            onAddEvent = {},
            isDarkTheme = false
        )
    }
}