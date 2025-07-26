package ch.seesturm.pfadiseesturm.presentation.aktuell.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPost
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIcon
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIconType
import ch.seesturm.pfadiseesturm.presentation.common.TopBarNavigationIcon
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.HtmlTextView
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.isValidUrl
import ch.seesturm.pfadiseesturm.util.state.UiState
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest


@Composable
fun AktuellDetailView(
    viewModel: AktuellDetailViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController,
    onPushNotificationsNavigate: () -> Unit
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    AktuellDetailContentView(
        postState = uiState,
        navController = navController,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        onPushNotificationsNavigate = onPushNotificationsNavigate,
        onRetry = {
            viewModel.getPost()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AktuellDetailContentView(
    postState: UiState<WordpressPost>,
    navController: NavController,
    bottomNavigationInnerPadding: PaddingValues,
    onPushNotificationsNavigate: () -> Unit,
    onRetry: () -> Unit,
    columnState: LazyListState = rememberLazyListState()
) {

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        navigationAction = TopBarNavigationIcon.Back { navController.navigateUp() },
        actions = {
            IconButton(
                onClick = {
                    onPushNotificationsNavigate()
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null
                )
            }
        }
    ) { topBarInnerPadding ->

        val combinedPadding = bottomNavigationInnerPadding.intersectWith(topBarInnerPadding, LayoutDirection.Ltr)

        LazyColumn(
            state = columnState,
            userScrollEnabled = !postState.scrollingDisabled,
            contentPadding = combinedPadding,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            when (postState) {
                UiState.Loading -> {
                    item(
                        key = "AktuellDetailLoadingView"
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .animateItem()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer()
                                    .aspectRatio(4 / 3f)
                                    .customLoadingBlinking()
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            RedactedText(
                                2,
                                MaterialTheme.typography.headlineMedium,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            )
                            RedactedText(
                                30,
                                MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 16.dp)
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "AktuellDetailErrorView"
                    ) {
                        ErrorCardView(
                            modifier = Modifier
                                .padding(16.dp)
                                .animateItem(),
                            errorTitle = "Ein Fehler ist aufgetreten",
                            errorDescription = postState.message
                        ) {
                            onRetry()
                        }
                    }
                }
                is UiState.Success -> {
                    item(
                        key = "AktuellDetailSuccessView"
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .animateItem()
                        ) {
                            if (postState.data.imageUrl.isValidUrl) {
                                SubcomposeAsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(postState.data.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(postState.data.imageAspectRatio.toFloat())
                                ) {
                                    when (painter.state) {
                                        is AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Loading -> {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(postState.data.imageAspectRatio.toFloat())
                                                    .graphicsLayer()
                                                    .customLoadingBlinking()
                                                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
                                            )
                                        }
                                        is AsyncImagePainter.State.Error -> {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(postState.data.imageAspectRatio.toFloat())
                                                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.HideImage,
                                                    contentDescription = null,
                                                    tint = Color.SEESTURM_GREEN,
                                                    modifier = Modifier
                                                        .size(66.dp)
                                                )
                                            }
                                        }
                                        is AsyncImagePainter.State.Success -> {
                                            SubcomposeAsyncImageContent()
                                        }
                                    }
                                }
                            }
                            Text(
                                postState.data.titleDecoded,
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, hyphens = Hyphens.Auto),
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(
                                        top = if (postState.data.imageUrl.isValidUrl) {
                                            0.dp
                                        } else {
                                            16.dp
                                        }
                                    )
                            )
                            TextWithIcon(
                                type = TextWithIconType.Text(
                                    text = postState.data.publishedFormatted.uppercase(),
                                    textStyle = { MaterialTheme.typography.labelMedium }
                                ),
                                imageVector = Icons.Outlined.CalendarMonth,
                                textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                iconTint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                maxLines = 1,
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                            HtmlTextView(
                                html = postState.data.content,
                                textColor = MaterialTheme.colorScheme.onBackground,
                                textStyle = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview("Loading")
@Composable
private fun AktuellDetailViewPreview1() {
    PfadiSeesturmTheme {
        AktuellDetailContentView(
            postState = UiState.Loading,
            navController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onPushNotificationsNavigate = {},
            onRetry = {}
        )
    }
}
@Preview("Error")
@Composable
private fun AktuellDetailViewPreview2() {
    PfadiSeesturmTheme {
        AktuellDetailContentView(
            postState = UiState.Error("Schwerer Fehler"),
            navController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onPushNotificationsNavigate = {},
            onRetry = {}
        )
    }
}
@Preview("Success")
@Composable
private fun AktuellDetailViewPreview3() {
    PfadiSeesturmTheme {
        AktuellDetailContentView(
            postState = UiState.Success(DummyData.aktuellPost1),
            navController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onPushNotificationsNavigate = {},
            onRetry = {}
        )
    }
}