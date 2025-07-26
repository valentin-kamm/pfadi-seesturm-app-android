package ch.seesturm.pfadiseesturm.presentation.anlaesse.detail

import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.util.state.UiState

data class AnlaesseDetailState(
    val eventState: UiState<GoogleCalendarEvent> = UiState.Loading,
)
