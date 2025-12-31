package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.TopBarNavigationIcon
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.alert.SimpleAlert
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonColor
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListFooter
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListHeaderMode
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemTrailingElementType
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
        isDarkTheme = appState.theme.isDarkTheme
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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

        val combinedPadding =
            bottomNavigationInnerPadding.intersectWith(
                other = topBarInnerPadding,
                layoutDirection = LayoutDirection.Ltr,
                additionalStartPadding = 16.dp,
                additionalEndPadding = 16.dp,
                additionalTopPadding = 16.dp,
                additionalBottomPadding = 16.dp
            )

        LazyColumn(
            state = columnState,
            contentPadding = combinedPadding,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            userScrollEnabled = !uiState.aktivitaetState.isLoading,
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .background(MaterialTheme.colorScheme.background)
        ) {

            when (uiState.aktivitaetState) {
                is UiState.Error -> {
                    item(
                        key = "AktivitaetBearbeitenErrorItem"
                    ) {
                        ErrorCardView(
                            errorDescription = uiState.aktivitaetState.message,
                            retryAction = {
                                onErrorRetry()
                            }
                        )
                    }
                }
                UiState.Loading -> {
                    item(
                        key = "AktivitaetBearbeitenDateLoadingItem"
                    ) {
                        BasicListHeader(BasicListHeaderMode.Loading)
                        FormItem(
                            items = (0..1).toList(),
                            index = 0,
                            modifier = Modifier
                                .animateItem(),
                            mainContent = FormItemContentType.Text(
                                title = "Start",
                                isLoading = true
                            )
                        )
                        FormItem(
                            items = (0..1).toList(),
                            index = 1,
                            modifier = Modifier
                                .animateItem(),
                            mainContent = FormItemContentType.Text(
                                title = "Start",
                                isLoading = true
                            )
                        )
                        BasicListFooter(mode = BasicListHeaderMode.Loading)
                    }
                    item(
                        key = "AktivitaetBearbeitenPlaceLoadingItem"
                    ) {
                        FormItem(
                            items = (0..0).toList(),
                            index = 0,
                            modifier = Modifier
                                .animateItem(),
                            mainContent = FormItemContentType.Text(
                                title = "Start",
                                isLoading = true
                            )
                        )
                        BasicListFooter(BasicListHeaderMode.Loading)
                    }
                    item(
                        key = "AktivitaetBearbeitenDescriptionLoadingItem"
                    ) {
                        BasicListHeader(BasicListHeaderMode.Loading)
                        FormItem(
                            items = (0..2).toList(),
                            index = 0,
                            modifier = Modifier
                                .animateItem(),
                            mainContent = FormItemContentType.Text(
                                title = "Start",
                                isLoading = true
                            )
                        )
                        FormItem(
                            items = (0..2).toList(),
                            index = 1,
                            modifier = Modifier
                                .animateItem(),
                            mainContent = FormItemContentType.Custom(
                                content = {
                                    RedactedText(
                                        numberOfLines = 7,
                                        lastLineFraction = 0.75f,
                                        textStyle = LocalTextStyle.current
                                    )
                                },
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 16.dp,
                                    bottom = 48.dp
                                )
                            )
                        )
                        FormItem(
                            items = (0..2).toList(),
                            index = 2,
                            modifier = Modifier
                                .animateItem(),
                            mainContent = FormItemContentType.Text(
                                title = "Vorlage einfügen",
                                isLoading = true
                            ),
                            trailingElement = FormItemTrailingElementType.Blank
                        )
                    }
                    item(
                        key = "AktivitaetBearbeitenPublishLoadingItem"
                    ) {
                        BasicListHeader(BasicListHeaderMode.Loading)
                        FormItem(
                            items = (0..1).toList(),
                            index = 0,
                            modifier = Modifier
                                .animateItem(),
                            mainContent = FormItemContentType.Text(
                                title = "Start",
                                isLoading = true
                            )
                        )
                        FormItem(
                            items = (0..1).toList(),
                            index = 1,
                            modifier = Modifier
                                .animateItem(),
                            mainContent = FormItemContentType.Text(
                                title = "Start",
                                isLoading = true
                            )
                        )
                    }
                }

                is UiState.Success -> {
                    item(
                        key = "AktivitaetBearbeitenDateItem"
                    ) {
                        BasicListHeader(BasicListHeaderMode.Normal("Zeit"))
                        FormItem(
                            items = (0..1).toList(),
                            index = 0,
                            modifier = Modifier
                                .animateItem(),
                            mainContent = FormItemContentType.Custom(
                                content = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Start",
                                            textAlign = TextAlign.Start,
                                            style = MaterialTheme.typography.bodyLarge,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier
                                                .weight(1f)
                                        )
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
                                            enabled = !uiState.publishAktivitaetState.isLoading,
                                            modifier = Modifier
                                                .wrapContentWidth(),
                                            isLoading = false
                                        )
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
                                            enabled = !uiState.publishAktivitaetState.isLoading,
                                            modifier = Modifier
                                                .wrapContentWidth(),
                                            isLoading = false
                                        )
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                            )
                        )
                        FormItem(
                            items = (0..1).toList(),
                            index = 1,
                            modifier = Modifier
                                .animateItem(),
                            mainContent = FormItemContentType.Custom(
                                content = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Ende",
                                            textAlign = TextAlign.Start,
                                            style = MaterialTheme.typography.bodyLarge,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier
                                                .weight(1f)
                                        )
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
                                            enabled = !uiState.publishAktivitaetState.isLoading,
                                            modifier = Modifier
                                                .wrapContentWidth(),
                                            isLoading = false
                                        )
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
                                            enabled = !uiState.publishAktivitaetState.isLoading,
                                            modifier = Modifier
                                                .wrapContentWidth(),
                                            isLoading = false
                                        )
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                            )
                        )
                        BasicListFooter(BasicListHeaderMode.Normal("Zeiten in MEZ/MESZ (CH-Zeit)"))
                    }

                    item(
                        key = "AktivitaetBearbeitenPlaceItem"
                    ) {
                        FormItem(
                            items = (0..0).toList(),
                            index = 0,
                            modifier = Modifier
                                .animateItem(),
                            mainContent = FormItemContentType.Custom(
                                content = {
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
                                        enabled = !uiState.publishAktivitaetState.isLoading,
                                        keyboardOptions = KeyboardOptions(
                                            imeAction = ImeAction.Next
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            )
                        )
                        BasicListFooter(BasicListHeaderMode.Normal("Treffpunkt am Anfang der Aktivität"))
                    }

                    item(
                        key = "AktivitaetBearbeitenDescriptionItem"
                    ) {
                        BasicListHeader(BasicListHeaderMode.Normal("Beschreibung"))
                        FormItem(
                            items = (0..2).toList(),
                            index = 0,
                            modifier = Modifier
                                .animateItem(),
                            mainContent = FormItemContentType.Custom(
                                content = {
                                    SeesturmTextField(
                                        state = uiState.title,
                                        leadingIcon = Icons.Outlined.Title,
                                        enabled = !uiState.publishAktivitaetState.isLoading,
                                        keyboardOptions = KeyboardOptions(
                                            imeAction = ImeAction.Next
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            )
                        )
                        FormItem(
                            items = (0..2).toList(),
                            index = 1,
                            modifier = Modifier
                                .animateItem(),
                            mainContent = FormItemContentType.Custom(
                                content = {
                                    SeesturmHTMLEditor(
                                        state = uiState.description,
                                        enabled = !uiState.publishAktivitaetState.isLoading,
                                        placeholder = {
                                            Text("Beschreibung")
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp)
                                    )
                                }
                            )
                        )
                        FormItem(
                            items = (0..2).toList(),
                            index = 2,
                            modifier = Modifier
                                .animateItem(),
                            mainContent = FormItemContentType.Text(
                                title = "Vorlage einfügen"
                            ),
                            trailingElement = FormItemTrailingElementType.Blank,
                            onClick = if (!uiState.publishAktivitaetState.isLoading) {
                                { showTemplatesSheet.value = true }
                            }
                            else {
                                null
                            }
                        )
                    }

                    item(
                        key = "AktivitaetBearbeitenPublishItem"
                    ) {
                        BasicListHeader(BasicListHeaderMode.Normal(mode.buttonTitle))
                        if (aktivitaetForPreview != null) {
                            FormItem(
                                items = (0..1).toList(),
                                index = 0,
                                modifier = Modifier
                                    .animateItem(),
                                mainContent = FormItemContentType.Text(
                                    title = "Vorschau"
                                ),
                                trailingElement = FormItemTrailingElementType.Blank,
                                onClick = if (!uiState.publishAktivitaetState.isLoading) {
                                    { aktivitaetForPreviewSheet.value = aktivitaetForPreview }
                                }
                                else {
                                    null
                                }
                            )
                        }
                        FormItem(
                            items = if (aktivitaetForPreview == null) {
                                (0..0).toList()
                            }
                            else {
                                (0..1).toList()
                            },
                            index = if (aktivitaetForPreview == null) {
                                0
                            }
                            else {
                                1
                            },
                            modifier = Modifier
                                .animateItem(),
                            mainContent = FormItemContentType.Text(
                                title = "Push-Nachricht senden"
                            ),
                            trailingElement = FormItemTrailingElementType.Custom(
                                content = {
                                    Switch(
                                        checked = uiState.sendPushNotification,
                                        onCheckedChange = { newValue ->
                                            onPushNotificationChange(newValue)
                                        },
                                        enabled = !uiState.publishAktivitaetState.isLoading,
                                        colors = SwitchDefaults.colors().copy(
                                            checkedThumbColor = MaterialTheme.colorScheme.background,
                                            checkedTrackColor = stufe.highContrastColor(isDarkTheme)
                                        )
                                    )
                                }
                            )
                        )
                    }
                    item(
                        key = "AktivitaetBearbeitenButtonItem"
                    ) {
                        SeesturmButton(
                            type = SeesturmButtonType.Primary,
                            colors = SeesturmButtonColor.Custom(
                                buttonColor = stufe.highContrastColor(isDarkTheme),
                                contentColor = stufe.onHighContrastColor()
                            ),
                            modifier = Modifier
                                .animateItem(),
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
                templatesState = UiState.Loading
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
            aktivitaetForPreviewSheet = mutableStateOf(null),
            showTemplatesSheet = mutableStateOf(false),
            onNavigateToTemplates = {},
            modifier = Modifier,
            isDarkTheme = false
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
                templatesState = UiState.Loading
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
            aktivitaetForPreviewSheet = mutableStateOf(null),
            showTemplatesSheet = mutableStateOf(false),
            onNavigateToTemplates = {},
            modifier = Modifier,
            isDarkTheme = false
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
                templatesState = UiState.Loading
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
            aktivitaetForPreviewSheet = mutableStateOf(null),
            showTemplatesSheet = mutableStateOf(false),
            onNavigateToTemplates = {},
            modifier = Modifier,
            isDarkTheme = false
        )
    }
}