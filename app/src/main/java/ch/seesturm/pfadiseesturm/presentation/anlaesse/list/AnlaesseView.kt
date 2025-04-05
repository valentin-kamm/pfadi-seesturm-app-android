package ch.seesturm.pfadiseesturm.presentation.anlaesse.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.seesturm.pfadiseesturm.util.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.state.SeesturmInfiniteScrollUiState
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.repository.AnlaesseRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.wordpress.model.groupedByYearAndMonth
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AnlaesseService
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.AnlassCardView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.AnlassLoadingCardView
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicLoadingStickHeader
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.forms.myStickyHeader
import ch.seesturm.pfadiseesturm.presentation.common.forms.rememberStickyHeaderOffsets
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.common.viewModelFactoryHelper
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.CalendarSubscriptionAlert
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.subscribeToCalendar
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AnlaesseView(
    bottomNavigationInnerPadding: PaddingValues,
    onNavigateToDetail: (SeesturmCalendar, String) -> Unit,
    viewModel: AnlaesseViewModel,
    calendar: SeesturmCalendar,
    columnState: LazyListState = rememberLazyListState(),
    refreshState: PullToRefreshState = rememberPullToRefreshState()
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

    TopBarScaffold(
        topBarStyle = TopBarStyle.Large,
        title = if (calendar.isLeitungsteam) "Termine Leitungsteam" else "Anlässe",
        actions = {
            IconButton(
                onClick = {
                    when (
                        subscribeToCalendar(
                            subscriptionUrl = calendar.subscriptionUrl,
                            context = context
                        )
                    ) {
                        is SeesturmResult.Error -> {
                            viewModel.updateAlertVisibility(true)
                        }
                        is SeesturmResult.Success -> {
                            // do nothing
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = if (calendar.isLeitungsteam) Color.SEESTURM_RED else Color.SEESTURM_GREEN
                )
            }
        }
    ) { topBarInnerPadding ->

        val combinedPadding = bottomNavigationInnerPadding.intersectWith(topBarInnerPadding, LayoutDirection.Ltr)

        // Calculate sticky offsets for all sticky headers
        val stickyOffsets = rememberStickyHeaderOffsets(columnState, 0)

        LazyColumn(
            state = columnState,
            userScrollEnabled = !uiState.result.scrollingDisabled,
            contentPadding = combinedPadding,
            modifier = Modifier
                .fillMaxSize()
                .pullToRefresh(
                    isRefreshing = uiState.refreshing,
                    state = refreshState,
                    onRefresh = {
                        viewModel.getInitialEvents(true)
                    }
                )
        ) {
            when (val localState = uiState.result) {
                SeesturmInfiniteScrollUiState.Loading -> {
                    stickyHeader {
                        BasicLoadingStickHeader()
                    }
                    items(
                        count = 3,
                        key = { index ->
                            "Loading Cell 1.$index"
                        }
                    ) { index ->
                        AnlassLoadingCardView(
                            modifier = Modifier
                                .padding(
                                    0.dp,
                                    if (index == 0) 16.dp else {
                                        0.dp
                                    },
                                    0.dp,
                                    0.dp
                                )
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
                                .padding(
                                    0.dp,
                                    if (index == 0) 16.dp else {
                                        0.dp
                                    },
                                    0.dp,
                                    0.dp
                                )
                                .animateItem()
                        )
                    }
                }
                is SeesturmInfiniteScrollUiState.Error -> {
                    item(
                        key = "AnlässeErrorCell"
                    ) {
                        CardErrorView(
                            errorTitle = "Ein Fehler ist aufgetreten",
                            errorDescription = localState.message,
                            modifier = Modifier
                                .padding(16.dp)
                                .animateItem()
                        ) {
                            viewModel.getInitialEvents(false)
                        }
                    }
                }
                is SeesturmInfiniteScrollUiState.Success -> {
                    if (localState.data.isNotEmpty()) {
                        localState.data.groupedByYearAndMonth.forEachIndexed { _, (month, events) ->
                            val headerTitle = DateTimeUtil.shared.formatDate(
                                date = month,
                                format = "MMMM yyyy",
                                withRelativeDateFormatting = false
                            )

                            myStickyHeader(
                                uniqueKey = headerTitle,
                                stickyOffsets = stickyOffsets
                            ) { _ ->
                                BasicListHeader(
                                    title = headerTitle,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.background)
                                )
                            }

                            itemsIndexed(
                                events,
                                key = { _, event ->
                                    event.id
                                }
                            ) { index, item ->
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
                                        .padding(top = if (index == 0) 16.dp else 0.dp)
                                        .animateItem()
                                )
                            }
                        }
                        if (viewModel.hasMoreEvents) {
                            when (val localSubState = localState.subState) {
                                SeesturmInfiniteScrollUiState.Success.SeesturmInfiniteScrollUiSubState.Success, SeesturmInfiniteScrollUiState.Success.SeesturmInfiniteScrollUiSubState.Loading -> {
                                    item(
                                        key = "LoadingMoreCell"
                                    ) {
                                        AnlassLoadingCardView(
                                            onAppear = {
                                                if (localSubState.infiniteScrollTaskShouldRun) {
                                                    viewModel.getMoreEvents()
                                                }
                                            },
                                            modifier = Modifier
                                                .padding(0.dp)
                                                .animateItem()
                                        )
                                    }
                                }
                                is SeesturmInfiniteScrollUiState.Success.SeesturmInfiniteScrollUiSubState.Error -> {
                                    item(
                                        key = "LoadingMoreErrorCell"
                                    ) {
                                        CardErrorView(
                                            modifier = Modifier
                                                .padding(
                                                    top = 0.dp,
                                                    bottom = 16.dp
                                                )
                                                .padding(horizontal = 16.dp)
                                                .animateItem(),
                                            errorTitle = "Ein Fehler ist aufgetreten",
                                            errorDescription = localSubState.message
                                        ) {
                                            viewModel.getMoreEvents()
                                        }
                                    }
                                }
                            }
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
                                    .padding(vertical = 75.dp)
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
@Preview
@Composable
fun AnlaesseViewPreview() {
    AnlaesseView(
        bottomNavigationInnerPadding = PaddingValues(0.dp),
        onNavigateToDetail = { _, _ -> },
        calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM,
        viewModel = viewModel<AnlaesseViewModel>(
            factory = viewModelFactoryHelper {
                AnlaesseViewModel(
                    service = AnlaesseService(
                        AnlaesseRepositoryImpl(
                            Retrofit.Builder()
                                .baseUrl(Constants.SEESTURM_API_BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                                .create(WordpressApi::class.java)
                        )
                    ),
                    calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM
                )
            }
        )
    )
}