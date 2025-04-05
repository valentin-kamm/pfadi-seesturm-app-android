package ch.seesturm.pfadiseesturm.presentation.mehr.documents

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.repository.WordpressDocumentsRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.wordpress.service.WordpressDocumentsService
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.mehr.documents.components.DokumenteLuuchtturmCell
import ch.seesturm.pfadiseesturm.presentation.mehr.documents.components.DokumenteLuuchtturmLoadingCell
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import ch.seesturm.pfadiseesturm.util.launchWebsite
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuuchtturmView(
    viewModel: LuuchtturmViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController,
    columnState: LazyListState = rememberLazyListState()
) {

    val context = LocalContext.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        title = "Lüüchtturm",
        backNavigationAction = {
            navController.popBackStack()
        }
    ) { topBarInnerPadding ->

        val combinedPadding = bottomNavigationInnerPadding.intersectWith(
            other = topBarInnerPadding,
            layoutDirection = LayoutDirection.Ltr,
            additionalTopPadding = 32.dp,
            additionalEndPadding = 16.dp,
            additionalStartPadding = 16.dp,
            additionalBottomPadding = 16.dp
        )
        LazyColumn(
            state = columnState,
            userScrollEnabled = !uiState.scrollingDisabled,
            contentPadding = combinedPadding,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            val loadingCellCount = 6
            when (val localState = uiState) {
                UiState.Loading -> {
                    items(
                        count = loadingCellCount,
                        key = { index ->
                            "DokumenteLoadingCell$index"
                        }
                    ) { index ->
                        DokumenteLuuchtturmLoadingCell(
                            items = (0..<loadingCellCount).toList(),
                            index = index,
                            modifier = Modifier
                                .animateItem()
                        )
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "DokumenteErrorCell"
                    ) {
                        CardErrorView(
                            errorTitle = "Ein Fehler ist aufgetreten",
                            errorDescription = localState.message,
                            modifier = Modifier
                                .animateItem()
                        ) {
                            viewModel.fetchLuuchtturm()
                        }
                    }
                }
                is UiState.Success -> {
                    itemsIndexed(
                        items = localState.data,
                        key = { _, item ->
                            "DokumenteCell${item.id}"
                        }
                    ) { index, item ->
                        DokumenteLuuchtturmCell(
                            document = item,
                            items = localState.data,
                            index = index,
                            onClick = {
                                launchWebsite(
                                    url = item.url,
                                    context = context
                                )
                            },
                            modifier = Modifier
                                .animateItem()
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun LuuchtturmViewPreview() {
    LuuchtturmView(
        viewModel = LuuchtturmViewModel(
            service = WordpressDocumentsService(
                repository = WordpressDocumentsRepositoryImpl(
                    api = Retrofit.Builder()
                        .baseUrl(Constants.SEESTURM_API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(WordpressApi::class.java)
                )
            )
        ),
        bottomNavigationInnerPadding = PaddingValues(0.dp),
        navController = rememberNavController()
    )
}
