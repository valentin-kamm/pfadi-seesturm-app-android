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
import androidx.compose.material.icons.outlined.CalendarMonth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.seesturm.pfadiseesturm.domain.wordpress.model.groupedByYearAndMonth
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.AnlassCardView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.AnlassLoadingCardView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.CalendarSubscriptionAlert
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListHeaderMode
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicLoadingStickHeader
import ch.seesturm.pfadiseesturm.presentation.common.forms.rememberStickyHeaderOffsets
import ch.seesturm.pfadiseesturm.presentation.common.forms.seesturmStickyHeader
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.InfiniteScrollUiState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.subscribeToCalendar
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnlaesseView(
    viewModel: AnlaesseViewModel,
    calendar: SeesturmCalendar,
    onNavigateBack: (() -> Unit)? = null,
    bottomNavigationInnerPadding: PaddingValues,
    onNavigateToDetail: (SeesturmCalendar, String) -> Unit
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    CalendarSubscriptionAlert (
        isShown = uiState.showCalendarSubscriptionAlert,
        title = "Kalender kann nicht abonniert werden",
        calendar = calendar,
        onDismiss = {
            viewModel.updateAlertVisibility(false)
        }
    )

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
        onSubscribeToCalendar = {
            val result = subscribeToCalendar(
                subscriptionUrl = calendar.subscriptionUrl,
                context = context
            )
            if (result is SeesturmResult.Error) {
                viewModel.updateAlertVisibility(true)
            }
        }
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
    onSubscribeToCalendar: () -> Unit,
    bottomNavigationInnerPadding: PaddingValues,
    onNavigateToDetail: (SeesturmCalendar, String) -> Unit,
    eventsLastUpdated: String,
    onNavigateBack: (() -> Unit)? = null,
    columnState: LazyListState = rememberLazyListState(),
    refreshState: PullToRefreshState = rememberPullToRefreshState()
) {

    TopBarScaffold(
        topBarStyle = TopBarStyle.Large,
        title = if (calendar.isLeitungsteam) "Termine Leitungsteam" else "Anlässe",
        onNavigateBack = onNavigateBack,
        actions = {
            IconButton(
                onClick = onSubscribeToCalendar
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = if (calendar.isLeitungsteam) Color.SEESTURM_RED else Color.SEESTURM_GREEN
                )
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
                        BasicLoadingStickHeader()
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
                        BasicLoadingStickHeader()
                    }
                    items(
                        count = 5,
                        key = { index ->
                            "Loading Cell 2.$index"
                        }
                    ) { index ->
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
                                        .animateItem()
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
                color = Color.SEESTURM_GREEN
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
            onSubscribeToCalendar = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onNavigateToDetail = { _, _ ->},
            onNavigateBack = {},
            eventsLastUpdated = ""
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
            onSubscribeToCalendar = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onNavigateToDetail = { _, _ ->},
            onNavigateBack = {},
            eventsLastUpdated = ""
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
            onSubscribeToCalendar = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onNavigateToDetail = { _, _ ->},
            onNavigateBack = {},
            eventsLastUpdated = ""
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
            onSubscribeToCalendar = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onNavigateToDetail = { _, _ ->},
            onNavigateBack = {},
            eventsLastUpdated = ""
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
            onSubscribeToCalendar = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onNavigateToDetail = { _, _ ->},
            onNavigateBack = {},
            eventsLastUpdated = ""
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
            onSubscribeToCalendar = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onNavigateToDetail = { _, _ ->},
            onNavigateBack = {},
            eventsLastUpdated = ""
        )
    }
}