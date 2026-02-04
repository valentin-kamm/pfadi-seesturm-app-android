package ch.seesturm.pfadiseesturm.presentation.common.event_management

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.templates.TemplateListView
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.templates.TemplateListViewMode
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.TopBarNavigationIcon
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.alert.SimpleAlert
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonColor
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventManagementMode
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventToManageType
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListFooter
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeaderMode
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumn
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumnItemTrailingContentType
import ch.seesturm.pfadiseesturm.presentation.common.picker.SeesturmDatePicker
import ch.seesturm.pfadiseesturm.presentation.common.picker.SeesturmTimePicker
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.SeesturmHTMLEditor
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.SeesturmRichTextState
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ModalBottomSheetKeyboardResponse
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ModalBottomSheetWithItem
import ch.seesturm.pfadiseesturm.presentation.common.sheet.SheetDetents
import ch.seesturm.pfadiseesturm.presentation.common.sheet.SheetScaffoldType
import ch.seesturm.pfadiseesturm.presentation.common.sheet.SimpleModalBottomSheet
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextField
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.Binding
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageEventView(
    viewModel: ManageEventViewModel,
    appStateViewModel: AppStateViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavHostController,
    onNavigateToTemplates: ((SeesturmStufe) -> Unit)?,
    modifier: Modifier = Modifier
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val appState by appStateViewModel.state.collectAsStateWithLifecycle()

    val isStartDatePickerShown = rememberSaveable { mutableStateOf(false) }
    val isStartTimePickerShown = rememberSaveable { mutableStateOf(false) }
    val isEndDatePickerShown = rememberSaveable { mutableStateOf(false) }
    val isEndTimePickerShown = rememberSaveable { mutableStateOf(false) }

    SimpleAlert(
        isShown = state.showConfirmationDialog,
        title = viewModel.confirmationDialogTitle,
        description = viewModel.confirmationDialogDescription,
        icon = viewModel.confirmationDialogIcon,
        confirmButtonText = viewModel.confirmationDialogButtonText,
        onConfirm = {
            viewModel.submit()
        },
        onDismiss = {
            viewModel.updateConfirmationDialogVisibility(false)
        },
        isConfirmButtonCritical = true
    )

    SeesturmDatePicker(
        datePickerState = state.startDatePickerState,
        isShown = isStartDatePickerShown.value,
        onDismiss = { isStartDatePickerShown.value = false },
        onConfirm = { year, month, dayOfMonth ->
            viewModel.updateStartDate(year, month, dayOfMonth)
        }
    )
    SeesturmTimePicker(
        state = state.startTimePickerState,
        isShown = isStartTimePickerShown.value,
        onDismiss = { isStartTimePickerShown.value = false },
        onConfirm = { hour, minute ->
            viewModel.updateStartDate(hour, minute)
        }
    )
    SeesturmDatePicker(
        datePickerState = state.endDatePickerState,
        isShown = isEndDatePickerShown.value,
        onDismiss = { isEndDatePickerShown.value = false },
        onConfirm = { year, month, dayOfMonth ->
            viewModel.updateEndDate(year, month, dayOfMonth)
        }
    )
    SeesturmTimePicker(
        state = state.endTimePickerState,
        isShown = isEndTimePickerShown.value,
        onDismiss = { isEndTimePickerShown.value = false },
        onConfirm = { hour, minute ->
            viewModel.updateEndDate(hour, minute)
        }
    )

    SimpleModalBottomSheet(
        show = state.showTemplatesSheet ?: Binding.Constant(false),
        detents = SheetDetents.MediumOnly,
        type = SheetScaffoldType.Title(viewModel.eventType.navigationTitle),
        appStateViewModel = appStateViewModel,
        keyboardResponse = ModalBottomSheetKeyboardResponse.None
    ) { _, _ ->
        val ts = state.templatesState
        if (ts != null) {
            TemplateListView(
                state = ts,
                mode = TemplateListViewMode.Use,
                contentPadding = PaddingValues(16.dp),
                onClick = { template ->
                    viewModel.useTemplateIfPossible(template)
                },
                isInEditingMode = false
            )
        }
    }

    val previewSheetNavController = rememberNavController()
    val previewSheetBackStackEntry by previewSheetNavController.currentBackStackEntryAsState()

    ModalBottomSheetWithItem(
        item = state.previewSheetItem,
        detents = SheetDetents.All,
        type = SheetScaffoldType.Title(
            title = viewModel.eventPreviewType.navigationTitle,
            navigationIcon = if (previewSheetBackStackEntry?.destination?.hierarchy?.any { it.route == ManageTerminPreviewNavigationDestinations.Detail::class.qualifiedName } == true) {
                TopBarNavigationIcon.Back(
                    onNavigateBack = { previewSheetNavController.navigateUp() }
                )
            }
            else {
                TopBarNavigationIcon.None
            }
        ),
        appStateViewModel = appStateViewModel,
        keyboardResponse = ModalBottomSheetKeyboardResponse.None
    ) { previewEvent, _, _ ->
        ManageEventPreviewView(
            type = viewModel.eventPreviewType,
            event = previewEvent,
            navController = previewSheetNavController,
            isDarkTheme = appState.theme.isDarkTheme
        )
    }

    ManageEventContentView(
        eventType = viewModel.eventType,
        mode = viewModel.mode,
        navController = navController,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        eventState = state.eventState,
        publishEventState = state.publishEventState,
        start = state.start,
        end = state.end,
        isAllDay = state.isAllDay,
        location = state.location,
        title = state.title,
        description = state.description,
        eventForPreview = viewModel.eventForPreview,
        pushNotificationBinding = state.sendPushNotification,
        selectedStufen = state.selectedStufen,
        updateStartDatePickerVisibility = {
            isStartDatePickerShown.value = it
        },
        updateStartTimePickerVisibility = {
            isStartTimePickerShown.value = it
        },
        updateEndDatePickerVisibility = {
            isEndDatePickerShown.value = it
        },
        updateEndTimePickerVisibility = {
            isEndTimePickerShown.value = it
        },
        onEventRetry = {
            viewModel.fetchEventIfPossible()
        },
        onShowTemplateSheet = viewModel.onShowTemplatesSheet,
        onShowPreviewSheet = {
            viewModel.updatePreviewSheetVisibility(true)
        },
        onTrySubmit = {
            viewModel.trySubmit()
        },
        onNavigateToTemplates = onNavigateToTemplates,
        isDarkTheme = appState.theme.isDarkTheme,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
private fun ManageEventContentView(
    eventType: EventToManageType,
    mode: EventManagementMode,
    navController: NavHostController,
    bottomNavigationInnerPadding: PaddingValues,
    eventState: UiState<Unit>,
    publishEventState: ActionState<Unit>,
    start: ZonedDateTime,
    end: ZonedDateTime,
    isAllDay: Binding<Boolean>,
    location: Binding<String>,
    title: SeesturmTextFieldState,
    description: SeesturmRichTextState,
    eventForPreview: GoogleCalendarEvent?,
    pushNotificationBinding: Binding<Boolean>?,
    selectedStufen: Binding<Set<SeesturmStufe>>?,
    updateStartDatePickerVisibility: (Boolean) -> Unit,
    updateStartTimePickerVisibility: (Boolean) -> Unit,
    updateEndDatePickerVisibility: (Boolean) -> Unit,
    updateEndTimePickerVisibility: (Boolean) -> Unit,
    onEventRetry: () -> Unit,
    onShowTemplateSheet: (() -> Unit)?,
    onShowPreviewSheet: () -> Unit,
    onTrySubmit: () -> Unit,
    isDarkTheme: Boolean,
    onNavigateToTemplates: ((SeesturmStufe) -> Unit)?,
    modifier: Modifier,
    columnState: LazyListState = rememberLazyListState()
) {

    val hazeState = remember { HazeState() }

    val startDateFormatted = DateTimeUtil.shared.formatDate(
        date = start,
        format = "dd.MM.yyyy",
        type = DateFormattingType.Absolute
    )
    val startTimeFormatted = DateTimeUtil.shared.formatDate(
        date = start,
        format = "HH:mm",
        type = DateFormattingType.Absolute
    )
    val endDateFormatted = DateTimeUtil.shared.formatDate(
        date = end,
        format = "dd.MM.yyyy",
        type = DateFormattingType.Absolute
    )
    val endTimeFormatted = DateTimeUtil.shared.formatDate(
        date = end,
        format = "HH:mm",
        type = DateFormattingType.Absolute
    )

    val controlsEnabled = !publishEventState.isLoading && !eventState.isLoading

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        title = eventType.navigationTitle,
        navigationAction = TopBarNavigationIcon.Back {
            navController.navigateUp()
        },
        modifier = modifier,
        actions = {
            if (eventType is EventToManageType.Aktivitaet && onNavigateToTemplates != null) {
                TextButton(
                    onClick = { onNavigateToTemplates(eventType.stufe) }
                ) {
                    Text("Vorlagen")
                }
            }
        }
    ) { topBarInnerPadding ->

        val combinedPadding = bottomNavigationInnerPadding.intersectWith(
            other = topBarInnerPadding,
            layoutDirection = LayoutDirection.Ltr,
            additionalStartPadding = 16.dp,
            additionalEndPadding = 16.dp,
            additionalTopPadding = 16.dp,
            additionalBottomPadding = 16.dp
        )

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            GroupedColumn(
                state = columnState,
                contentPadding = combinedPadding,
                userScrollEnabled = !eventState.isLoading,
                sectionSpacing = 16.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .hazeSource(hazeState)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (eventState) {
                    is UiState.Error -> {
                        section {
                            customItem(
                                key = "ManageEventErrorItem"
                            ) {
                                ErrorCardView(
                                    errorDescription = eventState.message,
                                    retryAction = onEventRetry
                                )
                            }
                        }
                    }
                    UiState.Loading, is UiState.Success -> {
                        section(
                            header = {
                                BasicListHeader(BasicListHeaderMode.Normal("Zeit"))
                            },
                            footer = {
                                BasicListFooter(BasicListHeaderMode.Normal("Zeiten in MEZ/MESZ (CH-Zeit)"))
                            }
                        ) {
                            textItem(
                                key = "ManageEventStartDateItem",
                                text = "Start",
                                trailingContent = GroupedColumnItemTrailingContentType.Custom {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        SeesturmButton(
                                            type = SeesturmButtonType.Primary,
                                            colors = SeesturmButtonColor.Custom(
                                                buttonColor = MaterialTheme.colorScheme.secondaryContainer,
                                                contentColor = eventType.accentColor(isDarkTheme)
                                            ),
                                            title = startDateFormatted,
                                            onClick = {
                                                updateStartDatePickerVisibility(true)
                                            },
                                            enabled = controlsEnabled,
                                            modifier = Modifier
                                                .wrapContentWidth()
                                        )
                                        if (!isAllDay.get()) {
                                            SeesturmButton(
                                                type = SeesturmButtonType.Primary,
                                                colors = SeesturmButtonColor.Custom(
                                                    buttonColor = MaterialTheme.colorScheme.secondaryContainer,
                                                    contentColor = eventType.accentColor(isDarkTheme)
                                                ),
                                                title = startTimeFormatted,
                                                onClick = {
                                                    updateStartTimePickerVisibility(true)
                                                },
                                                enabled = controlsEnabled,
                                                modifier = Modifier
                                                    .wrapContentWidth()
                                            )
                                        }
                                    }
                                }
                            )
                            textItem(
                                key = "ManageEventEndDateItem",
                                text = "Ende",
                                trailingContent = GroupedColumnItemTrailingContentType.Custom {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        SeesturmButton(
                                            type = SeesturmButtonType.Primary,
                                            colors = SeesturmButtonColor.Custom(
                                                buttonColor = MaterialTheme.colorScheme.secondaryContainer,
                                                contentColor = eventType.accentColor(isDarkTheme)
                                            ),
                                            title = endDateFormatted,
                                            onClick = {
                                                updateEndDatePickerVisibility(true)
                                            },
                                            enabled = controlsEnabled,
                                            modifier = Modifier
                                                .wrapContentWidth()
                                        )
                                        if (!isAllDay.get()) {
                                            SeesturmButton(
                                                type = SeesturmButtonType.Primary,
                                                colors = SeesturmButtonColor.Custom(
                                                    buttonColor = MaterialTheme.colorScheme.secondaryContainer,
                                                    contentColor = eventType.accentColor(isDarkTheme)
                                                ),
                                                title = endTimeFormatted,
                                                onClick = {
                                                    updateEndTimePickerVisibility(true)
                                                },
                                                enabled = controlsEnabled,
                                                modifier = Modifier
                                                    .wrapContentWidth()
                                            )
                                        }
                                    }
                                }
                            )
                            textItem(
                                key = "ManageEventAllDayItem",
                                text = "Ganztägig",
                                trailingContent = GroupedColumnItemTrailingContentType.Custom {
                                    Switch(
                                        checked = isAllDay.get(),
                                        onCheckedChange = isAllDay.set,
                                        enabled = controlsEnabled,
                                        colors = SwitchDefaults.colors().copy(
                                            checkedThumbColor = MaterialTheme.colorScheme.background,
                                            checkedTrackColor = eventType.accentColor(isDarkTheme)
                                        )
                                    )
                                }
                            )
                        }

                        section(
                            footer = {
                                BasicListFooter(
                                    BasicListHeaderMode.Normal(
                                        text = when (eventType) {
                                            is EventToManageType.Aktivitaet, EventToManageType.MultipleAktivitaeten -> "Treffpunkt am Anfang der Aktivität"
                                            is EventToManageType.Termin -> "Treffpunkt am Anfang des Anlasses"
                                        }
                                    )
                                )
                            }
                        ) {
                            item(
                                key = "ManageEventPlaceItem"
                            ) {
                                SeesturmTextField(
                                    state = SeesturmTextFieldState(
                                        text = location.get(),
                                        label = "Treffpunkt",
                                        state = SeesturmBinaryUiState.Success(Unit),
                                        onValueChanged = location.set
                                    ),
                                    leadingIcon = Icons.Outlined.LocationOn,
                                    iconTint = eventType.accentColor(isDarkTheme),
                                    enabled = controlsEnabled,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                        }

                        section(
                            header = {
                                BasicListHeader(BasicListHeaderMode.Normal("Beschreibung"))
                            }
                        ) {
                            item(
                                key = "ManageEventTitleItem"
                            ) {
                                SeesturmTextField(
                                    state = title,
                                    leadingIcon = Icons.Outlined.Title,
                                    iconTint = eventType.accentColor(isDarkTheme),
                                    enabled = controlsEnabled,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                            item(
                                key = "ManageEventDescriptionItem"
                            ) {
                                SeesturmHTMLEditor(
                                    state = description,
                                    enabled = controlsEnabled,
                                    buttonTint = eventType.accentColor(isDarkTheme),
                                    placeholder = {
                                        Text("Beschreibung")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                )
                            }
                            onShowTemplateSheet?.let {
                                textItem(
                                    key = "ManageEventTemplatesButton",
                                    text = "Vorlage einfügen",
                                    onClick = if (controlsEnabled) {
                                        { it() }
                                    } else null
                                )
                            }
                        }

                        if (selectedStufen != null) {
                            section(
                                header = {
                                    BasicListHeader(
                                        mode = BasicListHeaderMode.Normal("Stufen auswählen")
                                    )
                                }
                            ) {
                                textItems(
                                    items = SeesturmStufe.entries.sortedBy { it.id },
                                    key = { stufe ->
                                        "ManageEventStufenButton${stufe.id}"
                                    },
                                    text = { stufe ->
                                        stufe.name
                                    },
                                    trailingContent = { stufe ->
                                        if (selectedStufen.get().contains(stufe)) {
                                            GroupedColumnItemTrailingContentType.Custom {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = eventType.accentColor(isDarkTheme)
                                                )
                                            }
                                        }
                                        else {
                                            GroupedColumnItemTrailingContentType.None
                                        }
                                    },
                                    onClick = if (controlsEnabled) {
                                        { stufe ->
                                            if (selectedStufen.get().contains(stufe) && selectedStufen.get().count() > 1) {
                                                selectedStufen.set(selectedStufen.get() - stufe)
                                            }
                                            else {
                                                selectedStufen.set(selectedStufen.get() + stufe)
                                            }
                                        }
                                    }
                                    else null
                                )
                            }
                        }

                        if (eventForPreview != null || pushNotificationBinding != null) {
                            section(
                                header = {
                                    BasicListHeader(BasicListHeaderMode.Normal(mode.nomen))
                                }
                            ) {
                                if (eventForPreview != null) {
                                    textItem(
                                        key = "ManageEventPreviewButton",
                                        text = "Vorschau",
                                        onClick = if (controlsEnabled) onShowPreviewSheet else null
                                    )
                                }
                                if (pushNotificationBinding != null) {
                                    textItem(
                                        key = "ManageEventSendPushNotificationItem",
                                        text = "Push-Nachricht senden",
                                        trailingContent = GroupedColumnItemTrailingContentType.Custom {
                                            Switch(
                                                checked = pushNotificationBinding.get(),
                                                onCheckedChange = pushNotificationBinding.set,
                                                enabled = controlsEnabled,
                                                colors = SwitchDefaults.colors().copy(
                                                    checkedThumbColor = MaterialTheme.colorScheme.background,
                                                    checkedTrackColor = eventType.accentColor(isDarkTheme)
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        section {
                            customItem(
                                key = "ManageEventButtonItem"
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SeesturmButton(
                                        type = SeesturmButtonType.Primary,
                                        colors = SeesturmButtonColor.Custom(
                                            buttonColor = eventType.accentColor(isDarkTheme),
                                            contentColor = eventType.onAccentColor()
                                        ),
                                        enabled = controlsEnabled,
                                        title = mode.nomen,
                                        onClick = onTrySubmit,
                                        isLoading = publishEventState.isLoading
                                    )
                                }
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = eventState.isLoading,
                enter = EnterTransition.None,
                exit = fadeOut(animationSpec = tween(150)),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (eventState.isLoading) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                            .then(
                                if (Build.VERSION.SDK_INT >= 30) {
                                    Modifier
                                        .hazeEffect(
                                            hazeState,
                                            style = CupertinoMaterials.ultraThin()
                                        )
                                        .background(Color.Transparent)
                                } else {
                                    Modifier
                                        .background(MaterialTheme.colorScheme.background)
                                }
                            )
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            16.dp,
                            alignment = Alignment.CenterVertically
                        )
                    ) {
                        CircularProgressIndicator(
                            color = eventType.accentColor(isDarkTheme),
                            modifier = Modifier
                                .size(32.dp)
                        )
                        Text(
                            text = when (eventType) {
                                is EventToManageType.Aktivitaet -> "${eventType.stufe.aktivitaetDescription} wird geladen..."
                                EventToManageType.MultipleAktivitaeten -> "Aktivität wird geladen..."
                                is EventToManageType.Termin -> "Anlass wird geladen..."
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ManageEventContentViewForPreview(
    state: PreviewState,
    eventType: EventToManageType,
    modifier: Modifier = Modifier
) {

    val mode: EventManagementMode = when (eventType) {
        is EventToManageType.Aktivitaet -> eventType.mode
        EventToManageType.MultipleAktivitaeten -> EventManagementMode.Insert
        is EventToManageType.Termin -> eventType.mode
    }
    val uiState: UiState<Unit> = when (state) {
        PreviewState.Error -> UiState.Error("Schwerer Fehler")
        PreviewState.Success -> UiState.Success(Unit)
        PreviewState.LoadingEvent -> UiState.Loading
        PreviewState.Publishing -> UiState.Success(Unit)
    }
    val actionState: ActionState<Unit> = when (state) {
        PreviewState.Error, PreviewState.Success, PreviewState.LoadingEvent -> ActionState.Idle
        PreviewState.Publishing -> ActionState.Loading(Unit)
    }
    val pushNotificationBinding: Binding<Boolean>? = when (eventType) {
        is EventToManageType.Aktivitaet, EventToManageType.MultipleAktivitaeten -> Binding.Constant(true)
        is EventToManageType.Termin -> null
    }
    val selectedStufen: Binding<Set<SeesturmStufe>>? = when (eventType) {
        is EventToManageType.Aktivitaet, is EventToManageType.Termin -> null
        EventToManageType.MultipleAktivitaeten -> Binding.Constant(setOf(SeesturmStufe.Wolf,
            SeesturmStufe.Pio))
    }
    val showTemplatesSheet: (() -> Unit)? = when (eventType) {
        is EventToManageType.Aktivitaet, EventToManageType.MultipleAktivitaeten -> { { } }
        is EventToManageType.Termin -> null
    }

    ManageEventContentView(
        eventType = eventType,
        mode = mode,
        navController = rememberNavController(),
        bottomNavigationInnerPadding = PaddingValues(0.dp),
        eventState = uiState,
        publishEventState = actionState,
        start = ZonedDateTime.now(),
        end = ZonedDateTime.now(),
        isAllDay = Binding.Constant(false),
        location = Binding.Constant(""),
        title = SeesturmTextFieldState(
            text = "",
            label = "Titel",
            state = SeesturmBinaryUiState.Success(Unit),
            onValueChanged = {}
        ),
        description = SeesturmRichTextState(
            state = rememberRichTextState(),
            onValueChanged = {}
        ),
        eventForPreview = DummyData.aktivitaet1,
        pushNotificationBinding = pushNotificationBinding,
        selectedStufen = selectedStufen,
        updateStartDatePickerVisibility = { },
        updateStartTimePickerVisibility = { },
        updateEndDatePickerVisibility = { },
        updateEndTimePickerVisibility = { },
        onEventRetry = { },
        onShowTemplateSheet = showTemplatesSheet,
        onShowPreviewSheet = { },
        onTrySubmit = { },
        isDarkTheme = false,
        modifier = modifier,
        onNavigateToTemplates = {}
    )
}

private enum class PreviewState {
    Error,
    Success,
    LoadingEvent,
    Publishing
}

@Preview("Aktivität (Laden)")
@Composable
fun ManageEventViewPreview1() {
    PfadiSeesturmTheme {
        ManageEventContentViewForPreview(
            state = PreviewState.LoadingEvent,
            eventType = EventToManageType.Aktivitaet(
                stufe = SeesturmStufe.Wolf,
                mode = EventManagementMode.Update("")
            )
        )
    }
}
@Preview("Aktivität (Fehler)")
@Composable
fun ManageEventViewPreview2() {
    PfadiSeesturmTheme {
        ManageEventContentViewForPreview(
            state = PreviewState.Error,
            eventType = EventToManageType.Aktivitaet(
                stufe = SeesturmStufe.Wolf,
                mode = EventManagementMode.Insert
            )
        )
    }
}
@Preview("Aktivität (Erfolg)")
@Composable
fun ManageEventViewPreview3() {
    PfadiSeesturmTheme {
        ManageEventContentViewForPreview(
            state = PreviewState.Success,
            eventType = EventToManageType.Aktivitaet(
                stufe = SeesturmStufe.Wolf,
                mode = EventManagementMode.Insert
            )
        )
    }
}
@Preview("Aktivität (Veröffentlichen)")
@Composable
fun ManageEventViewPreview4() {
    PfadiSeesturmTheme {
        ManageEventContentViewForPreview(
            state = PreviewState.Publishing,
            eventType = EventToManageType.Aktivitaet(
                stufe = SeesturmStufe.Wolf,
                mode = EventManagementMode.Update("")
            )
        )
    }
}
@Preview("Aktivitäten (Laden)")
@Composable
fun ManageEventViewPreview5() {
    PfadiSeesturmTheme {
        ManageEventContentViewForPreview(
            state = PreviewState.LoadingEvent,
            eventType = EventToManageType.MultipleAktivitaeten
        )
    }
}
@Preview("Aktivitäten (Fehler)")
@Composable
fun ManageEventViewPreview6() {
    PfadiSeesturmTheme {
        ManageEventContentViewForPreview(
            state = PreviewState.Error,
            eventType = EventToManageType.MultipleAktivitaeten
        )
    }
}
@Preview("Aktivitäten (Erfolg)")
@Composable
fun ManageEventViewPreview7() {
    PfadiSeesturmTheme {
        ManageEventContentViewForPreview(
            state = PreviewState.Success,
            eventType = EventToManageType.MultipleAktivitaeten
        )
    }
}
@Preview("Aktivitäten (Veröffentlichen)")
@Composable
fun ManageEventViewPreview8() {
    PfadiSeesturmTheme {
        ManageEventContentViewForPreview(
            state = PreviewState.Publishing,
            eventType = EventToManageType.MultipleAktivitaeten
        )
    }
}
@Preview("Termin (Laden)")
@Composable
fun ManageEventViewPreview9() {
    PfadiSeesturmTheme {
        ManageEventContentViewForPreview(
            state = PreviewState.LoadingEvent,
            eventType = EventToManageType.Termin(
                calendar = SeesturmCalendar.TERMINE,
                mode = EventManagementMode.Update("")
            )
        )
    }
}
@Preview("Termin (Fehler)")
@Composable
fun ManageEventViewPreview10() {
    PfadiSeesturmTheme {
        ManageEventContentViewForPreview(
            state = PreviewState.Error,
            eventType = EventToManageType.Termin(
                calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM,
                mode = EventManagementMode.Insert
            )
        )
    }
}
@Preview("Termin (Erfolg)")
@Composable
fun ManageEventViewPreview11() {
    PfadiSeesturmTheme {
        ManageEventContentViewForPreview(
            state = PreviewState.Success,
            eventType = EventToManageType.Termin(
                calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM,
                mode = EventManagementMode.Update("")
            )
        )
    }
}
@Preview("Termin (Veröffentlichen)")
@Composable
fun ManageEventViewPreview12() {
    PfadiSeesturmTheme {
        ManageEventContentViewForPreview(
            state = PreviewState.Publishing,
            eventType = EventToManageType.Termin(
                calendar = SeesturmCalendar.TERMINE,
                mode = EventManagementMode.Insert
            )
        )
    }
}