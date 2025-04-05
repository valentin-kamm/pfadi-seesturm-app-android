package ch.seesturm.pfadiseesturm.presentation.home

import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.model.Weather
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPost
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.state.SeesturmState

data class HomeListState(
    val naechsteAktivitaetState: Map<SeesturmStufe, UiState<GoogleCalendarEvent?>> = emptyMap(),
    val selectedStufen: UiState<Set<SeesturmStufe>> = UiState.Loading,
    val aktuellResult: UiState<WordpressPost> = UiState.Loading,
    val anlaesseResult: UiState<List<GoogleCalendarEvent>> = UiState.Loading,
    val weatherResult: UiState<Weather> = UiState.Loading,
    val refreshing: Boolean = false
)
/*
data class HomeNaechsteAktivitaet(
    val stufe: SeesturmStufe,
    val naechsteAktivitaet: UiState<GoogleCalendarEvent?>
)

 */