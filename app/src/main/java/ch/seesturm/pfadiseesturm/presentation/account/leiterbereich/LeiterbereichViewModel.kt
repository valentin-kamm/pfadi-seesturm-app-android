package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.data.firestore.dto.FoodOrderDto
import ch.seesturm.pfadiseesturm.data.firestore.dto.toFoodOrder
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.account.service.LeiterbereichService
import ch.seesturm.pfadiseesturm.presentation.common.BottomSheetContent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarType
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeiterbereichViewModel(
    private val service: LeiterbereichService,
    private val calendar: SeesturmCalendar,
    private val userId: String,
    private val updateSheetContent: (BottomSheetContent?) -> Unit
): ViewModel() {

    private val _state = MutableStateFlow(LeiterbereichState.create(
        onFoodItemValueChanged = { newValue ->
            updateFoodItem(newValue)
        }
    ))
    val state = _state.asStateFlow()

    init {
        fetchEvents()
        observeUsersAndFoodOrders()
        startListeningToSelectedStufen()
    }

    val stufenDropdownText: String
        get() = when (val stufenState = _state.value.selectedStufen) {
            is UiState.Success -> {
                when (stufenState.data.size) {
                    0 -> "Wählen"
                    1 -> stufenState.data.first().stufenName
                    4 -> "Alle"
                    else -> "Mehrere"
                }
            }
            else -> {"Stufe"}
        }

    private val newFoodOrder: FoodOrderDto
        get() = FoodOrderDto(
            itemDescription = state.value.foodItemState.text.trim(),
            userIds = generateSequence { userId }.take(state.value.newFoodItemCount).toList()
        )
    private val newFoodOrderCanBeSaved: Boolean
        get() = state.value.foodItemState.state.isSuccess

    private fun updateFoodItem(newValue: String) {
        _state.update {
            it.copy(
                foodItemState = state.value.foodItemState.copy(
                    text = newValue,
                    state = SeesturmBinaryUiState.Success(Unit)
                )
            )
        }
    }
    fun updateFoodItemCount(newValue: Int) {
        _state.update {
            it.copy(
                newFoodItemCount = newValue
            )
        }
    }

    fun updateSignOutAlertVisibility(isVisible: Boolean) {
        _state.update {
            it.copy(
                showSignOutAlert = isVisible
            )
        }
    }
    fun updateDeleteAccountAlertVisibility(isVisible: Boolean) {
        _state.update {
            it.copy(
                showDeleteAccountAlert = isVisible
            )
        }
    }
    fun updateDeleteAllOrdersAlertVisibility(isVisible: Boolean) {
        _state.update {
            it.copy(
                showDeleteAllOrdersAlert = isVisible
            )
        }
    }

    fun fetchEvents() {
        _state.update {
            it.copy(
                termineResult = UiState.Loading
            )
        }
        viewModelScope.launch {
            when (val result = service.fetchNext3Events(calendar)) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            termineResult = UiState.Error("Termine konnten nicht geladen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            termineResult = UiState.Success(result.data)
                        )
                    }
                }
            }
        }
    }

    private fun observeUsersAndFoodOrders() {
        _state.update {
            it.copy(
                foodResult = UiState.Loading,
                usersResult = UiState.Loading
            )
        }
        viewModelScope.launch {
            service.observeUsers()
                .combine(service.observeFoodOrders()) { usersResult, foodResult ->
                    // update users mutable state flow separately
                    when (usersResult) {
                        is SeesturmResult.Error -> {
                            _state.update {
                                it.copy(
                                    usersResult = UiState.Error("Benutzer konnten nicht geladen werden. ${usersResult.error.defaultMessage}")
                                )
                            }
                        }
                        is SeesturmResult.Success -> {
                            _state.update {
                                it.copy(
                                    usersResult = UiState.Success(usersResult.data)
                                )
                            }
                        }
                    }
                    // combine the two flows
                    when {
                        usersResult is SeesturmResult.Error -> {
                            UiState.Error(message = "Benutzer konnten nicht geladen werden. ${usersResult.error.defaultMessage}")
                        }
                        foodResult is SeesturmResult.Error -> {
                            UiState.Error(message = "Bestellungen konnten nicht geladen werden. ${foodResult.error.defaultMessage}")
                        }
                        usersResult is SeesturmResult.Success && foodResult is SeesturmResult.Success -> {
                            val foodOrders = foodResult.data.map { it.toFoodOrder(usersResult.data) }
                            UiState.Success(data = foodOrders)
                        }
                        else -> {
                            UiState.Error(message = "Bestellungen konnten nicht geladen werden. Ein unbekannter Fehler ist aufgetreten.")
                        }
                    }
                }
                .collect { result ->
                    _state.update {
                        it.copy(
                            foodResult = result
                        )
                    }
                }
        }
    }

    private fun startListeningToSelectedStufen() {
        _state.update {
            it.copy(
                selectedStufen = UiState.Loading
            )
        }
        service.readSelectedStufen().onEach { result ->
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            selectedStufen = UiState.Error("Die gewählten Stufen konnten nicht gelesen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            selectedStufen = UiState.Success(result.data)
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun toggleStufe(stufe: SeesturmStufe) {
        when (val localState = _state.value.selectedStufen) {
            is UiState.Success -> {
                if (localState.data.contains(stufe)) {
                    removeStufe(stufe)
                }
                else {
                    addStufe(stufe)
                }
            }
            else -> {
                return
            }
        }
    }
    private fun addStufe(stufe: SeesturmStufe) {
        viewModelScope.launch {
            when (val result = service.addStufe(stufe)) {
                is SeesturmResult.Error -> {
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = "${stufe.stufenName} konnte nicht hinzugefügt werden. ${result.error.defaultMessage}",
                            type = SnackbarType.Error,
                            onDismiss = {},
                            duration = SnackbarDuration.Short,
                            allowManualDismiss = true,
                            showInSheetIfPossible = false
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    // do nothing
                }
            }
        }
    }
    private fun removeStufe(stufe: SeesturmStufe) {
        viewModelScope.launch {
            when (val result = service.deleteStufe(stufe)) {
                is SeesturmResult.Error -> {
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = "${stufe.stufenName} konnte nicht entfernt werden. ${result.error.defaultMessage}",
                            type = SnackbarType.Error,
                            onDismiss = {},
                            duration = SnackbarDuration.Short,
                            allowManualDismiss = true,
                            showInSheetIfPossible = false
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    // do nothing
                }
            }
        }
    }

    fun addNewFoodOrder() {
        viewModelScope.launch {
            validateNewFoodOrder()
            if (newFoodOrderCanBeSaved) {
                _state.update {
                    it.copy(
                        addNewOrderState = ActionState.Loading(Unit)
                    )
                }
                when (val result = service.addNewFoodOrder(newFoodOrder)) {
                    is SeesturmResult.Error -> {
                        val message = "Die Bestellung konnte nicht gespeichert werden. ${result.error.defaultMessage}"
                        _state.update {
                            it.copy(
                                addNewOrderState = ActionState.Error(Unit, message)
                            )
                        }
                        SnackbarController.sendEvent(
                            event = SeesturmSnackbarEvent(
                                message = message,
                                type = SnackbarType.Error,
                                onDismiss = {
                                    _state.update {
                                        it.copy(
                                            addNewOrderState = ActionState.Idle
                                        )
                                    }
                                },
                                duration = SnackbarDuration.Long,
                                allowManualDismiss = true,
                                showInSheetIfPossible = true
                            )
                        )
                    }
                    is SeesturmResult.Success -> {
                        val message = "Bestellung erfolgreich gespeichert."
                        updateSheetContent(null)
                        _state.update {
                            it.copy(
                                addNewOrderState = ActionState.Success(Unit, message)
                            )
                        }
                        SnackbarController.sendEvent(
                            event = SeesturmSnackbarEvent(
                                message = message,
                                type = SnackbarType.Success,
                                onDismiss = {
                                    _state.update {
                                        it.copy(
                                            addNewOrderState = ActionState.Idle
                                        )
                                    }
                                },
                                duration = SnackbarDuration.Short,
                                allowManualDismiss = true,
                                showInSheetIfPossible = false
                            )
                        )
                        updateFoodItem("")
                        updateFoodItemCount(1)
                    }
                }
            }
        }
    }
    fun deleteFromExistingOrder(orderId: String) {
        viewModelScope.launch {
            when (val result = service.deleteFromExistingOrder(userId = userId, orderId = orderId)) {
                is SeesturmResult.Error -> {
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = "Beim Bearbeiten der Bestellung ist ein Fehler aufgetreten. ${result.error.defaultMessage}",
                            duration = SnackbarDuration.Indefinite,
                            type = SnackbarType.Error,
                            allowManualDismiss = true,
                            onDismiss = {},
                            showInSheetIfPossible = true
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    // do nothing
                }
            }
        }
    }
    fun addToExistingOrder(orderId: String) {
        viewModelScope.launch {
            when (val result = service.addToExistingOrder(userId = userId, orderId = orderId)) {
                is SeesturmResult.Error -> {
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = "Beim Bearbeiten der Bestellung ist ein Fehler aufgetreten. ${result.error.defaultMessage}",
                            duration = SnackbarDuration.Indefinite,
                            type = SnackbarType.Error,
                            allowManualDismiss = true,
                            onDismiss = {},
                            showInSheetIfPossible = true
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    // do nothing
                }
            }
        }
    }
    fun deleteAllOrders() {
        when (val localState = state.value.foodResult) {
            is UiState.Success -> {
                _state.update {
                    it.copy(
                        deleteAllOrdersState = ActionState.Loading(Unit)
                    )
                }
                viewModelScope.launch {
                    when (val result = service.deleteAllOrders(localState.data)) {
                        is SeesturmResult.Error -> {
                            val message = "Bestellungen konnte nicht gelöscht werden. ${result.error.defaultMessage}"
                            _state.update {
                                it.copy(
                                    deleteAllOrdersState = ActionState.Error(Unit, message)
                                )
                            }
                            SnackbarController.sendEvent(
                                event = SeesturmSnackbarEvent(
                                    message = message,
                                    duration = SnackbarDuration.Long,
                                    type = SnackbarType.Error,
                                    allowManualDismiss = true,
                                    onDismiss = {
                                        _state.update {
                                            it.copy(
                                                deleteAllOrdersState = ActionState.Idle
                                            )
                                        }
                                    },
                                    showInSheetIfPossible = false
                                )
                            )
                        }
                        is SeesturmResult.Success -> {
                            val message = "Bestellungen erfolgreich gelöscht"
                            _state.update {
                                it.copy(
                                    deleteAllOrdersState = ActionState.Success(Unit, message)
                                )
                            }
                            SnackbarController.sendEvent(
                                event = SeesturmSnackbarEvent(
                                    message = message,
                                    duration = SnackbarDuration.Long,
                                    type = SnackbarType.Success,
                                    allowManualDismiss = true,
                                    onDismiss = {
                                        _state.update {
                                            it.copy(
                                                deleteAllOrdersState = ActionState.Idle
                                            )
                                        }
                                    },
                                    showInSheetIfPossible = false
                                )
                            )
                        }
                    }
                }
            }
            else -> {
                return
            }
        }
    }
    private fun validateNewFoodOrder() {
        if (newFoodOrder.itemDescription.isEmpty()) {
            _state.update {
                it.copy(
                    foodItemState = _state.value.foodItemState.copy(
                        state = SeesturmBinaryUiState.Error("Eingabe ungültig")
                    )
                )
            }
        }
    }
}