package ch.seesturm.pfadiseesturm.presentation.mehr.leitungsteam

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.repository.LeitungsteamRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.wordpress.service.LeitungsteamService
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.components.DropdownButton
import ch.seesturm.pfadiseesturm.presentation.common.components.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.forms.myStickyHeader
import ch.seesturm.pfadiseesturm.presentation.common.forms.rememberStickyHeaderOffsets
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.mehr.leitungsteam.components.LeitungsteamCell
import ch.seesturm.pfadiseesturm.presentation.mehr.leitungsteam.components.LeitungsteamLoadingCell
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeitungsteamView(
    viewModel: LeitungsteamViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController,
    selectedStufe: String = "Abteilungsleitung",
    columnState: LazyListState = rememberLazyListState()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val stickyOffsets = rememberStickyHeaderOffsets(columnState, 0)
    var selectedStufe by remember { mutableStateOf(selectedStufe) }
    val loadingCellCount = 7

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        title = "Leitungsteam",
        backNavigationAction = {
            navController.popBackStack()
        }
    ) { topBarInnerPadding ->

        val combinedPadding = bottomNavigationInnerPadding.intersectWith(
            other = topBarInnerPadding,
            layoutDirection = LayoutDirection.Ltr
        )
        LazyColumn(
            state = columnState,
            userScrollEnabled = !uiState.scrollingDisabled,
            contentPadding = combinedPadding,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val localState = uiState) {
                UiState.Loading -> {
                    myStickyHeader(
                        uniqueKey = "LeitungsteamLoadingStickyHeader",
                        stickyOffsets = stickyOffsets
                    ) { _ ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .fillMaxWidth()
                                .padding(16.dp)
                                .animateItem()
                        ) {
                            RedactedText(
                                numberOfLines = 1,
                                textStyle = MaterialTheme.typography.titleLarge,
                                lastLineFraction = 0.55f
                            )
                        }
                    }
                    items(
                        count = loadingCellCount,
                        key = { index ->
                            "LeitungsteamLoadingCell$index"
                        }
                    ) { index ->
                        LeitungsteamLoadingCell(
                            modifier = Modifier
                                .animateItem()
                                .padding(horizontal = 16.dp)
                                .padding(top = if (index == 0) 16.dp else 0.dp)
                        )
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "LeitungsteamErroCell"
                    ) {
                        CardErrorView(
                            errorTitle = "Ein Fehler ist aufgetreten",
                            errorDescription = localState.message,
                            modifier = Modifier
                                .padding(16.dp)
                                .animateItem()
                        ) {
                            viewModel.fetchLeitungsteam()
                        }
                    }
                }
                is UiState.Success -> {
                    myStickyHeader(
                        uniqueKey = "LeitungsteamStickyHeader",
                        stickyOffsets = stickyOffsets
                    ) { _ ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .fillMaxWidth()
                                .padding(16.dp)
                                .animateItem()
                        ) {
                            Text(
                                text = selectedStufe,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier
                                    .weight(1f)
                                    .alpha(0.4f)
                            )
                            Box(
                                modifier = Modifier
                                    .wrapContentSize()
                            ) {
                                DropdownButton(
                                    title = "Stufe",
                                    dropdown = { isShown, dismiss ->
                                        DropdownMenu(
                                            expanded = isShown,
                                            onDismissRequest = {
                                                dismiss()
                                            }
                                        ) {
                                            localState.data.reversed().forEach { stufe ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            text = stufe.teamName
                                                        )
                                                    },
                                                    onClick = {
                                                        dismiss()
                                                        selectedStufe = stufe.teamName
                                                    },
                                                    trailingIcon = {
                                                        if (stufe.teamName == selectedStufe) {
                                                            Icon(
                                                                imageVector = Icons.Default.Check,
                                                                contentDescription = null
                                                            )
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    if (localState.data.map { it.teamName }.contains(selectedStufe)) {
                        itemsIndexed(
                            items = localState.data.first { it.teamName == selectedStufe }.members,
                            key = { index, _ ->
                                "LeitungsteamCell$index"
                            }
                        ) { index, item ->
                            LeitungsteamCell(
                                member = item,
                                modifier = Modifier
                                    .animateItem()
                                    .padding(horizontal = 16.dp)
                                    .padding(top = if (index == 0) 16.dp else 0.dp)
                                    .animateItem()
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
fun LeitungsteamViewPreview() {
    LeitungsteamView(
        viewModel = LeitungsteamViewModel(
            service = LeitungsteamService(
                repository = LeitungsteamRepositoryImpl(
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