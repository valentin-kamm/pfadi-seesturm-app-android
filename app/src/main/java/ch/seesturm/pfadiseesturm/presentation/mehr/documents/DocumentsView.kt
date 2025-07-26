package ch.seesturm.pfadiseesturm.presentation.mehr.documents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressDocument
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.TopBarNavigationIcon
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.mehr.documents.components.DocumentCardView
import ch.seesturm.pfadiseesturm.presentation.mehr.documents.components.DocumentLoadingCardView
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import ch.seesturm.pfadiseesturm.util.types.WordpressDocumentType
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.launchWebsite
import ch.seesturm.pfadiseesturm.util.state.UiState

@Composable
fun DocumentsView(
    viewModel: DocumentsViewModel,
    documentType: WordpressDocumentType,
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    DocumentsContentView(
        uiState = uiState,
        documentType = documentType,
        navController = navController,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        onRetry = {
            viewModel.fetchDocuments()
        },
        onClick = {
            launchWebsite(
                url = it.documentUrl,
                context = context
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocumentsContentView(
    uiState:  UiState<List<WordpressDocument>>,
    documentType: WordpressDocumentType,
    navController: NavController,
    bottomNavigationInnerPadding: PaddingValues,
    onRetry: () -> Unit,
    onClick: (WordpressDocument) -> Unit,
    columnState: LazyListState = rememberLazyListState()
) {
    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        title = documentType.title,
        navigationAction = TopBarNavigationIcon.Back { navController.navigateUp() },
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
            val loadingCellCount = 4
            when (uiState) {
                UiState.Loading -> {
                    items(
                        count = loadingCellCount,
                        key = { index ->
                            "DokumenteLoadingCell$index"
                        }
                    ) { index ->
                        DocumentLoadingCardView(
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
                        ErrorCardView(
                            errorTitle = "Ein Fehler ist aufgetreten",
                            errorDescription = uiState.message,
                            modifier = Modifier
                                .animateItem()
                        ) {
                            onRetry()
                        }
                    }
                }
                is UiState.Success -> {
                    if (uiState.data.isEmpty()) {
                        item(
                            key = "NoDocumentsCell"
                        ) {
                            Text(
                                "Keine Dokumente",
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
                    else {
                        itemsIndexed(
                            items = uiState.data,
                            key = { _, item ->
                                "DokumenteCell${item.id}"
                            }
                        ) { index, item ->
                            DocumentCardView(
                                document = item,
                                items = uiState.data,
                                index = index,
                                onClick = {
                                    onClick(item)
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
}

@Preview("Laden")
@Composable
private fun DokumenteViewPreview1() {
    PfadiSeesturmTheme {
        DocumentsContentView(
            uiState = UiState.Loading,
            documentType = WordpressDocumentType.Luuchtturm,
            navController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onRetry = {},
            onClick = {}
        )
    }
}
@Preview("Fehler")
@Composable
private fun DokumenteViewPreview2() {
    PfadiSeesturmTheme {
        DocumentsContentView(
            uiState = UiState.Error("Schlimmer Fehler"),
            documentType = WordpressDocumentType.Luuchtturm,
            navController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onRetry = {},
            onClick = {}
        )
    }
}

@Preview("No documents")
@Composable
private fun DokumenteViewPreview3() {
    PfadiSeesturmTheme {
        DocumentsContentView(
            uiState = UiState.Success(listOf()),
            documentType = WordpressDocumentType.Luuchtturm,
            navController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onRetry = {},
            onClick = {}
        )
    }
}
@Preview("Erfolg")
@Composable
private fun DokumenteViewPreview4() {
    PfadiSeesturmTheme {
        DocumentsContentView(
            uiState = UiState.Success(DummyData.documents),
            documentType = WordpressDocumentType.Luuchtturm,
            navController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onRetry = {},
            onClick = {}
        )
    }
}