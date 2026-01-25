package ch.seesturm.pfadiseesturm.presentation.mehr.leitungsteam

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
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
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumn
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.mehr.leitungsteam.components.LeitungsteamCell
import ch.seesturm.pfadiseesturm.presentation.mehr.leitungsteam.components.LeitungsteamLoadingCell
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
private fun LeitungsteamContentView(
    uiState: UiState<List<Leitungsteam>>,
    navController: NavController,
    bottomNavigationInnerPadding: PaddingValues,
    onRetry: () -> Unit,
    columnState: LazyListState = rememberLazyListState()
) {

    var selectedStufe by remember { mutableStateOf("Abteilungsleitung") }

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

        GroupedColumn(
            state = columnState,
            userScrollEnabled = !uiState.scrollingDisabled,
            contentPadding = combinedPadding,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (uiState) {
                UiState.Loading -> {
                    section(
                        header = {
                            RedactedText(
                                numberOfLines = 1,
                                textStyle = MaterialTheme.typography.titleLarge,
                                lastLineFraction = 0.55f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    ) {
                        items(
                            count = 2,
                            key = { index ->
                                "LeitungsteamLoadingCell$index"
                            },
                            padding = { index ->
                                PaddingValues(
                                    top = if (index == 0) 16.dp else 0.dp,
                                    bottom = 0.dp,
                                    start = 16.dp,
                                    end = 16.dp
                                )
                            }
                        ) {
                            LeitungsteamLoadingCell(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    section {
                        customItem(
                            key = "LeitungsteamErrorCell"
                        ) {
                            ErrorCardView(
                                errorTitle = "Ein Fehler ist aufgetreten",
                                errorDescription = uiState.message,
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                onRetry()
                            }
                        }
                    }
                }
                is UiState.Success -> {
                    section(
                        header = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = selectedStufe,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier
                                        .weight(1f)
                                        .alpha(0.4f)
                                )
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
                    ) {
                        if (uiState.data.map { it.teamName }.contains(selectedStufe)) {
                            itemsIndexed(
                                items = uiState.data.first { it.teamName == selectedStufe}.members,
                                key = { index, _ ->
                                    "LeitungsteamCell$index"
                                },
                                padding = { index , _->
                                    PaddingValues(
                                        top = if (index == 0) 16.dp else 0.dp,
                                        bottom = 0.dp,
                                        start = 16.dp,
                                        end = 16.dp
                                    )
                                }
                            ) { _, member ->
                                LeitungsteamCell(
                                    member = member,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                )
                            }
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