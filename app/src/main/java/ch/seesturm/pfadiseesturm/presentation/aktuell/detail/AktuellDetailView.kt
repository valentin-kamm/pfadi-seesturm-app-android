package ch.seesturm.pfadiseesturm.presentation.aktuell.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.repository.AktuellRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.wordpress.model.hasValidImageUrl
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AktuellService
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.components.HtmlText
import ch.seesturm.pfadiseesturm.presentation.common.components.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.components.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AktuellDetailView(
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController,
    onPushNotificationsNavigate: () -> Unit,
    viewModel: AktuellDetailViewModel,
    columnState: LazyListState = rememberLazyListState()
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        backNavigationAction = {
            navController.popBackStack()
        },
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
            userScrollEnabled = !uiState.scrollingDisabled,
            contentPadding = combinedPadding,
            modifier = Modifier
                .fillMaxSize()
        ) {

            when (val localState = uiState) {
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
                                MaterialTheme.typography.displaySmall,
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
                        CardErrorView(
                            modifier = Modifier
                                .padding(16.dp)
                                .animateItem(),
                            errorTitle = "Ein Fehler ist aufgetreten",
                            errorDescription = localState.message
                        ) {
                            viewModel.getPost()
                        }
                    }
                }
                is UiState.Success -> {
                    val post = localState.data
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
                            if (post.hasValidImageUrl()) {
                                SubcomposeAsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(post.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(post.aspectRatio.toFloat())
                                ) {
                                    when (painter.state) {
                                        is AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Loading -> {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(post.aspectRatio.toFloat())
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
                                                    .aspectRatio(post.aspectRatio.toFloat())
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
                                post.titleDecoded,
                                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(
                                        top = if (post.hasValidImageUrl()) {
                                            0.dp
                                        } else {
                                            16.dp
                                        }
                                    )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Transparent)
                                    .padding(horizontal = 16.dp)

                            ) {
                                Icon(
                                    Icons.Outlined.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .size(
                                            with(LocalDensity.current) {
                                                MaterialTheme.typography.labelMedium.lineHeight
                                                    .toPx()
                                                    .toDp()
                                            }
                                        )
                                        .alpha(0.4f)
                                )
                                Text(
                                    text = post.published.uppercase(),
                                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .alpha(0.4f)
                                )
                            }
                            HtmlText(
                                html = post.content,
                                textColor = MaterialTheme.colorScheme.onBackground,
                                fontStyle = MaterialTheme.typography.bodyLarge,
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

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun AktuellDetailViewPreview() {
    AktuellDetailView(
        bottomNavigationInnerPadding = PaddingValues(0.dp),
        navController = rememberNavController(),
        onPushNotificationsNavigate = {  },
        viewModel = AktuellDetailViewModel(
            postId = 23505,
            service = AktuellService(
                AktuellRepositoryImpl(
                    Retrofit.Builder()
                        .baseUrl(Constants.SEESTURM_API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(WordpressApi::class.java)
                )
            ),
            cacheIdentifier = MemoryCacheIdentifier.List
        )
    )
}