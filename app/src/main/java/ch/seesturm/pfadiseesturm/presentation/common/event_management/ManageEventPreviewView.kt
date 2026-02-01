package ch.seesturm.pfadiseesturm.presentation.common.event_management

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.anlaesse.detail.AnlaesseDetailContentView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.detail.AnlaesseDetailView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.AnlassCardView
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventPreviewType
import ch.seesturm.pfadiseesturm.presentation.common.sheet.LocalScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailCardView
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailViewMode
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import kotlinx.serialization.Serializable

@Composable
fun ManageEventPreviewView(
    type: EventPreviewType,
    event: GoogleCalendarEvent,
    isDarkTheme: Boolean,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    when (type) {
        is EventPreviewType.Aktivitaet -> {
            ManageAktivitaetenPreviewView(
                event = event,
                stufen = setOf(type.stufe),
                isDarkTheme = isDarkTheme,
                modifier = modifier
            )
        }
        is EventPreviewType.MultipleAktivitaeten -> {
            ManageAktivitaetenPreviewView(
                event = event,
                stufen = type.stufen,
                isDarkTheme = isDarkTheme,
                modifier = modifier
            )
        }
        is EventPreviewType.Termin -> {
            ManageTerminPreviewView(
                event = event,
                calendar = type.calendar,
                navController = navController,
                isDarkTheme = isDarkTheme,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ManageAktivitaetenPreviewView(
    event: GoogleCalendarEvent,
    stufen: Set<SeesturmStufe>,
    modifier: Modifier,
    isDarkTheme: Boolean,
    listState: LazyListState = rememberLazyListState()
) {

    require(stufen.isNotEmpty()) { "Stufen must not be empty" }

    val sortedStufen = remember(stufen) {
        stufen.sortedBy { it.id }
    }
    var selectedStufe by remember {
        mutableStateOf(sortedStufen.first())
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
            .fillMaxSize()
    ) {
        if (sortedStufen.size > 1) {
            stickyHeader {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    sortedStufen.forEachIndexed { index, stufe ->
                        SegmentedButton(
                            selected = stufe == selectedStufe,
                            onClick = {
                                selectedStufe = stufe
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = sortedStufen.size
                            ),
                            colors = SegmentedButtonDefaults.colors().copy(
                                activeContentColor = stufe.highContrastColor(isDarkTheme)
                            )
                        ) {
                            Text(
                                text = stufe.stufenNameShort,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
        item(
            key = selectedStufe
        ) {
            AktivitaetDetailCardView(
                aktivitaet = event,
                stufe = selectedStufe,
                mode = AktivitaetDetailViewMode.ViewOnly,
                isDarkTheme = isDarkTheme,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
            )
        }
    }
}

@Composable
private fun ManageTerminPreviewView(
    event: GoogleCalendarEvent,
    calendar: SeesturmCalendar,
    modifier: Modifier,
    isDarkTheme: Boolean,
    navController: NavHostController,
    listState: LazyListState = rememberLazyListState()
) {
    NavHost(
        navController = navController,
        modifier = modifier
            .fillMaxSize(),
        startDestination = ManageTerminPreviewNavigationDestinations.Root,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        }
    ) {
        composable<ManageTerminPreviewNavigationDestinations.Root> {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    AnlassCardView(
                        event = event,
                        calendar = calendar,
                        onClick = {
                            navController.navigate(
                                ManageTerminPreviewNavigationDestinations.Detail
                            )
                        },
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
        composable<ManageTerminPreviewNavigationDestinations.Detail> {
            AnlaesseDetailContentView(
                terminState = UiState.Success(event),
                calendar = calendar,
                contentPadding = PaddingValues(16.dp),
                onRetry = {}
            )
        }
    }
}

@Serializable
sealed interface ManageTerminPreviewNavigationDestinations {
    @Serializable
    data object Root: ManageTerminPreviewNavigationDestinations
    @Serializable
    data object Detail: ManageTerminPreviewNavigationDestinations
}

@Preview("Termin normal")
@Composable
private fun ManageEventPreviewViewPreview1() {
    PfadiSeesturmTheme {
        CompositionLocalProvider(
            LocalScreenContext provides ScreenContext.ModalBottomSheet
        ) {
            ManageEventPreviewView(
                type = EventPreviewType.Termin(calendar = SeesturmCalendar.TERMINE),
                isDarkTheme = false,
                event = DummyData.aktivitaet2,
                navController = rememberNavController(),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}
@Preview("Termin Leitungsteam")
@Composable
private fun ManageEventPreviewViewPreview2() {
    PfadiSeesturmTheme {
        CompositionLocalProvider(
            LocalScreenContext provides ScreenContext.ModalBottomSheet
        ) {
            ManageEventPreviewView(
                type = EventPreviewType.Termin(calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM),
                isDarkTheme = false,
                event = DummyData.aktivitaet2,
                navController = rememberNavController(),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}
@Preview("Single aktivitaet")
@Composable
private fun ManageEventPreviewViewPreview3() {
    PfadiSeesturmTheme {
        CompositionLocalProvider(
            LocalScreenContext provides ScreenContext.ModalBottomSheet
        ) {
            ManageEventPreviewView(
                type = EventPreviewType.Aktivitaet(stufe = SeesturmStufe.Wolf),
                isDarkTheme = false,
                event = DummyData.aktivitaet2,
                navController = rememberNavController(),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}
@Preview("Multiple aktivitaeten")
@Composable
private fun ManageEventPreviewViewPreview4() {
    PfadiSeesturmTheme {
        CompositionLocalProvider(
            LocalScreenContext provides ScreenContext.ModalBottomSheet
        ) {
            ManageEventPreviewView(
                type = EventPreviewType.MultipleAktivitaeten(stufen = SeesturmStufe.entries.toSet()),
                isDarkTheme = false,
                event = DummyData.aktivitaet2,
                navController = rememberNavController(),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}
