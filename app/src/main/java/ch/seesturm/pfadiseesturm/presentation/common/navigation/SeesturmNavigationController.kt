package ch.seesturm.pfadiseesturm.presentation.common.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow

object SeesturmNavigationController {

    private val _tabEvents = Channel<AppDestination.MainTabView.Destinations>()
    val tabEvents = _tabEvents.receiveAsFlow()

    private val _homeEvents = Channel<AppDestination.MainTabView.Destinations.Home.Destinations>()
    val homeEvents = _homeEvents.receiveAsFlow()

    private val _aktuellEvents = Channel<AppDestination.MainTabView.Destinations.Aktuell.Destinations>()
    val aktuellEvents = _aktuellEvents.receiveAsFlow()

    private val _anlaesseEvents = Channel<AppDestination.MainTabView.Destinations.Anlaesse.Destinations>()
    val anlaesseEvents = _anlaesseEvents.receiveAsFlow()

    private val _mehrEvents = Channel<AppDestination.MainTabView.Destinations.Mehr.Destinations>()
    val mehrEvents = _mehrEvents.receiveAsFlow()

    private val _accountEvents = Channel<AppDestination.MainTabView.Destinations.Account.Destinations>()
    val accountEvents = _accountEvents.receiveAsFlow()

    suspend fun changeTab(tab: AppDestination.MainTabView.Destinations) {
        _tabEvents.send(tab)
    }
    suspend fun navigateInHome(destination: AppDestination.MainTabView.Destinations.Home.Destinations) {
        _homeEvents.send(destination)
    }
    suspend fun navigateInAktuell(destination: AppDestination.MainTabView.Destinations.Aktuell.Destinations) {
        _aktuellEvents.send(destination)
    }
    suspend fun navigateInAnlaesse(destination: AppDestination.MainTabView.Destinations.Anlaesse.Destinations) {
        _anlaesseEvents.send(destination)
    }
    suspend fun navigateInMehr(destination: AppDestination.MainTabView.Destinations.Mehr.Destinations) {
        _mehrEvents.send(destination)
    }
    suspend fun navigateInAccount(destination: AppDestination.MainTabView.Destinations.Account.Destinations) {
        _accountEvents.send(destination)
    }
}