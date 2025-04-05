package ch.seesturm.pfadiseesturm.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.data.data_store.dao.SeesturmPreferencesDao
import ch.seesturm.pfadiseesturm.data.data_store.repository.SelectedStufenRepositoryImpl
import ch.seesturm.pfadiseesturm.data.firestore.FirestoreApiImpl
import ch.seesturm.pfadiseesturm.data.firestore.repository.FirestoreRepositoryImpl
import ch.seesturm.pfadiseesturm.util.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.repository.AktuellRepositoryImpl
import ch.seesturm.pfadiseesturm.data.wordpress.repository.AnlaesseRepositoryImpl
import ch.seesturm.pfadiseesturm.data.wordpress.repository.NaechsteAktivitaetRepositoryImpl
import ch.seesturm.pfadiseesturm.data.wordpress.repository.WeatherRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AktuellService
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AnlaesseService
import ch.seesturm.pfadiseesturm.domain.wordpress.service.NaechsteAktivitaetService
import ch.seesturm.pfadiseesturm.domain.wordpress.service.WeatherService
import ch.seesturm.pfadiseesturm.presentation.aktuell.list.components.AktuellCardView
import ch.seesturm.pfadiseesturm.presentation.aktuell.list.components.AktuellLoadingCell
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.AnlassCardView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.AnlassLoadingCardView
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.components.DropdownButton
import ch.seesturm.pfadiseesturm.presentation.common.components.MainSectionHeader
import ch.seesturm.pfadiseesturm.presentation.common.components.MainSectionHeaderType
import ch.seesturm.pfadiseesturm.presentation.common.forms.myStickyHeader
import ch.seesturm.pfadiseesturm.presentation.common.forms.rememberStickyHeaderOffsets
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.home.components.WeatherCell
import ch.seesturm.pfadiseesturm.presentation.home.components.WeatherLoadingCell
import ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen.FakeDataStore
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.home.AktivitaetHomeHorizontalScrollView
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.home.AktivitaetHomeLoadingView
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.navigation.AppDestination
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    bottomNavigationInnerPadding: PaddingValues,
    tabNavController: NavController,
    homeNavController: NavController,
    calendar: SeesturmCalendar,
    viewModel: HomeViewModel,
    columnState: LazyListState = rememberLazyListState(),
    refreshState: PullToRefreshState = rememberPullToRefreshState()
) {

    TopBarScaffold(
        topBarStyle = TopBarStyle.Large,
        title = "Pfadi Seesturm"
    ) { topBarInnerPadding ->

        val uiState by viewModel.state.collectAsStateWithLifecycle()
        val combinedPadding = bottomNavigationInnerPadding.intersectWith(topBarInnerPadding, LayoutDirection.Ltr)
        val corountineScope = rememberCoroutineScope()

        // Calculate sticky offsets for all sticky headers
        val stickyOffsets = rememberStickyHeaderOffsets(columnState, 0)

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                state = columnState,
                userScrollEnabled = true,
                contentPadding = combinedPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .pullToRefresh(
                        isRefreshing = uiState.refreshing,
                        state = refreshState,
                        onRefresh = {
                            viewModel.refresh()
                        }
                    )
                    .background(MaterialTheme.colorScheme.background)
            ) {

                // nächste aktivität
                myStickyHeader(
                    uniqueKey = "AktivitaetStickyHeader",
                    stickyOffsets = stickyOffsets
                ) { _ ->
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MainSectionHeader(
                            sectionTitle = "Nächste Aktivität",
                            icon = Icons.Default.Group,
                            type = MainSectionHeaderType.Custom(
                                content = {
                                    DropdownButton(
                                        title = viewModel.stufenDropdownText,
                                        enabled = uiState.selectedStufen.isSuccess,
                                        dropdown = { isShown, dismiss ->
                                            DropdownMenu(
                                                expanded = isShown,
                                                onDismissRequest = {
                                                    dismiss()
                                                }
                                            ) {
                                                SeesturmStufe.entries.sortedBy { it.id }.forEach { stufe ->
                                                    DropdownMenuItem(
                                                        text = { Text(text = stufe.stufenName) },
                                                        onClick = {
                                                            viewModel.toggleStufe(stufe)
                                                        },
                                                        trailingIcon = {
                                                            when (val localState = uiState.selectedStufen) {
                                                                is UiState.Success -> {
                                                                    if (localState.data.contains(stufe)) {
                                                                        Icon(
                                                                            imageVector = Icons.Default.Check,
                                                                            contentDescription = null
                                                                        )
                                                                    }
                                                                }
                                                                else -> {
                                                                    // nothing
                                                                }
                                                            }
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                            ),
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                        )
                    }
                }
                when (val localState = uiState.selectedStufen) {
                    UiState.Loading -> {
                        item(
                            key = "NaechsteAktivitaetHomeLoadingCell"
                        ) {
                            AktivitaetHomeLoadingView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .animateItem()
                            )
                        }
                    }
                    is UiState.Error -> {
                        item(
                            key = "NaechsteAktivitaetHomeErrorCell"
                        ) {
                            CardErrorView(
                                errorTitle = "Ein Fehler ist aufgetreten.",
                                errorDescription = localState.message,
                                modifier = Modifier
                                    .padding(16.dp)
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
                                        .padding(vertical = 75.dp)
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
                                        corountineScope.launch {
                                            viewModel.fetchAktivitaet(
                                                stufe = stufe,
                                                isPullToRefresh = false
                                            )
                                        }
                                    },
                                    homeNavController = homeNavController,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateItem()
                                )
                            }
                        }
                    }
                }

                // aktuell
                myStickyHeader(
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
                            tabNavController.navigate(AppDestination.MainTabView.Destinations.Aktuell) {
                                popUpTo(tabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
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
                            AktuellLoadingCell(
                                null,
                                modifier = Modifier
                                    .animateItem()
                                    .padding(top = 16.dp)
                            )
                        }
                    }
                    is UiState.Error -> {
                        item(
                            key = "HomeAktuellErrorCell"
                        ) {
                            CardErrorView(
                                "Ein Fehler ist aufgetreten",
                                aktuellState.message,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .animateItem()
                            ) {
                                corountineScope.launch {
                                    viewModel.fetchPost(false)
                                }
                            }
                        }
                    }
                    is UiState.Success -> {
                        item(
                            key = "HomeAktuellCell"
                        ) {
                            AktuellCardView(
                                post = aktuellState.data,
                                onClick = {
                                    homeNavController.navigate(
                                        AppDestination.MainTabView.Destinations.Home.Destinations.AktuellDetail(
                                            aktuellState.data.id
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .animateItem()
                            )
                        }
                    }
                }

                // termine
                myStickyHeader(
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
                            tabNavController.navigate(AppDestination.MainTabView.Destinations.Anlaesse) {
                                popUpTo(tabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
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
                        ) { index ->
                            AnlassLoadingCardView(
                                onAppear = null,
                                modifier = Modifier
                                    .padding(top = if (index == 0) 16.dp else 0.dp)
                                    .animateItem()
                            )
                        }
                    }
                    is UiState.Error -> {
                        item(
                            key = "HomeAnlaesseErrorCell"
                        ) {
                            CardErrorView(
                                "Ein Fehler ist aufgetreten",
                                termineState.message,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .animateItem()
                            ) {
                                corountineScope.launch {
                                    viewModel.fetchEvents(false)
                                }
                            }
                        }
                    }
                    is UiState.Success -> {
                        if (termineState.data.isNotEmpty()) {
                            itemsIndexed(
                                termineState.data,
                                key = { _, event ->
                                    event.id
                                }
                            ) { index, item ->
                                AnlassCardView(
                                    event = item,
                                    calendar = calendar,
                                    onClick = {
                                        homeNavController.navigate(
                                            AppDestination.MainTabView.Destinations.Home.Destinations.AnlaesseDetail(
                                                calendar = calendar,
                                                eventId = item.id
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(top = if (index == 0) 16.dp else 0.dp)
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
                                        .padding(vertical = 75.dp)
                                        .padding(horizontal = 16.dp)
                                        .alpha(0.4f)
                                        .animateItem()
                                )
                            }
                        }
                    }
                }

                // Wetter
                myStickyHeader(
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
                                    .padding(16.dp)
                            )
                        }
                    }
                    is UiState.Error -> {
                        item(
                            key = "HomeWeatherErrorCell"
                        ) {
                            CardErrorView(
                                "Ein Fehler ist aufgetreten",
                                weatherState.message,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .animateItem()
                            ) {
                                corountineScope.launch {
                                    viewModel.fetchWeather(false)
                                }
                            }
                        }
                    }
                    is UiState.Success -> {
                        item(
                            key = "HomeWeatherCell"
                        ) {
                            WeatherCell(
                                weather = weatherState.data,
                                modifier = Modifier
                                    .padding(16.dp)
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

@SuppressLint("ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeViewPreview() {
    HomeView(
        bottomNavigationInnerPadding = PaddingValues(0.dp),
        tabNavController = rememberNavController(),
        homeNavController = rememberNavController(),
        calendar = SeesturmCalendar.TERMINE,
        viewModel = HomeViewModel(
            calendar = SeesturmCalendar.TERMINE,
            anlaesseService = AnlaesseService(
                AnlaesseRepositoryImpl(
                    Retrofit.Builder()
                        .baseUrl(Constants.SEESTURM_API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(WordpressApi::class.java)
                )
            ),
            aktuellService = AktuellService(
                AktuellRepositoryImpl(
                    Retrofit.Builder()
                        .baseUrl(Constants.SEESTURM_API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(WordpressApi::class.java)
                )
            ),
            weatherService = WeatherService(
                WeatherRepositoryImpl(
                    Retrofit.Builder()
                        .baseUrl(Constants.SEESTURM_API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(WordpressApi::class.java)
                )
            ),
            naechsteAktivitaetService = NaechsteAktivitaetService(
                NaechsteAktivitaetRepositoryImpl(
                    Retrofit.Builder()
                        .baseUrl(Constants.SEESTURM_API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(WordpressApi::class.java)
                ),
                firestoreRepository = FirestoreRepositoryImpl(
                    api = FirestoreApiImpl(
                        Firebase.firestore
                    ),
                    db = Firebase.firestore
                ),
                selectedStufenRepository = SelectedStufenRepositoryImpl(
                    dataStore = FakeDataStore(
                        initialValue = SeesturmPreferencesDao()
                    )
                )
            )
        )
    )
}
