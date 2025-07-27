package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.data.firestore.dto.FoodOrderDto
import ch.seesturm.pfadiseesturm.data.firestore.dto.toFoodOrder
import ch.seesturm.pfadiseesturm.data.firestore.dto.toSchoepflialarm
import ch.seesturm.pfadiseesturm.domain.account.service.LeiterbereichService
import ch.seesturm.pfadiseesturm.domain.account.service.SchoepflialarmMessageType
import ch.seesturm.pfadiseesturm.domain.account.service.SchoepflialarmService
import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.domain.fcm.service.FCMService
import ch.seesturm.pfadiseesturm.domain.firestore.model.Schoepflialarm
import ch.seesturm.pfadiseesturm.presentation.common.BottomSheetContent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarType
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.SchoepflialarmError
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.SchoepflialarmReactionType
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeiterbereichViewModel(
    private val leiterbereichService: LeiterbereichService,
    private val schoepflialarmService: SchoepflialarmService,
    private val fcmService: FCMService,
    private val userId: String,
    private val userDisplayNameShort: String,
    private val calendar: SeesturmCalendar,
    private val updateSheetContent: (BottomSheetContent?) -> Unit
): ViewModel() {

    private val _state = MutableStateFlow(LeiterbereichState.create(
        onFoodItemValueChanged = { newValue ->
            updateFoodItem(newValue)
        },
        onSchoepflialarmValueChanged = { newValue ->
            updateSchoepflialarmMessage(newValue)
        }
    ))
    val state = _state.asStateFlow()

    init {
        fetchNext3Events()
        observeUsersAndFoodOrders()
        startListeningToSelectedStufen()
        observeSchoepflialarm()
        observeSchoepflialarmReactions()
        getSubscribedTopics()
        showPushNotificationSnackbars()
    }

    val schoepflialarmResult: UiState<Schoepflialarm>
        get() {

            val genericErrorMessage = "Der letzte Schöpflialarm konnte nicht geladen werden."

            return when (val localUsersState = state.value.usersResult) {

                UiState.Loading -> UiState.Loading
                is UiState.Error -> UiState.Error(genericErrorMessage + " " + localUsersState.message)
                is UiState.Success -> {

                    when (val localSchoepflialarmState = state.value.schoepflialarmResultDto) {

                        UiState.Loading -> UiState.Loading
                        is UiState.Error -> UiState.Error(genericErrorMessage + " " + localSchoepflialarmState.message)
                        is UiState.Success -> {

                            when (val localReactionsState = state.value.schoepflialarmReactionsResultDto) {

                                UiState.Loading -> UiState.Loading
                                is UiState.Error -> UiState.Error(genericErrorMessage + " " + localReactionsState.message)
                                is UiState.Success -> {
                                    try {
                                        val schoepflialarm = localSchoepflialarmState.data.toSchoepflialarm(
                                            users = localUsersState.data,
                                            reactions = localReactionsState.data
                                        )
                                        UiState.Success(schoepflialarm)
                                    }
                                    catch (e: Exception) {
                                        UiState.Error(genericErrorMessage + " " + e.message)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    private val schoepflialarmMessageType: SchoepflialarmMessageType
        get() {
            val message = state.value.schoepflialarmMessage.text.trim()
            return when (message.isEmpty()) {
                true -> SchoepflialarmMessageType.Generic
                false -> SchoepflialarmMessageType.Custom(message)
            }
        }
    val schoepflialarmConfirmationText: String
        get() = when (schoepflialarmMessageType) {
            SchoepflialarmMessageType.Generic -> {
                "Der Schöpflialarm wird ohne Nachricht gesendet."
            }
            is SchoepflialarmMessageType.Custom -> {
                "Möchtest du den Schöpflialarm wirklich senden?"
            }
        }

    private val newFoodOrder: FoodOrderDto
        get() = FoodOrderDto(
            itemDescription = state.value.foodItemState.text.trim(),
            userIds = generateSequence { userId }.take(state.value.newFoodItemCount).toList()
        )
    private val newFoodOrderCanBeSaved: Boolean
        get() = state.value.foodItemState.state.isSuccess

    private fun updateFoodItem(newDescription: String) {
        _state.update {
            it.copy(
                foodItemState = state.value.foodItemState.copy(
                    text = newDescription,
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

    fun fetchNext3Events() {
        _state.update {
            it.copy(
                termineState = UiState.Loading
            )
        }
        viewModelScope.launch {
            when (val result = leiterbereichService.fetchNext3Events(calendar)) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            termineState = UiState.Error("Die nächsten Termine konnten nicht geladen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            termineState = UiState.Success(result.data)
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
            delay(100)
            leiterbereichService.observeUsers()
                .combine(leiterbereichService.observeFoodOrders()) { usersResult, foodResult ->
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
                            UiState.Loading
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
        leiterbereichService.readSelectedStufen().onEach { result ->
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            selectedStufen = UiState.Error("Die gespeicherten Stufen konnten nicht gelesen werden. ${result.error.defaultMessage}")
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

            val result = leiterbereichService.addStufe(stufe)

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

            val result = leiterbereichService.deleteStufe(stufe)

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

    fun addNewFoodOrder() {

        validateNewFoodOrder()
        if (!newFoodOrderCanBeSaved) {
            return
        }

        _state.update {
            it.copy(
                addNewOrderState = ActionState.Loading(Unit)
            )
        }

        viewModelScope.launch {
            when (val result = leiterbereichService.addNewFoodOrder(newFoodOrder)) {
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
                            type = SeesturmSnackbarType.Error,
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
                            type = SeesturmSnackbarType.Success,
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
    fun deleteFromExistingOrder(orderId: String) {
        viewModelScope.launch {

            val result = leiterbereichService.deleteFromExistingOrder(userId = userId, orderId = orderId)

            if (result is SeesturmResult.Error) {
                SnackbarController.sendEvent(
                    event = SeesturmSnackbarEvent(
                        message = "Beim Entfernen der Bestellung ist ein Fehler aufgetreten. ${result.error.defaultMessage}",
                        duration = SnackbarDuration.Indefinite,
                        type = SeesturmSnackbarType.Error,
                        allowManualDismiss = true,
                        onDismiss = {},
                        showInSheetIfPossible = true
                    )
                )
            }
        }
    }
    fun addToExistingOrder(orderId: String) {
        viewModelScope.launch {

            val result = leiterbereichService.addToExistingOrder(userId = userId, orderId = orderId)

            if (result is SeesturmResult.Error) {
                SnackbarController.sendEvent(
                    event = SeesturmSnackbarEvent(
                        message = "Beim Hinzufügen der Bestellung ist ein Fehler aufgetreten. ${result.error.defaultMessage}",
                        duration = SnackbarDuration.Indefinite,
                        type = SeesturmSnackbarType.Error,
                        allowManualDismiss = true,
                        onDismiss = {},
                        showInSheetIfPossible = true
                    )
                )
            }
        }
    }
    fun deleteAllOrders() {

        val localState = state.value.foodResult

        if (localState !is UiState.Success) {
            return
        }

        _state.update {
            it.copy(
                deleteAllOrdersState = ActionState.Loading(Unit)
            )
        }
        viewModelScope.launch {
            when (val result = leiterbereichService.deleteAllOrders(localState.data)) {
                is SeesturmResult.Error -> {
                    val message = "Bestellungen konnten nicht gelöscht werden. ${result.error.defaultMessage}"
                    _state.update {
                        it.copy(
                            deleteAllOrdersState = ActionState.Error(Unit, message)
                        )
                    }
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Long,
                            type = SeesturmSnackbarType.Error,
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
                            type = SeesturmSnackbarType.Success,
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

    private fun observeSchoepflialarm() {

        _state.update {
            it.copy(
                schoepflialarmResultDto = UiState.Loading
            )
        }
        schoepflialarmService.observeSchoepflialarm().onEach { result ->
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            schoepflialarmResultDto = UiState.Error("Der letzte Schöpflialarm konnte nicht geladen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            schoepflialarmResultDto = UiState.Success(result.data)
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun observeSchoepflialarmReactions() {

        _state.update {
            it.copy(
                schoepflialarmReactionsResultDto = UiState.Loading
            )
        }
        schoepflialarmService.observeSchoepflialarmReactions().onEach { result ->
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            schoepflialarmReactionsResultDto = UiState.Error("Der letzte Schöpflialarm konnte nicht geladen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            schoepflialarmReactionsResultDto = UiState.Success(result.data)
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun trySendSchoepflialarm() {
        updateConfirmSchoepflialarmAlertVisibility(true)
    }

    fun sendSchoepflialarm(
        requestMessagingPermission: suspend () -> Boolean,
        requestLocationPermission: suspend () -> Boolean
    ) {

        _state.update {
            it.copy(
                sendSchoepflialarmState = ActionState.Loading(Unit)
            )
        }
        viewModelScope.launch {
            when (
                val result = schoepflialarmService.sendSchoepflialarm(
                    messageType = schoepflialarmMessageType,
                    userId = userId,
                    userDisplayNameShort = userDisplayNameShort,
                    requestMessagingPermission = requestMessagingPermission,
                    requestLocationPermission = requestLocationPermission
                )
            ) {
                is SeesturmResult.Error -> {
                    when (result.error) {
                        SchoepflialarmError.LocationPermissionMissing -> {
                            _state.update {
                                it.copy(
                                    sendSchoepflialarmState = ActionState.Idle,
                                    showLocationSettingsAlert = true
                                )
                            }
                        }
                        SchoepflialarmError.MessagingPermissionMissing -> {
                            _state.update {
                                it.copy(
                                    sendSchoepflialarmState = ActionState.Idle,
                                    showNotificationSettingsAlert = true
                                )
                            }
                        }
                        else -> {
                            _state.update {
                                it.copy(
                                    sendSchoepflialarmState = ActionState.Error(Unit, result.error.defaultMessage)
                                )
                            }
                            SnackbarController.sendEvent(
                                event = SeesturmSnackbarEvent(
                                    message = result.error.defaultMessage,
                                    duration = SnackbarDuration.Long,
                                    type = SeesturmSnackbarType.Error,
                                    allowManualDismiss = true,
                                    showInSheetIfPossible = true,
                                    onDismiss = {
                                        _state.update {
                                            it.copy(
                                                sendSchoepflialarmState = ActionState.Idle
                                            )
                                        }
                                    }
                                )
                            )
                        }
                    }
                }
                is SeesturmResult.Success -> {
                    updateSchoepflialarmMessage("")
                    val message = "Schöpflialarm erfolgreich gesendet"
                    _state.update {
                        it.copy(
                            sendSchoepflialarmState = ActionState.Success(Unit, message)
                        )
                    }
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Long,
                            type = SeesturmSnackbarType.Success,
                            allowManualDismiss = true,
                            showInSheetIfPossible = true,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        sendSchoepflialarmState = ActionState.Idle
                                    )
                                }
                            }
                        )
                    )
                }
            }
        }
    }
    fun sendSchoepflialarmReaction(reaction: SchoepflialarmReactionType) {

        _state.update {
            it.copy(
                sendSchoepflialarmReactionState = ActionState.Loading(reaction)
            )
        }
        viewModelScope.launch {
            when (
                val result = schoepflialarmService.sendSchoepflialarmReaction(
                    userId = userId,
                    userDisplayNameShort = userDisplayNameShort,
                    reaction = reaction
                )
            ) {
                is SeesturmResult.Error -> {
                    val message = "Beim Senden der Reaktion ist ein Fehler aufgetreten. ${result.error.defaultMessage}"
                    _state.update {
                        it.copy(
                            sendSchoepflialarmReactionState = ActionState.Error(reaction, message)
                        )
                    }
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Long,
                            type = SeesturmSnackbarType.Error,
                            allowManualDismiss = true,
                            showInSheetIfPossible = true,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        sendSchoepflialarmReactionState = ActionState.Idle
                                    )
                                }
                            }
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    val message = "Reaktion erfolgreich gesendet"
                    _state.update {
                        it.copy(
                            sendSchoepflialarmReactionState = ActionState.Success(reaction, message)
                        )
                    }
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Long,
                            type = SeesturmSnackbarType.Success,
                            allowManualDismiss = true,
                            showInSheetIfPossible = true,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        sendSchoepflialarmReactionState = ActionState.Idle
                                    )
                                }
                            }
                        )
                    )
                }
            }
        }
    }
    fun updateConfirmSchoepflialarmAlertVisibility(isVisible: Boolean) {
        _state.update {
            it.copy(
                showConfirmSchoepflialarmAlert = isVisible
            )
        }
    }
    fun updateNotificationSettingsAlert(isVisible: Boolean) {
        _state.update {
            it.copy(
                showNotificationSettingsAlert = isVisible
            )
        }
    }
    fun updateLocationSettingsAlert(isVisible: Boolean) {
        _state.update {
            it.copy(
                showLocationSettingsAlert = isVisible
            )
        }
    }
    private fun updateSchoepflialarmMessage(newMessage: String) {
        _state.update {
            it.copy(
                schoepflialarmMessage = it.schoepflialarmMessage.copy(
                    text = newMessage
                )
            )
        }
    }

    private fun getSubscribedTopics() {
        _state.update {
            it.copy(
                notificationTopicsReadingState = UiState.Loading
            )
        }
        fcmService.readSubscribedTopics().onEach { result ->
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            notificationTopicsReadingState = UiState.Error(result.error.defaultMessage)
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            notificationTopicsReadingState = UiState.Success(result.data)
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun toggleNotificationTopic(
        isSwitchingOn: Boolean,
        requestPermission: suspend () -> Boolean
    ) {
        val topic = SeesturmFCMNotificationTopic.SchoepflialarmReaction
        _state.update {
            it.copy(
                toggleSchoepflialarmReactionsPushNotificationState = ActionState.Loading(topic)
            )
        }
        viewModelScope.launch {
            if (isSwitchingOn) {
                subscribe(
                    topic = topic,
                    requestPermission = requestPermission
                )
            }
            else {
                unsubscribe(topic)
            }
        }
    }

    private suspend fun subscribe(
        topic: SeesturmFCMNotificationTopic,
        requestPermission: suspend () -> Boolean
    ) {
        when (val result = fcmService.subscribe(topic, requestPermission)) {
            is SeesturmResult.Error -> {
                when (result.error) {
                    DataError.Messaging.PERMISSION_ERROR -> {
                        _state.update {
                            it.copy(
                                toggleSchoepflialarmReactionsPushNotificationState = ActionState.Idle,
                                showNotificationSettingsAlert = true
                            )
                        }
                    }
                    else -> {
                        _state.update {
                            it.copy(
                                toggleSchoepflialarmReactionsPushNotificationState = ActionState.Error(topic, result.error.defaultMessage)
                            )
                        }
                    }
                }
            }
            is SeesturmResult.Success -> {
                _state.update {
                    it.copy(
                        toggleSchoepflialarmReactionsPushNotificationState = ActionState.Success(
                            action = topic,
                            message = "Anmeldung für ${topic.topicName} erfolgreich."
                        )
                    )
                }
            }
        }
    }

    private suspend fun unsubscribe(
        topic: SeesturmFCMNotificationTopic
    ) {
        when (val result = fcmService.unsubscribe(topic)) {
            is SeesturmResult.Error -> {
                _state.update {
                    it.copy(
                        toggleSchoepflialarmReactionsPushNotificationState = ActionState.Error(topic, result.error.defaultMessage)
                    )
                }
            }
            is SeesturmResult.Success -> {
                _state.update {
                    it.copy(
                        toggleSchoepflialarmReactionsPushNotificationState = ActionState.Success(topic, "Abmeldung von ${topic.topicName} erfolgreich.")
                    )
                }
            }
        }
    }

    private fun showPushNotificationSnackbars() {
        viewModelScope.launch {
            state.map { it.toggleSchoepflialarmReactionsPushNotificationState }
                .distinctUntilChanged()
                .collect { new ->
                    when (new) {
                        is ActionState.Error -> {
                            SnackbarController.sendEvent(
                                event = SeesturmSnackbarEvent(
                                    message = new.message,
                                    duration = SnackbarDuration.Short,
                                    type = SeesturmSnackbarType.Error,
                                    allowManualDismiss = true,
                                    onDismiss = {
                                        _state.update {
                                            it.copy(
                                                toggleSchoepflialarmReactionsPushNotificationState = ActionState.Idle
                                            )
                                        }
                                    },
                                    showInSheetIfPossible = true
                                )
                            )
                        }
                        is ActionState.Success -> {
                            SnackbarController.sendEvent(
                                event = SeesturmSnackbarEvent(
                                    message = new.message,
                                    duration = SnackbarDuration.Short,
                                    type = SeesturmSnackbarType.Success,
                                    allowManualDismiss = true,
                                    onDismiss = {
                                        _state.update {
                                            it.copy(
                                                toggleSchoepflialarmReactionsPushNotificationState = ActionState.Idle
                                            )
                                        }
                                    },
                                    showInSheetIfPossible = true
                                )
                            )
                        }
                        else -> {
                            // do nothing
                        }
                    }
                }
        }
    }

    fun requestNotificationPermissionIfNecessary(requestPermission: suspend () -> Boolean) {
        viewModelScope.launch {
            try {
                fcmService.requestOrCheckNotificationPermission(requestPermission)
            }
            catch (_: Exception) {
                return@launch
            }
        }
    }
}