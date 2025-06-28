package ch.seesturm.pfadiseesturm.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import ch.seesturm.pfadiseesturm.presentation.aktuell.list.components.AktuellCardView
import ch.seesturm.pfadiseesturm.presentation.aktuell.list.components.AktuellLoadingCardView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.AnlassCardView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.AnlassLoadingCardView
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.MainSectionHeader
import ch.seesturm.pfadiseesturm.presentation.common.MainSectionHeaderType
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.forms.rememberStickyHeaderOffsets
import ch.seesturm.pfadiseesturm.presentation.common.forms.seesturmStickyHeader
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.home.weather.WeatherCardView
import ch.seesturm.pfadiseesturm.presentation.home.weather.WeatherLoadingCell
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.home.AktivitaetHomeHorizontalScrollView
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.home.AktivitaetHomeLoadingView
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    viewModel: HomeViewModel,
    calendar: SeesturmCalendar,
    bottomNavigationInnerPadding: PaddingValues,
    tabNavController: NavController,
    homeNavController: NavController
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    HomeContentView(
        uiState = uiState,
        calendar = calendar,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        onRefresh = {
            viewModel.refresh()
        },
        onToggleStufe = { stufe ->
            viewModel.toggleStufe(stufe)
        },
        onChangeTab = { destination ->
            tabNavController.navigate(destination) {
                popUpTo(tabNavController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        onNavigate = { destination ->
            homeNavController.navigate(destination)
        },
        onFetchAktivitaet = { stufe, isPullToRefresh ->
            viewModel.viewModelScope.launch {
                viewModel.fetchAktivitaet(
                    stufe = stufe,
                    isPullToRefresh = isPullToRefresh
                )
            }
        },
        onFetchPost = { isPullToRefresh ->
            viewModel.viewModelScope.launch {
                viewModel.fetchPost(isPullToRefresh)
            }
        },
        onFetchEvents = { isPullToRefresh ->
            viewModel.viewModelScope.launch {
                viewModel.fetchEvents(isPullToRefresh)
            }
        },
        onFetchWeather = { isPullToRefresh ->
            viewModel.viewModelScope.launch {
                viewModel.fetchWeather(isPullToRefresh)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContentView(
    uiState: HomeListState,
    calendar: SeesturmCalendar,
    bottomNavigationInnerPadding: PaddingValues,
    onRefresh: () -> Unit,
    onToggleStufe: (SeesturmStufe) -> Unit,
    onChangeTab: (AppDestination.MainTabView.Destinations) -> Unit,
    onNavigate: (AppDestination.MainTabView.Destinations.Home.Destinations) -> Unit,
    onFetchAktivitaet: (SeesturmStufe, Boolean) -> Unit,
    onFetchPost: (Boolean) -> Unit,
    onFetchEvents: (Boolean) -> Unit,
    onFetchWeather: (Boolean) -> Unit,
    columnState: LazyListState = rememberLazyListState(),
    refreshState: PullToRefreshState = rememberPullToRefreshState()
) {

    val selectedStufen = when(val localState = uiState.selectedStufen) {
        is UiState.Success -> localState.data.toList()
        else -> emptyList()
    }

    TopBarScaffold(
        topBarStyle = TopBarStyle.Large,
        title = "Pfadi Seesturm"
    ) { topBarInnerPadding ->

        val combinedPadding = bottomNavigationInnerPadding.intersectWith(
            other = topBarInnerPadding,
            layoutDirection = LayoutDirection.Ltr,
            additionalBottomPadding = 16.dp
        )

        val stickyOffsets = rememberStickyHeaderOffsets(columnState, 0)

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                state = columnState,
                contentPadding = combinedPadding,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .pullToRefresh(
                        isRefreshing = uiState.refreshing,
                        state = refreshState,
                        onRefresh = onRefresh
                    )
                    .background(MaterialTheme.colorScheme.background)
            ) {

                // nächste aktivität
                seesturmStickyHeader(
                    uniqueKey = "AktivitaetStickyHeader",
                    stickyOffsets = stickyOffsets
                ) { _ ->
                    MainSectionHeader(
                        sectionTitle = "Nächste Aktivität",
                        icon = Icons.Default.Group,
                        type = MainSectionHeaderType.StufenButton(
                            selectedStufen = selectedStufen,
                            onToggle = {
                                onToggleStufe(it)
                            },
                            enabled = uiState.selectedStufen.isSuccess
                        ),
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                    )
                }
                when (val localState = uiState.selectedStufen) {
                    UiState.Loading -> {
                        item(
                            key = "NaechsteAktivitaetHomeLoadingCell"
                        ) {
                            AktivitaetHomeLoadingView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                    is UiState.Error -> {
                        item(
                            key = "NaechsteAktivitaetHomeErrorCell"
                        ) {
                            ErrorCardView(
                                errorDescription = localState.message,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .animateItem()
                            )
                        }
                    }
                    is UiState.Success -> {
                        if (localState.data.isEmpty()) {
                            item(
                                key = "NaechsteAktivitaetKeineStufeAusgewähltCell"
                            ) {
                                Text(
                                    "Wähle eine Stufe aus, um die Infos zur nächsten Aktivität anzuzeigen",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 60.dp)
                                        .padding(horizontal = 16.dp)
                                        .alpha(0.4f)
                                        .animateItem()
                                )
                            }
                        }
                        else {
                            item(
                                key = "NaechsteAktivitaetHomeHorizontalSlider"
                            ) {
                                AktivitaetHomeHorizontalScrollView(
                                    stufen = localState.data,
                                    naechsteAktivitaetState = uiState.naechsteAktivitaetState,
                                    screenWidth = maxWidth,
                                    onRetry = { stufe ->
                                        onFetchAktivitaet(stufe, false)
                                    },
                                    onNavigate = onNavigate,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateItem()
                                )
                            }
                        }
                    }
                }

                // aktuell
                seesturmStickyHeader(
                    uniqueKey = "AktuellStickyHeader",
                    stickyOffsets = stickyOffsets
                ) { _ ->
                    MainSectionHeader(
                        "Aktuell",
                        Icons.Default.Newspaper,
                        MainSectionHeaderType.Button(
                            "Mehr",
                            Icons.AutoMirrored.Default.ArrowForwardIos
                        ) {
                            onChangeTab(AppDestination.MainTabView.Destinations.Aktuell)
                        },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                    )
                }
                when (val aktuellState = uiState.aktuellResult) {
                    UiState.Loading -> {
                        item(
                            key = "HomeAktuellLoadingCell"
                        ) {
                            AktuellLoadingCardView(
                                modifier = Modifier
                                    .animateItem()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                    is UiState.Error -> {
                        item(
                            key = "HomeAktuellErrorCell"
                        ) {
                            ErrorCardView(
                                errorDescription = aktuellState.message,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .animateItem(),
                                retryAction = {
                                    onFetchPost(false)
                                }
                            )
                        }
                    }
                    is UiState.Success -> {
                        item(
                            key = "HomeAktuellCell"
                        ) {
                            AktuellCardView(
                                post = aktuellState.data,
                                onClick = {
                                    onNavigate(
                                        AppDestination.MainTabView.Destinations.Home.Destinations.AktuellDetail(
                                            aktuellState.data.id
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .animateItem()
                            )
                        }
                    }
                }

                // termine
                seesturmStickyHeader(
                    uniqueKey = "TermineStickyHeader",
                    stickyOffsets = stickyOffsets
                ) { _ ->
                    MainSectionHeader(
                        "Anlässe",
                        Icons.Default.CalendarMonth,
                        MainSectionHeaderType.Button(
                            "Mehr",
                            Icons.AutoMirrored.Default.ArrowForwardIos
                        ) {
                            onChangeTab(AppDestination.MainTabView.Destinations.Anlaesse)
                        },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                    )
                }
                when (val termineState = uiState.anlaesseResult) {
                    UiState.Loading -> {
                        items(
                            count = 3,
                            key = { index ->
                                "HomeTermineLoadingCell$index"
                            }
                        ) {
                            AnlassLoadingCardView(
                                onAppear = null,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .animateItem()
                            )
                        }
                    }
                    is UiState.Error -> {
                        item(
                            key = "HomeAnlaesseErrorCell"
                        ) {
                            ErrorCardView(
                                errorDescription = termineState.message,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .animateItem(),
                                retryAction = {
                                    onFetchEvents(false)
                                }
                            )
                        }
                    }
                    is UiState.Success -> {
                        if (termineState.data.isNotEmpty()) {
                            itemsIndexed(
                                termineState.data,
                                key = { _, event ->
                                    event.id
                                }
                            ) { _, item ->
                                AnlassCardView(
                                    event = item,
                                    calendar = calendar,
                                    onClick = {
                                        onNavigate(
                                            AppDestination.MainTabView.Destinations.Home.Destinations.AnlaesseDetail(
                                                calendar = calendar,
                                                eventId = item.id
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .animateItem()
                                )
                            }
                        }
                        else {
                            item(
                                key = "KeineBevorstehendenAnlaesseCell"
                            ) {
                                Text(
                                    "Keine bevorstehenden Anlässe",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 60.dp)
                                        .padding(horizontal = 16.dp)
                                        .alpha(0.4f)
                                        .animateItem()
                                )
                            }
                        }
                    }
                }

                // Wetter
                seesturmStickyHeader(
                    uniqueKey = "WetterStickyHeader",
                    stickyOffsets = stickyOffsets
                ) { _ ->
                    MainSectionHeader(
                        "Wetter",
                        Icons.Default.WbSunny,
                        MainSectionHeaderType.Blank,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                    )
                }
                when (val weatherState = uiState.weatherResult) {
                    UiState.Loading -> {
                        item(
                            key = "HomeWeatherLoadingCell"
                        ) {
                            WeatherLoadingCell(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .animateItem()
                            )
                        }
                    }
                    is UiState.Error -> {
                        item(
                            key = "HomeWeatherErrorCell"
                        ) {
                            ErrorCardView(
                                errorDescription = weatherState.message,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .animateItem(),
                                retryAction = {
                                    onFetchWeather(false)
                                }
                            )
                        }
                    }
                    is UiState.Success -> {
                        item(
                            key = "HomeWeatherCell"
                        ) {
                            WeatherCardView(
                                weather = weatherState.data,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .animateItem()
                            )
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Loading1")
@Composable
private fun HomeViewPreview1() {
    PfadiSeesturmTheme {
        HomeContentView(
            uiState = HomeListState(
                naechsteAktivitaetState = mapOf(),
                selectedStufen = UiState.Loading,
                aktuellResult = UiState.Loading,
                anlaesseResult = UiState.Loading,
                weatherResult = UiState.Loading,
            ),
            calendar = SeesturmCalendar.TERMINE,
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onRefresh = {  },
            onToggleStufe = {  },
            onChangeTab = {  },
            onNavigate = {  },
            onFetchAktivitaet = { _, _ -> },
            onFetchPost = {  },
            onFetchEvents = {  },
            onFetchWeather = {  }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Loading2")
@Composable
private fun HomeViewPreview2() {
    PfadiSeesturmTheme {
        HomeContentView(
            uiState = HomeListState(
                naechsteAktivitaetState = mapOf(
                    SeesturmStufe.Wolf to UiState.Loading,
                    SeesturmStufe.Biber to UiState.Loading
                ),
                selectedStufen = UiState.Success(setOf(SeesturmStufe.Wolf, SeesturmStufe.Biber)),
                aktuellResult = UiState.Loading,
                anlaesseResult = UiState.Loading,
                weatherResult = UiState.Loading,
            ),
            calendar = SeesturmCalendar.TERMINE,
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onRefresh = {  },
            onToggleStufe = {  },
            onChangeTab = {  },
            onNavigate = {  },
            onFetchAktivitaet = { _, _ -> },
            onFetchPost = {  },
            onFetchEvents = {  },
            onFetchWeather = {  }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Error1")
@Composable
private fun HomeViewPreview3() {
    PfadiSeesturmTheme {
        HomeContentView(
            uiState = HomeListState(
                naechsteAktivitaetState = mapOf(),
                selectedStufen = UiState.Error("Schwerer Fehler"),
                aktuellResult = UiState.Error("Schwerer Fehler"),
                anlaesseResult = UiState.Error("Schwerer Fehler"),
                weatherResult = UiState.Error("Schwerer Fehler"),
            ),
            calendar = SeesturmCalendar.TERMINE,
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onRefresh = {  },
            onToggleStufe = {  },
            onChangeTab = {  },
            onNavigate = {  },
            onFetchAktivitaet = { _, _ -> },
            onFetchPost = {  },
            onFetchEvents = {  },
            onFetchWeather = {  }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Error2")
@Composable
private fun HomeViewPreview4() {
    PfadiSeesturmTheme {
        HomeContentView(
            uiState = HomeListState(
                naechsteAktivitaetState = mapOf(
                    SeesturmStufe.Wolf to UiState.Error("Schwerer Fehler")
                ),
                selectedStufen = UiState.Success(setOf(SeesturmStufe.Wolf)),
                aktuellResult = UiState.Error("Schwerer Fehler"),
                anlaesseResult = UiState.Error("Schwerer Fehler"),
                weatherResult = UiState.Error("Schwerer Fehler"),
            ),
            calendar = SeesturmCalendar.TERMINE,
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onRefresh = {  },
            onToggleStufe = {  },
            onChangeTab = {  },
            onNavigate = {  },
            onFetchAktivitaet = { _, _ -> },
            onFetchPost = {  },
            onFetchEvents = {  },
            onFetchWeather = {  }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Empty anlässe and no event")
@Composable
private fun HomeViewPreview5() {
    PfadiSeesturmTheme {
        HomeContentView(
            uiState = HomeListState(
                naechsteAktivitaetState = mapOf(
                    SeesturmStufe.Wolf to UiState.Success(null)
                ),
                selectedStufen = UiState.Success(setOf(SeesturmStufe.Wolf)),
                aktuellResult = UiState.Success(DummyData.aktuellPost3),
                anlaesseResult = UiState.Success(emptyList()),
                weatherResult = UiState.Success(DummyData.weather)
            ),
            calendar = SeesturmCalendar.TERMINE,
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onRefresh = {  },
            onToggleStufe = {  },
            onChangeTab = {  },
            onNavigate = {  },
            onFetchAktivitaet = { _, _ -> },
            onFetchPost = {  },
            onFetchEvents = {  },
            onFetchWeather = {  }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("No stufe selected")
@Composable
private fun HomeViewPreview6() {
    PfadiSeesturmTheme {
        HomeContentView(
            uiState = HomeListState(
                naechsteAktivitaetState = mapOf(),
                selectedStufen = UiState.Success(setOf()),
                aktuellResult = UiState.Success(DummyData.aktuellPost3),
                anlaesseResult = UiState.Success(emptyList()),
                weatherResult = UiState.Success(DummyData.weather)
            ),
            calendar = SeesturmCalendar.TERMINE,
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onRefresh = {  },
            onToggleStufe = {  },
            onChangeTab = {  },
            onNavigate = {  },
            onFetchAktivitaet = { _, _ -> },
            onFetchPost = {  },
            onFetchEvents = {  },
            onFetchWeather = {  }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Success")
@Composable
private fun HomeViewPreview7() {
    PfadiSeesturmTheme {
        HomeContentView(
            uiState = HomeListState(
                naechsteAktivitaetState = mapOf(
                    SeesturmStufe.Wolf to UiState.Success(DummyData.aktivitaet1)
                ),
                selectedStufen = UiState.Success(setOf(SeesturmStufe.Wolf)),
                aktuellResult = UiState.Success(DummyData.aktuellPost3),
                anlaesseResult = UiState.Success(listOf(DummyData.oneDayEvent, DummyData.multiDayEvent)),
                weatherResult = UiState.Success(DummyData.weather)
            ),
            calendar = SeesturmCalendar.TERMINE,
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onRefresh = {  },
            onToggleStufe = {  },
            onChangeTab = {  },
            onNavigate = {  },
            onFetchAktivitaet = { _, _ -> },
            onFetchPost = {  },
            onFetchEvents = {  },
            onFetchWeather = {  }
        )
    }
}