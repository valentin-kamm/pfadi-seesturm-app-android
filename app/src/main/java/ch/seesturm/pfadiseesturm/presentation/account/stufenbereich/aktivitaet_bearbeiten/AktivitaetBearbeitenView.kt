package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
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
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.templates.TemplateListView
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.templates.TemplateListViewMode
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.TopBarNavigationIcon
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.alert.SimpleAlert
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonColor
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListFooter
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeaderMode
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumn
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumnItemTrailingContentType
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
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
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import com.mohamedrejeb.richeditor.model.RichTextState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AktivitaetBearbeitenView(
    viewModel: AktivitaetBearbeitenViewModel,
    appStateViewModel: AppStateViewModel,
    mode: AktivitaetBearbeitenMode,
    stufe: SeesturmStufe,
    bottomNavigationInnerPadding: PaddingValues,
    accountNavController: NavHostController,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val appState by appStateViewModel.state.collectAsStateWithLifecycle()

    val isStartDatePickerShown = rememberSaveable { mutableStateOf(false) }
    val isStartTimePickerShown = rememberSaveable { mutableStateOf(false) }
    val isEndDatePickerShown = rememberSaveable { mutableStateOf(false) }
    val isEndTimePickerShown = rememberSaveable { mutableStateOf(false) }

    SimpleAlert(
        isShown = uiState.showConfirmationDialog,
        title = viewModel.confirmationDialogTitle,
        description = viewModel.confirmationDialogDescription,
        icon = viewModel.confirmationDialogIcon,
        confirmButtonText = viewModel.confirmationDialogConfirmButtonText,
        onConfirm = {
            viewModel.submit()
        },
        onDismiss = {
            viewModel.updateConfirmationDialogVisibility(false)
        },
        isConfirmButtonCritical = true
    )

    SeesturmDatePicker(
        datePickerState = uiState.startDatePickerState,
        isShown = isStartDatePickerShown.value,
        onDismiss = { isStartDatePickerShown.value = false },
        onConfirm = { year, month, dayOfMonth ->
            viewModel.updateStartDate(year, month, dayOfMonth)
        }
    )
    SeesturmTimePicker(
        state = uiState.startTimePickerState,
        isShown = isStartTimePickerShown.value,
        onDismiss = { isStartTimePickerShown.value = false },
        onConfirm = { hour, minute ->
            viewModel.updateStartDate(hour, minute)
        }
    )
    SeesturmDatePicker(
        datePickerState = uiState.endDatePickerState,
        isShown = isEndDatePickerShown.value,
        onDismiss = { isEndDatePickerShown.value = false },
        onConfirm = { year, month, dayOfMonth ->
            viewModel.updateEndDate(year, month, dayOfMonth)
        }
    )
    SeesturmTimePicker(
        state = uiState.endTimePickerState,
        isShown = isEndTimePickerShown.value,
        onDismiss = { isEndTimePickerShown.value = false },
        onConfirm = { hour, minute ->
            viewModel.updateEndDate(hour, minute)
        }
    )

    ModalBottomSheetWithItem(
        item = viewModel.aktivitaetForPreviewSheet,
        detents = SheetDetents.All,
        type = SheetScaffoldType.Title("Vorschau ${stufe.aktivitaetDescription}"),
        appStateViewModel = appStateViewModel,
        keyboardResponse = ModalBottomSheetKeyboardResponse.None
    ) { event, _, _ ->
        AktivitaetBearbeitenPreviewView(
            aktivitaet = event,
            stufe = stufe,
            isDarkTheme = appState.theme.isDarkTheme
        )
    }

    val showTemplatesSheet = rememberSaveable { mutableStateOf(false) }

    SimpleModalBottomSheet(
        show = showTemplatesSheet,
        detents = SheetDetents.MediumOnly,
        type = SheetScaffoldType.Title("Vorlagen ${stufe.stufenName}"),
        appStateViewModel = appStateViewModel,
        keyboardResponse = ModalBottomSheetKeyboardResponse.None
    ) { _, _ ->
        TemplateListView(
            state = uiState.templatesState,
            mode = TemplateListViewMode.Use,
            contentPadding = PaddingValues(16.dp),
            onClick = { template ->
                viewModel.useTemplate(template)
                showTemplatesSheet.value = false
            },
            isInEditingMode = false
        )
    }

    AktivitaetBearbeitenContentView(
        uiState = uiState,
        modifier = modifier,
        updateStartDatePickerVisibility = { isVisible ->
            isStartDatePickerShown.value = isVisible
        },
        updateStartTimePickerVisibility = { isVisible ->
            isStartTimePickerShown.value = isVisible
        },
        updateEndDatePickerVisibility = { isVisible ->
            isEndDatePickerShown.value = isVisible
        },
        updateEndTimePickerVisibility = { isVisible ->
            isEndTimePickerShown.value = isVisible
        },
        onLocationChange = { newLocation ->
            viewModel.updateLocation(newLocation)
        },
        onPushNotificationChange = { newValue ->
            viewModel.updatePushNotification(newValue)
        },
        onSubmitButtonClick = {
            viewModel.trySubmit()
        },
        aktivitaetForPreview = viewModel.aktivitaetForPreview,
        stufe = stufe,
        onErrorRetry = {
            viewModel.fetchAktivitaetIfNecessary()
        },
        aktivitaetForPreviewSheet = viewModel.aktivitaetForPreviewSheet,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        mode = mode,
        showTemplatesSheet = showTemplatesSheet,
        onNavigateToTemplates = {
            accountNavController.navigate(
                AppDestination.MainTabView.Destinations.Account.Destinations.Templates(
                    stufe = stufe
                )
            )
        },
        onNavigateBack = {
            accountNavController.navigateUp()
        },
        isDarkTheme = appState.theme.isDarkTheme,
        onToggleAllDay = { isAllDay ->
            viewModel.updateAllDay(isAllDay)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
private fun AktivitaetBearbeitenContentView(
    uiState: AktivitaetBearbeitenState,
    bottomNavigationInnerPadding: PaddingValues,
    mode: AktivitaetBearbeitenMode,
    aktivitaetForPreview: GoogleCalendarEvent?,
    updateStartDatePickerVisibility: (Boolean) -> Unit,
    updateStartTimePickerVisibility: (Boolean) -> Unit,
    updateEndDatePickerVisibility: (Boolean) -> Unit,
    updateEndTimePickerVisibility: (Boolean) -> Unit,
    onToggleAllDay: (Boolean) -> Unit,
    onLocationChange: (String) -> Unit,
    onPushNotificationChange: (Boolean) -> Unit,
    onSubmitButtonClick: () -> Unit,
    onErrorRetry: () -> Unit,
    onNavigateBack: () -> Unit,
    stufe: SeesturmStufe,
    aktivitaetForPreviewSheet: MutableState<GoogleCalendarEvent?>,
    showTemplatesSheet: MutableState<Boolean>,
    onNavigateToTemplates: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    columnState: LazyListState = rememberLazyListState()
) {

    val hazeState = remember { HazeState() }

    val startDateFormatted = DateTimeUtil.shared.formatDate(
        date = uiState.start,
        format = "dd.MM.yyyy",
        type = DateFormattingType.Absolute
    )
    val startTimeFormatted = DateTimeUtil.shared.formatDate(
        date = uiState.start,
        format = "HH:mm",
        type = DateFormattingType.Absolute
    )
    val endDateFormatted = DateTimeUtil.shared.formatDate(
        date = uiState.end,
        format = "dd.MM.yyyy",
        type = DateFormattingType.Absolute
    )
    val endTimeFormatted = DateTimeUtil.shared.formatDate(
        date = uiState.end,
        format = "HH:mm",
        type = DateFormattingType.Absolute
    )

    val controlsEnabled = !uiState.publishAktivitaetState.isLoading && !uiState.aktivitaetState.isLoading

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        title = mode.topBarTitle(stufe),
        navigationAction = TopBarNavigationIcon.Back { onNavigateBack() },
        modifier = modifier,
        actions = {
            TextButton(
                onClick = onNavigateToTemplates
            ) {
                Text("Vorlagen")
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
                userScrollEnabled = !uiState.aktivitaetState.isLoading,
                sectionSpacing = 16.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .hazeSource(hazeState)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (uiState.aktivitaetState) {
                    is UiState.Error -> {
                        section {
                            customItem(
                                key = "ManageEventErrorItem"
                            ) {
                                ErrorCardView(
                                    errorDescription = uiState.aktivitaetState.message,
                                    retryAction = {
                                        onErrorRetry()
                                    }
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
                                key = "AktivitaetBearbeitenStartDateItem",
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
                                                contentColor = Color.SEESTURM_GREEN
                                            ),
                                            title = startDateFormatted,
                                            onClick = {
                                                updateStartDatePickerVisibility(true)
                                            },
                                            enabled = controlsEnabled,
                                            modifier = Modifier
                                                .wrapContentWidth(),
                                            isLoading = false
                                        )
                                        if (!uiState.isAllDay) {
                                            SeesturmButton(
                                                type = SeesturmButtonType.Primary,
                                                colors = SeesturmButtonColor.Custom(
                                                    buttonColor = MaterialTheme.colorScheme.secondaryContainer,
                                                    contentColor = Color.SEESTURM_GREEN
                                                ),
                                                title = startTimeFormatted,
                                                onClick = {
                                                    updateStartTimePickerVisibility(true)
                                                },
                                                enabled = controlsEnabled,
                                                modifier = Modifier
                                                    .wrapContentWidth(),
                                                isLoading = false
                                            )
                                        }
                                    }
                                }
                            )
                            textItem(
                                key = "AktivitaetBearbeitenEndDateItem",
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
                                                contentColor = Color.SEESTURM_GREEN
                                            ),
                                            title = endDateFormatted,
                                            onClick = {
                                                updateEndDatePickerVisibility(true)
                                            },
                                            enabled = controlsEnabled,
                                            modifier = Modifier
                                                .wrapContentWidth(),
                                            isLoading = false
                                        )
                                        if (!uiState.isAllDay) {
                                            SeesturmButton(
                                                type = SeesturmButtonType.Primary,
                                                colors = SeesturmButtonColor.Custom(
                                                    buttonColor = MaterialTheme.colorScheme.secondaryContainer,
                                                    contentColor = Color.SEESTURM_GREEN
                                                ),
                                                title = endTimeFormatted,
                                                onClick = {
                                                    updateEndTimePickerVisibility(true)
                                                },
                                                enabled = controlsEnabled,
                                                modifier = Modifier
                                                    .wrapContentWidth(),
                                                isLoading = false
                                            )
                                        }
                                    }
                                }
                            )
                            textItem(
                                key = "AktivitaetBearbeitenAllDayDateItem",
                                text = "Ganztägig",
                                trailingContent = GroupedColumnItemTrailingContentType.Custom {
                                    Switch(
                                        checked = uiState.isAllDay,
                                        onCheckedChange = { newValue ->
                                            onToggleAllDay(newValue)
                                        },
                                        enabled = controlsEnabled,
                                        colors = SwitchDefaults.colors().copy(
                                            checkedThumbColor = MaterialTheme.colorScheme.background,
                                            checkedTrackColor = stufe.highContrastColor(isDarkTheme)
                                        )
                                    )
                                }
                            )
                        }

                        section(
                            footer = {
                                BasicListFooter(BasicListHeaderMode.Normal("Treffpunkt am Anfang der Aktivität"))
                            }
                        ) {
                            item(
                                key = "AktivitaetBearbeitenPlaceItem"
                            ) {
                                SeesturmTextField(
                                    state = SeesturmTextFieldState(
                                        text = uiState.location,
                                        label = "Treffpunkt",
                                        state = SeesturmBinaryUiState.Success(Unit),
                                        onValueChanged = { newValue ->
                                            onLocationChange(newValue)
                                        }
                                    ),
                                    leadingIcon = Icons.Outlined.LocationOn,
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
                                key = "AktivitaetBearbeitenTitleItem"
                            ) {
                                SeesturmTextField(
                                    state = uiState.title,
                                    leadingIcon = Icons.Outlined.Title,
                                    enabled = controlsEnabled,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                            item(
                                key = "AktivitaetBearbeitenDescriptionItem"
                            ) {
                                SeesturmHTMLEditor(
                                    state = uiState.description,
                                    enabled = controlsEnabled,
                                    placeholder = {
                                        Text("Beschreibung")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                )
                            }
                            textItem(
                                key = "AktivitaetBearbeitenInsertTemplateItem",
                                text = "Vorlage einfügen",
                                onClick = if (controlsEnabled) {
                                    { showTemplatesSheet.value = true }
                                } else {
                                    null
                                }
                            )
                        }

                        section(
                            header = {
                                BasicListHeader(BasicListHeaderMode.Normal(mode.buttonTitle))
                            }
                        ) {
                            if (aktivitaetForPreview != null) {
                                textItem(
                                    key = "AktivitaetBearbeitenPublishPreviewItem",
                                    text = "Vorschau",
                                    onClick = if (controlsEnabled) {
                                        { aktivitaetForPreviewSheet.value = aktivitaetForPreview }
                                    } else {
                                        null
                                    }
                                )
                            }
                            textItem(
                                key = "AktivitaetBearbeitenPublishPushNotificationItem",
                                text = "Push-Nachricht senden",
                                trailingContent = GroupedColumnItemTrailingContentType.Custom {
                                    Switch(
                                        checked = uiState.sendPushNotification,
                                        onCheckedChange = { newValue ->
                                            onPushNotificationChange(newValue)
                                        },
                                        enabled = controlsEnabled,
                                        colors = SwitchDefaults.colors().copy(
                                            checkedThumbColor = MaterialTheme.colorScheme.background,
                                            checkedTrackColor = stufe.highContrastColor(isDarkTheme)
                                        )
                                    )
                                }
                            )
                        }

                        section {
                            customItem(
                                key = "AktivitaetBearbeitenButtonItem"
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SeesturmButton(
                                        type = SeesturmButtonType.Primary,
                                        colors = SeesturmButtonColor.Custom(
                                            buttonColor = stufe.highContrastColor(isDarkTheme),
                                            contentColor = stufe.onHighContrastColor()
                                        ),
                                        enabled = controlsEnabled,
                                        title = mode.buttonTitle,
                                        onClick = {
                                            onSubmitButtonClick()
                                        },
                                        isLoading = uiState.publishAktivitaetState.isLoading
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (uiState.aktivitaetState.isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .then(
                            if (Build.VERSION.SDK_INT >= 30) {
                                Modifier
                                    .hazeEffect(hazeState, style = CupertinoMaterials.ultraThin())
                                    .background(Color.Transparent)
                            }
                            else {
                                Modifier
                                    .background(MaterialTheme.colorScheme.background)
                            }
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically)
                ) {
                    CircularProgressIndicator(
                        color = stufe.highContrastColor(isDarkTheme),
                        modifier = Modifier
                            .size(32.dp)
                    )
                    Text(
                        text = "${stufe.aktivitaetDescription} wird geladen...",
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Loading", uiMode = UI_MODE_NIGHT_NO)
@Preview("Loading", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AktivitaetBearbeitenViewPreview1() {
    PfadiSeesturmTheme {
        AktivitaetBearbeitenContentView(
            uiState = AktivitaetBearbeitenState(
                aktivitaetState = UiState.Loading,
                publishAktivitaetState = ActionState.Idle,
                title = SeesturmTextFieldState(
                    text = "",
                    label = "",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = {}
                ),
                description = SeesturmRichTextState(
                    state = RichTextState(),
                    onValueChanged = {},
                    annotatedString = AnnotatedString("")
                ),
                location = "",
                start = ZonedDateTime.now(),
                end = ZonedDateTime.now(),
                sendPushNotification = true,
                showConfirmationDialog = false,
                startDatePickerState = DatePickerState(
                    initialSelectedDateMillis = ZonedDateTime.now().toEpochSecond(),
                    locale = CalendarLocale.getDefault()
                ),
                startTimePickerState = TimePickerState(
                    initialHour = ZonedDateTime.now().hour,
                    initialMinute = ZonedDateTime.now().minute,
                    is24Hour = true
                ),
                endDatePickerState = DatePickerState(
                    initialSelectedDateMillis = ZonedDateTime.now().toEpochSecond(),
                    locale = CalendarLocale.getDefault()
                ),
                endTimePickerState = TimePickerState(
                    initialHour = ZonedDateTime.now().hour,
                    initialMinute = ZonedDateTime.now().minute,
                    is24Hour = true
                ),
                templatesState = UiState.Loading,
                isAllDay = false
            ),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            mode = AktivitaetBearbeitenMode.Insert,
            aktivitaetForPreview = null,
            updateStartDatePickerVisibility = {},
            updateStartTimePickerVisibility = {},
            updateEndDatePickerVisibility = {},
            updateEndTimePickerVisibility = {},
            onLocationChange = {},
            onPushNotificationChange = {},
            onSubmitButtonClick = {},
            onErrorRetry = {},
            onNavigateBack = {},
            stufe = SeesturmStufe.Wolf,
            aktivitaetForPreviewSheet = remember { mutableStateOf(null) },
            showTemplatesSheet = remember { mutableStateOf(false) },
            onNavigateToTemplates = {},
            modifier = Modifier,
            isDarkTheme = false,
            onToggleAllDay = {}
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Error", uiMode = UI_MODE_NIGHT_NO)
@Preview("Error", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AktivitaetBearbeitenViewPreview2() {
    PfadiSeesturmTheme {
        AktivitaetBearbeitenContentView(
            uiState = AktivitaetBearbeitenState(
                aktivitaetState = UiState.Error("Schwerer Fehler"),
                publishAktivitaetState = ActionState.Idle,
                title = SeesturmTextFieldState(
                    text = "",
                    label = "",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = {}
                ),
                description = SeesturmRichTextState(
                    state = RichTextState(),
                    onValueChanged = {},
                    annotatedString = AnnotatedString("")
                ),
                location = "",
                start = ZonedDateTime.now(),
                end = ZonedDateTime.now(),
                sendPushNotification = true,
                showConfirmationDialog = false,
                startDatePickerState = DatePickerState(
                    initialSelectedDateMillis = ZonedDateTime.now().toEpochSecond(),
                    locale = CalendarLocale.getDefault()
                ),
                startTimePickerState = TimePickerState(
                    initialHour = ZonedDateTime.now().hour,
                    initialMinute = ZonedDateTime.now().minute,
                    is24Hour = true
                ),
                endDatePickerState = DatePickerState(
                    initialSelectedDateMillis = ZonedDateTime.now().toEpochSecond(),
                    locale = CalendarLocale.getDefault()
                ),
                endTimePickerState = TimePickerState(
                    initialHour = ZonedDateTime.now().hour,
                    initialMinute = ZonedDateTime.now().minute,
                    is24Hour = true
                ),
                templatesState = UiState.Loading,
                isAllDay = false
            ),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            mode = AktivitaetBearbeitenMode.Insert,
            aktivitaetForPreview = null,
            updateStartDatePickerVisibility = {},
            updateStartTimePickerVisibility = {},
            updateEndDatePickerVisibility = {},
            updateEndTimePickerVisibility = {},
            onLocationChange = {},
            onPushNotificationChange = {},
            onSubmitButtonClick = {},
            onErrorRetry = {},
            onNavigateBack = {},
            stufe = SeesturmStufe.Wolf,
            aktivitaetForPreviewSheet = remember { mutableStateOf(null) },
            showTemplatesSheet = remember { mutableStateOf(false) },
            onNavigateToTemplates = {},
            modifier = Modifier,
            isDarkTheme = false,
            onToggleAllDay = {}
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Success", uiMode = UI_MODE_NIGHT_NO)
@Preview("Success", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AktivitaetBearbeitenViewPreview3() {
    PfadiSeesturmTheme {
        AktivitaetBearbeitenContentView(
            uiState = AktivitaetBearbeitenState(
                aktivitaetState = UiState.Success(Unit),
                publishAktivitaetState = ActionState.Idle,
                title = SeesturmTextFieldState(
                    text = "",
                    label = "Titel",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = {}
                ),
                description = SeesturmRichTextState(
                    state = RichTextState(),
                    onValueChanged = {},
                    annotatedString = AnnotatedString("")
                ),
                location = "",
                start = ZonedDateTime.now(),
                end = ZonedDateTime.now(),
                sendPushNotification = true,
                showConfirmationDialog = false,
                startDatePickerState = DatePickerState(
                    initialSelectedDateMillis = ZonedDateTime.now().toEpochSecond(),
                    locale = CalendarLocale.getDefault()
                ),
                startTimePickerState = TimePickerState(
                    initialHour = ZonedDateTime.now().hour,
                    initialMinute = ZonedDateTime.now().minute,
                    is24Hour = true
                ),
                endDatePickerState = DatePickerState(
                    initialSelectedDateMillis = ZonedDateTime.now().toEpochSecond(),
                    locale = CalendarLocale.getDefault()
                ),
                endTimePickerState = TimePickerState(
                    initialHour = ZonedDateTime.now().hour,
                    initialMinute = ZonedDateTime.now().minute,
                    is24Hour = true
                ),
                templatesState = UiState.Loading,
                isAllDay = false
            ),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            mode = AktivitaetBearbeitenMode.Insert,
            aktivitaetForPreview = DummyData.aktivitaet1,
            updateStartDatePickerVisibility = {},
            updateStartTimePickerVisibility = {},
            updateEndDatePickerVisibility = {},
            updateEndTimePickerVisibility = {},
            onLocationChange = {},
            onPushNotificationChange = {},
            onSubmitButtonClick = {},
            onErrorRetry = {},
            onNavigateBack = {},
            stufe = SeesturmStufe.Biber,
            aktivitaetForPreviewSheet = remember { mutableStateOf(null) },
            showTemplatesSheet = remember { mutableStateOf(false) },
            onNavigateToTemplates = {},
            modifier = Modifier,
            isDarkTheme = false,
            onToggleAllDay = {}
        )
    }
}