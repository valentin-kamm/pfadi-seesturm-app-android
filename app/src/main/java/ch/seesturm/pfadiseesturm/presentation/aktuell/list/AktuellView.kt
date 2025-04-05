package ch.seesturm.pfadiseesturm.presentation.aktuell.list


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.state.SeesturmInfiniteScrollUiState
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.repository.AktuellRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.wordpress.model.groupedByYear
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AktuellService
import ch.seesturm.pfadiseesturm.presentation.aktuell.list.components.AktuellCardView
import ch.seesturm.pfadiseesturm.presentation.aktuell.list.components.AktuellLoadingCell
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicLoadingStickHeader
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.forms.myStickyHeader
import ch.seesturm.pfadiseesturm.presentation.common.forms.rememberStickyHeaderOffsets
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.common.viewModelFactoryHelper
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.navigation.AppDestination
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AktuellView(
    bottomNavigationInnerPadding: PaddingValues,
    aktuellNavController: NavController,
    viewModel: AktuellViewModel,
    columnState: LazyListState = rememberLazyListState(),
    refreshState: PullToRefreshState = rememberPullToRefreshState()
) {

    TopBarScaffold(
        topBarStyle = TopBarStyle.Large,
        title = "Aktuell",
        actions = {
            IconButton(
                onClick = {
                    aktuellNavController.navigate(
                        AppDestination.MainTabView.Destinations.Aktuell.Destinations.PushNotifications
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null
                )
            }
        }
    ) { topBarInnerPadding ->

        val uiState by viewModel.state.collectAsStateWithLifecycle()
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
                        viewModel.getInitialPosts(true)
                    }
                )
        ) {
            when (val localState = uiState.result) {
                SeesturmInfiniteScrollUiState.Loading -> {
                    stickyHeader {
                        BasicLoadingStickHeader()
                    }
                    items(
                        count = 5,
                        key = { index ->
                            "Loading Cell $index"
                        }
                    ) { index ->
                        AktuellLoadingCell(
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
                        key = "ErrorCell"
                    ) {
                        CardErrorView(
                            modifier = Modifier
                                .padding(16.dp)
                                .animateItem(),
                            errorTitle = "Ein Fehler ist aufgetreten",
                            errorDescription = localState.message
                        ) {
                            viewModel.getInitialPosts(false)
                        }
                    }
                }
                is SeesturmInfiniteScrollUiState.Success -> {
                    localState.data.groupedByYear.forEachIndexed { _, (year, posts) ->
                        val headerTitle = "Pfadijahr $year"
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
                            posts,
                            key = { _, post ->
                                post.id
                            }
                        ) { index, item ->
                            AktuellCardView(
                                post = item,
                                onClick = {
                                    aktuellNavController.navigate(
                                        AppDestination.MainTabView.Destinations.Aktuell.Destinations.AktuellDetail(postId = item.id)
                                    )
                                },
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
                    if (viewModel.hasMorePosts) {
                        when (val localSubState = localState.subState) {
                            SeesturmInfiniteScrollUiState.Success.SeesturmInfiniteScrollUiSubState.Success, SeesturmInfiniteScrollUiState.Success.SeesturmInfiniteScrollUiSubState.Loading -> {
                                item(
                                    key = "LoadingMoreCell"
                                ) {
                                    AktuellLoadingCell(
                                        onAppear = {
                                            if (localSubState.infiniteScrollTaskShouldRun) {
                                                viewModel.getMorePosts()
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
                                        viewModel.getMorePosts()
                                    }
                                }
                            }
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
@Preview(showBackground = true)
@Composable
fun AktuellViewPreview() {
    AktuellView(
        bottomNavigationInnerPadding = PaddingValues(0.dp),
        aktuellNavController = rememberNavController(),
        viewModel = viewModel<AktuellViewModel>(
            factory = viewModelFactoryHelper {
                AktuellViewModel(
                    service = AktuellService(
                        AktuellRepositoryImpl(
                            Retrofit.Builder()
                                .baseUrl(Constants.SEESTURM_API_BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                                .create(WordpressApi::class.java)
                        )
                    )
                )
            }
        )
    )
}
