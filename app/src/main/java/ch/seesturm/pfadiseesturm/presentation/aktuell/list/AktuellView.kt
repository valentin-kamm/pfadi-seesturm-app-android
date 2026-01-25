package ch.seesturm.pfadiseesturm.presentation.aktuell.list


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.wordpress.model.groupedByYear
import ch.seesturm.pfadiseesturm.presentation.aktuell.list.components.AktuellCardView
import ch.seesturm.pfadiseesturm.presentation.aktuell.list.components.AktuellLoadingCardView
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeaderMode
import ch.seesturm.pfadiseesturm.presentation.common.lists.rememberStickyHeaderOffsets
import ch.seesturm.pfadiseesturm.presentation.common.lists.seesturmStickyHeader
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.InfiniteScrollUiState
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AktuellView(
    viewModel: AktuellViewModel,
    aktuellNavController: NavController,
    bottomNavigationInnerPadding: PaddingValues
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    AktuellContentView(
        uiState = uiState,
        aktuellNavController = aktuellNavController,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        onGetInitialPosts = { isPullToRefresh ->
            viewModel.getInitialPosts(isPullToRefresh)
        },
        hasMorePosts = viewModel.hasMorePosts,
        onGetMorePosts = {
            viewModel.getMorePosts()
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AktuellContentView(
    uiState: AktuellListState,
    aktuellNavController: NavController,
    bottomNavigationInnerPadding: PaddingValues,
    onGetInitialPosts: (Boolean) -> Unit,
    hasMorePosts: Boolean,
    onGetMorePosts: () -> Unit,
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


        val combinedPadding = bottomNavigationInnerPadding.intersectWith(
            other = topBarInnerPadding,
            layoutDirection = LayoutDirection.Ltr,
            additionalBottomPadding = 16.dp
        )

        // Calculate sticky offsets for all sticky headers
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
                        onGetInitialPosts(true)
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
                        count = 5,
                        key = { index ->
                            "Loading Cell $index"
                        }
                    ) { _ ->
                        AktuellLoadingCardView(
                            modifier = Modifier
                                .animateItem()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
                is InfiniteScrollUiState.Error -> {
                    item(
                        key = "ErrorCell"
                    ) {
                        ErrorCardView(
                            modifier = Modifier
                                .animateItem()
                                .padding(16.dp),
                            errorTitle = "Ein Fehler ist aufgetreten",
                            errorDescription = localState.message
                        ) {
                            onGetInitialPosts(false)
                        }
                    }
                }
                is InfiniteScrollUiState.Success -> {
                    localState.data.groupedByYear.forEachIndexed { _, (year, posts) ->
                        val headerTitle = "Pfadijahr $year"
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
                            posts,
                            key = { _, post ->
                                post.id
                            }
                        ) { _, item ->
                            AktuellCardView(
                                post = item,
                                onClick = {
                                    aktuellNavController.navigate(
                                        AppDestination.MainTabView.Destinations.Aktuell.Destinations.AktuellDetail(postId = item.id)
                                    )
                                },
                                modifier = Modifier
                                    .animateItem()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                    if (hasMorePosts) {
                        when (val localSubState = localState.subState) {
                            InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Success, InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Loading -> {
                                item(
                                    key = "LoadingMoreCell"
                                ) {
                                    AktuellLoadingCardView(
                                        onAppear = {
                                            if (localSubState.infiniteScrollTaskShouldRun) {
                                                onGetMorePosts()
                                            }
                                        },
                                        modifier = Modifier
                                            .padding(0.dp)
                                            .animateItem()
                                            .padding(horizontal = 16.dp)
                                    )
                                }
                            }
                            is InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Error -> {
                                item(
                                    key = "LoadingMoreErrorCell"
                                ) {
                                    ErrorCardView(
                                        modifier = Modifier
                                            .padding(
                                                top = 0.dp
                                            )
                                            .padding(horizontal = 16.dp)
                                            .animateItem(),
                                        errorTitle = "Ein Fehler ist aufgetreten",
                                        errorDescription = localSubState.message
                                    ) {
                                        onGetMorePosts()
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
                color = Color.SEESTURM_GREEN,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Loading")
@Composable
private fun AktuellViewPreview1() {
    PfadiSeesturmTheme {
        AktuellContentView(
            uiState = AktuellListState(
                result = InfiniteScrollUiState.Loading
            ),
            aktuellNavController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onGetInitialPosts = {},
            hasMorePosts = true,
            onGetMorePosts = {}
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Error")
@Composable
private fun AktuellViewPreview2() {
    PfadiSeesturmTheme {
        AktuellContentView(
            uiState = AktuellListState(
                result = InfiniteScrollUiState.Error("Schwerer Fehler")
            ),
            aktuellNavController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onGetInitialPosts = {},
            hasMorePosts = true,
            onGetMorePosts = {}
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Success with more posts")
@Composable
private fun AktuellViewPreview3() {
    PfadiSeesturmTheme {
        AktuellContentView(
            uiState = AktuellListState(
                result = InfiniteScrollUiState.Success(
                    data = listOf(DummyData.aktuellPost1, DummyData.aktuellPost2),
                    subState = InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Loading
                )
            ),
            aktuellNavController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onGetInitialPosts = {},
            hasMorePosts = true,
            onGetMorePosts = {}
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Success with more posts error")
@Composable
private fun AktuellViewPreview4() {
    PfadiSeesturmTheme {
        AktuellContentView(
            uiState = AktuellListState(
                result = InfiniteScrollUiState.Success(
                    data = listOf(DummyData.aktuellPost1, DummyData.aktuellPost2),
                    subState = InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Error("Schwerer Fehler")
                )
            ),
            aktuellNavController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onGetInitialPosts = {},
            hasMorePosts = true,
            onGetMorePosts = {}
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Success without more posts")
@Composable
private fun AktuellViewPreview5() {
    PfadiSeesturmTheme {
        AktuellContentView(
            uiState = AktuellListState(
                result = InfiniteScrollUiState.Success(
                    data = listOf(DummyData.aktuellPost1, DummyData.aktuellPost2),
                    subState = InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Success
                )
            ),
            aktuellNavController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onGetInitialPosts = {},
            hasMorePosts = true,
            onGetMorePosts = {}
        )
    }
}