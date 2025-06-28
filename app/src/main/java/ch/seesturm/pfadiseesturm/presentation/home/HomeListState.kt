package ch.seesturm.pfadiseesturm.presentation.home

import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.model.Weather
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPost
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.state.UiState

data class HomeListState(
    val naechsteAktivitaetState: Map<SeesturmStufe, UiState<GoogleCalendarEvent?>> = emptyMap(),
    val selectedStufen: UiState<Set<SeesturmStufe>> = UiState.Loading,
    val aktuellResult: UiState<WordpressPost> = UiState.Loading,
    val anlaesseResult: UiState<List<GoogleCalendarEvent>> = UiState.Loading,
    val weatherResult: UiState<Weather> = UiState.Loading,
    val refreshing: Boolean = false
)