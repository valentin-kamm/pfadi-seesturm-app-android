package ch.seesturm.pfadiseesturm.presentation.mehr.leitungsteam

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
import ch.seesturm.pfadiseesturm.domain.wordpress.model.Leitungsteam
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenu
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenuItem
import ch.seesturm.pfadiseesturm.presentation.common.TopBarNavigationIcon
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.buttons.DropdownButton
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.forms.rememberStickyHeaderOffsets
import ch.seesturm.pfadiseesturm.presentation.common.forms.seesturmStickyHeader
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.mehr.leitungsteam.components.LeitungsteamCell
import ch.seesturm.pfadiseesturm.presentation.mehr.leitungsteam.components.LeitungsteamLoadingCell
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.UiState

@Composable
fun LeitungsteamView(
    viewModel: LeitungsteamViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LeitungsteamContentView(
        uiState = uiState,
        navController = navController,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        onRetry = {
            viewModel.fetchLeitungsteam()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeitungsteamContentView(
    uiState: UiState<List<Leitungsteam>>,
    navController: NavController,
    bottomNavigationInnerPadding: PaddingValues,
    onRetry: () -> Unit,
    columnState: LazyListState = rememberLazyListState()
) {

    val stickyOffsets = rememberStickyHeaderOffsets(columnState, 0)
    var selectedStufe by remember { mutableStateOf("Abteilungsleitung") }
    val loadingCellCount = 7

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        title = "Leitungsteam",
        navigationAction = TopBarNavigationIcon.Back { navController.navigateUp() }
    ) { topBarInnerPadding ->

        val combinedPadding = bottomNavigationInnerPadding.intersectWith(
            other = topBarInnerPadding,
            layoutDirection = LayoutDirection.Ltr,
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
            when (uiState) {
                UiState.Loading -> {
                    seesturmStickyHeader(
                        uniqueKey = "LeitungsteamLoadingStickyHeader",
                        stickyOffsets = stickyOffsets
                    ) { _ ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .fillMaxWidth()
                                .animateItem()
                                .padding(16.dp)
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
                        FormItem(
                            items = (0..<loadingCellCount).toList(),
                            index = index,
                            modifier = Modifier
                                .animateItem()
                                .padding(horizontal = 16.dp)
                                .padding(top = if (index == 0) 16.dp else 0.dp),
                            mainContent = FormItemContentType.Custom(
                                content = {
                                    LeitungsteamLoadingCell()
                                }
                            ),
                        )
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "LeitungsteamErroCell"
                    ) {
                        ErrorCardView(
                            errorTitle = "Ein Fehler ist aufgetreten",
                            errorDescription = uiState.message,
                            modifier = Modifier
                                .animateItem()
                                .padding(16.dp)
                        ) {
                            onRetry()
                        }
                    }
                }
                is UiState.Success -> {
                    seesturmStickyHeader(
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
                                        ThemedDropdownMenu(
                                            expanded = isShown,
                                            onDismissRequest = {
                                                dismiss()
                                            }
                                        ) {
                                            uiState.data.reversed().forEach { stufe ->
                                                ThemedDropdownMenuItem(
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
                    if (uiState.data.map { it.teamName }.contains(selectedStufe)) {
                        val members = uiState.data.first { it.teamName == selectedStufe }.members
                        itemsIndexed(
                            items = members,
                            key = { index, _ ->
                                "LeitungsteamCell$index"
                            }
                        ) { index, item ->
                            FormItem(
                                items = members,
                                index = index,
                                modifier = Modifier
                                    .animateItem()
                                    .padding(horizontal = 16.dp)
                                    .padding(top = if (index == 0) 16.dp else 0.dp),
                                mainContent = FormItemContentType.Custom(
                                    content = {
                                        LeitungsteamCell(
                                            member = item
                                        )
                                    }
                                )
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
private fun LeitungsteamViewPreview1() {
    PfadiSeesturmTheme {
        LeitungsteamContentView(
            uiState = UiState.Loading,
            navController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onRetry = {}
        )
    }
}
@Preview("Error")
@Composable
private fun LeitungsteamViewPreview2() {
    PfadiSeesturmTheme {
        LeitungsteamContentView(
            uiState = UiState.Error("Schwerer Fehler"),
            navController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onRetry = {}
        )
    }
}
@Preview("Success")
@Composable
private fun LeitungsteamViewPreview3() {
    PfadiSeesturmTheme {
        LeitungsteamContentView(
            uiState = UiState.Success(
                listOf(
                    Leitungsteam(
                        id = 123,
                        teamName = "Abteilungsleitung",
                        members = listOf(
                            DummyData.leitungsteamMember,
                            DummyData.leitungsteamMember,
                            DummyData.leitungsteamMember,
                            DummyData.leitungsteamMember,
                            DummyData.leitungsteamMember,
                            DummyData.leitungsteamMember
                        )
                    )
                )
            ),
            navController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onRetry = {}
        )
    }
}