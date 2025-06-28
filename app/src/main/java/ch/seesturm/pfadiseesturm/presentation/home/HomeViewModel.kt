package ch.seesturm.pfadiseesturm.presentation.home

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AktuellService
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AnlaesseService
import ch.seesturm.pfadiseesturm.domain.wordpress.service.NaechsteAktivitaetService
import ch.seesturm.pfadiseesturm.domain.wordpress.service.WeatherService
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarType
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val naechsteAktivitaetService: NaechsteAktivitaetService,
    private val aktuellService: AktuellService,
    private val calendar: SeesturmCalendar,
    private val anlaesseService: AnlaesseService,
    private val weatherService: WeatherService
): ViewModel() {

    private val _state = MutableStateFlow(HomeListState())
    val state = _state.asStateFlow()

    init {
        loadInitialData()
    }

    private val stufenForRefresh: Set<SeesturmStufe>
        get() = _state.value.naechsteAktivitaetState.keys

    private fun loadInitialData() {

        startListeningToSelectedStufenAndAktivitaeten()

        viewModelScope.launch {
            coroutineScope {
                val fetchEventsJob = async {
                    fetchEvents(false)
                }
                val fetchPostJob = async {
                    fetchPost(false)
                }
                val fetchWeatherJob = async {
                    fetchWeather(false)
                }
                fetchEventsJob.await()
                fetchPostJob.await()
                fetchWeatherJob.await()
            }
        }
    }

    fun refresh() {

        updateRefreshingState(true)

        viewModelScope.launch {
            coroutineScope {
                val refreshEventsJob = async {
                    fetchEvents(true)
                }
                val refreshPostJob = async {
                    fetchPost(true)
                }
                val refreshWeatherJob = async {
                    fetchWeather(true)
                }
                val refreshAktivitaetenJob = async {
                    fetchAktivitaeten(stufenForRefresh, true)
                }
                refreshEventsJob.await()
                refreshPostJob.await()
                refreshWeatherJob.await()
                refreshAktivitaetenJob.await()
                updateRefreshingState(false)
            }
        }
    }

    private fun startListeningToSelectedStufenAndAktivitaeten() {

        _state.update {
            it.copy(
                selectedStufen = UiState.Loading
            )
        }

        naechsteAktivitaetService.readSelectedStufen().onEach { result ->
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            selectedStufen = UiState.Error("Nächste Aktivitäten konnten nicht geladen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            selectedStufen = UiState.Success(result.data)
                        )
                    }
                    fetchNecessaryAktivitaeten(result.data, false)
                }
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun fetchNecessaryAktivitaeten(stufen: Set<SeesturmStufe>, isPullToRefresh: Boolean) {

        val stufenToLoad = stufen.filterNot { stufe -> stufe in _state.value.naechsteAktivitaetState.keys }

        val tasks = stufenToLoad.map { stufe ->
            suspend { fetchAktivitaet(stufe, isPullToRefresh) }
        }
        coroutineScope {
            val jobs = tasks.map { task ->
                async {
                    task()
                }
            }
            jobs.awaitAll()
        }
    }

    private suspend fun fetchAktivitaeten(stufen: Set<SeesturmStufe>, isPullToRefresh: Boolean) {

        val tasks = stufen.map { stufe ->
            suspend { fetchAktivitaet(stufe, isPullToRefresh) }
        }
        coroutineScope {
            val jobs = tasks.map { task ->
                async {
                    task()
                }
            }
            jobs.awaitAll()
        }
    }

    suspend fun fetchAktivitaet(stufe: SeesturmStufe, isPullToRefresh: Boolean) {

        if (!isPullToRefresh) {
            upsertNaechsteAktivitaetState(
                stufe = stufe,
                newState = UiState.Loading
            )
        }
        when (val response = naechsteAktivitaetService.fetchNaechsteAktivitaet(stufe)) {
            is SeesturmResult.Error -> {
                upsertNaechsteAktivitaetState(
                    stufe = stufe,
                    newState = UiState.Error("Nächste Aktivität der ${stufe.stufenName} konnte nicht geladen werden. ${response.error.defaultMessage}")
                )
            }
            is SeesturmResult.Success -> {
                upsertNaechsteAktivitaetState(
                    stufe = stufe,
                    newState = UiState.Success(response.data)
                )
            }
        }
    }

    fun toggleStufe(stufe: SeesturmStufe) {

        val localState = state.value.selectedStufen

        if (localState !is UiState.Success) {
            return
        }

        if (localState.data.contains(stufe)) {
            removeStufe(stufe)
        }
        else {
            addStufe(stufe)
        }
    }

    private fun addStufe(stufe: SeesturmStufe) {

        viewModelScope.launch {

            val result = naechsteAktivitaetService.addStufe(stufe)

            if (result is SeesturmResult.Error) {
                SnackbarController.sendEvent(
                    event = SeesturmSnackbarEvent(
                        message = "${stufe.stufenName} konnte nicht hinzugefügt werden. ${result.error.defaultMessage}",
                        type = SeesturmSnackbarType.Error,
                        onDismiss = {},
                        duration = SnackbarDuration.Short,
                        allowManualDismiss = true,
                        showInSheetIfPossible = false
                    )
                )
            }
        }
    }
    private fun removeStufe(stufe: SeesturmStufe) {

        viewModelScope.launch {

            val result = naechsteAktivitaetService.deleteStufe(stufe)

            if (result is SeesturmResult.Error) {
                SnackbarController.sendEvent(
                    event = SeesturmSnackbarEvent(
                        message = "${stufe.stufenName} konnte nicht entfernt werden. ${result.error.defaultMessage}",
                        type = SeesturmSnackbarType.Error,
                        onDismiss = {},
                        duration = SnackbarDuration.Short,
                        allowManualDismiss = true,
                        showInSheetIfPossible = false
                    )
                )
            }
        }
    }

    private fun upsertNaechsteAktivitaetState(stufe: SeesturmStufe, newState: UiState<GoogleCalendarEvent?>) {
        _state.update {
            it.copy(
                naechsteAktivitaetState = it.naechsteAktivitaetState + (stufe to newState)
            )
        }
    }

    suspend fun fetchEvents(isPullToRefresh: Boolean) {

        if (!isPullToRefresh) {
            _state.update {
                it.copy(
                    anlaesseResult = UiState.Loading
                )
            }
        }
        when (val response = anlaesseService.fetchNextThreeEvents(calendar)) {
            is SeesturmResult.Error -> {
                _state.update {
                    it.copy(
                        anlaesseResult = UiState.Error("Die nächsten Anlässe konnten nicht geladen werden. ${response.error.defaultMessage}")
                    )
                }
            }
            is SeesturmResult.Success -> {
                _state.update {
                    it.copy(
                        anlaesseResult = UiState.Success(response.data)
                    )
                }
            }
        }
    }

    suspend fun fetchPost(isPullToRefresh: Boolean) {

        if (!isPullToRefresh) {
            _state.update {
                it.copy(
                    aktuellResult = UiState.Loading
                )
            }
        }
        when (val response = aktuellService.fetchLatestPost()) {
            is SeesturmResult.Error -> {
                _state.update {
                    it.copy(
                        aktuellResult = UiState.Error("Der neuste Post konnte nicht geladen werden. ${response.error.defaultMessage}")
                    )
                }
            }
            is SeesturmResult.Success -> {
                _state.update {
                    it.copy(
                        aktuellResult = UiState.Success(response.data)
                    )
                }
            }
        }
    }

    suspend fun fetchWeather(isPullToRefresh: Boolean) {

        if (!isPullToRefresh) {
            _state.update {
                it.copy(
                    weatherResult = UiState.Loading
                )
            }
        }
        when (val response = weatherService.getWeather()) {
            is SeesturmResult.Error -> {
                _state.update { 
                    it.copy(
                        weatherResult = UiState.Error("Das Wetter vom nächsten Samstag konnte nicht geladen werden. ${response.error.defaultMessage}")
                    )
                }
            }
            is SeesturmResult.Success -> {
                _state.update {
                    it.copy(
                        weatherResult = UiState.Success(response.data)
                    )
                }
            }
        }
    }

    private fun updateRefreshingState(isRefreshing: Boolean) {
        _state.update {
            it.copy(
                refreshing = isRefreshing
            )
        }
    }
}