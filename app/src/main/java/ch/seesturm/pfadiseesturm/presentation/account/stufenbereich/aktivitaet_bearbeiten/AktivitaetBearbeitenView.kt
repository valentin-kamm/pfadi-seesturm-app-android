package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.StufenbereichSheetMode
import ch.seesturm.pfadiseesturm.presentation.common.SeesturmDatePicker
import ch.seesturm.pfadiseesturm.presentation.common.SeesturmTimePicker
import ch.seesturm.pfadiseesturm.presentation.common.SimpleAlert
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.components.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListFooter
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemTertiaryElementType
import ch.seesturm.pfadiseesturm.presentation.common.rich_text_editor.SeesturmHTMLEditor
import ch.seesturm.pfadiseesturm.presentation.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.SeesturmTextField
import ch.seesturm.pfadiseesturm.util.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.util.navigation.AppDestination
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.UiState
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AktivitaetBearbeitenView(
    viewModel: AktivitaetBearbeitenViewModel,
    stufe: SeesturmStufe,
    selectedSheetMode: StufenbereichSheetMode,
    snackbarHost: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    sheetNavigationController: NavHostController = rememberNavController()
) {

    val bearbeitenScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    NavHost(
        navController = sheetNavigationController,
        startDestination = AktivitaetBearbeitenNavigationDestination.AktivitaetBearbeiten
    ) {
        composable<AktivitaetBearbeitenNavigationDestination.AktivitaetBearbeiten> {
            Scaffold(
                modifier = Modifier
                    .fillMaxHeight(0.95f),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = when (selectedSheetMode) { StufenbereichSheetMode.Insert -> { "Neue ${stufe.aktivitaetDescription}" } is StufenbereichSheetMode.Update -> { "${stufe.aktivitaetDescription} bearbeiten" } },
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = TopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            scrolledContainerColor = MaterialTheme.colorScheme.background,
                            navigationIconContentColor = Color.SEESTURM_GREEN,
                            titleContentColor = MaterialTheme.colorScheme.onBackground,
                            actionIconContentColor = Color.SEESTURM_GREEN,
                            subtitleContentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        scrollBehavior = bearbeitenScrollBehavior
                    )
                },
                snackbarHost = snackbarHost
            ) { innerPadding ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(innerPadding)
                        .nestedScroll(bearbeitenScrollBehavior.nestedScrollConnection)
                ) {
                    AktivitaetBearbeitenContentView(
                        viewModel = viewModel,
                        sheetNavigationController = sheetNavigationController,
                        modifier = modifier,
                        stufe = stufe
                    )
                }
            }
        }
        composable<AktivitaetBearbeitenNavigationDestination.AktivitaetBearbeitenPreview> {
            val stufe = it.toRoute<AktivitaetBearbeitenNavigationDestination.AktivitaetBearbeitenPreview>().stufe
            AktivitaetBearbeitenPreviewView(
                aktivitaetForPreview = viewModel.aktivitaetForPreview,
                sheetNavigationController = sheetNavigationController,
                stufe = stufe,
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AktivitaetBearbeitenContentView(
    viewModel: AktivitaetBearbeitenViewModel,
    sheetNavigationController: NavHostController,
    stufe: SeesturmStufe,
    modifier: Modifier
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

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
        }
    )

    SeesturmDatePicker(
        datePickerState = uiState.startDatePickerState,
        isShown = isStartDatePickerShown.value,
        onDismiss = { isStartDatePickerShown.value = false },
        onConfirm = { year, month, dayOfMonth ->
            viewModel.updateStartDate(year, month, dayOfMonth)
        },
        dismissOnClickOutside = false,
        dismissOnBackPress = false
    )
    SeesturmDatePicker(
        datePickerState = uiState.endDatePickerState,
        isShown = isEndDatePickerShown.value,
        onDismiss = { isEndDatePickerShown.value = false },
        onConfirm = { year, month, dayOfMonth ->
            viewModel.updateEndDate(year, month, dayOfMonth)
        },
        dismissOnClickOutside = false,
        dismissOnBackPress = false
    )

    SeesturmTimePicker(
        state = uiState.startTimePickerState,
        isShown = isStartTimePickerShown.value,
        onDismiss = { isStartTimePickerShown.value = false },
        onConfirm = { hour, minute ->
            viewModel.updateStartDate(hour, minute)
        },
        dismissOnClickOutside = false,
        dismissOnBackPress = false
    )
    SeesturmTimePicker(
        state = uiState.endTimePickerState,
        isShown = isEndTimePickerShown.value,
        onDismiss = { isEndTimePickerShown.value = false },
        onConfirm = { hour, minute ->
            viewModel.updateEndDate(hour, minute)
        },
        dismissOnClickOutside = false,
        dismissOnBackPress = false
    )

    AktivitaetBearbeitenForm(
        uiState = uiState,
        modifier = modifier,
        startDateDateFormatted = viewModel.startDateDateFormatted,
        startDateTimeFormatted = viewModel.startDateTimeFormatted,
        endDateDateFormatted = viewModel.endDateDateFormatted,
        endDateTimeFormatted = viewModel.endDateTimeFormatted,
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
        buttonTitle = viewModel.buttonTitle,
        onPushNotificationChange = { newValue ->
            viewModel.updatePushNotification(newValue)
        },
        onSubmitButtonClick = {
            viewModel.trySubmit()
        },
        sheetNavigationController = sheetNavigationController,
        aktivitaetForPreview = viewModel.aktivitaetForPreview,
        stufe = stufe,
        onErrorRetry = {
            viewModel.fetchAktivitaetIfNecessary()
        }
    )
}

@Composable
private fun AktivitaetBearbeitenForm(
    uiState: AktivitaetBearbeitenState,
    buttonTitle: String,
    startDateDateFormatted: String,
    startDateTimeFormatted: String,
    endDateDateFormatted: String,
    endDateTimeFormatted: String,
    aktivitaetForPreview: GoogleCalendarEvent?,
    updateStartDatePickerVisibility: (Boolean) -> Unit,
    updateStartTimePickerVisibility: (Boolean) -> Unit,
    updateEndDatePickerVisibility: (Boolean) -> Unit,
    updateEndTimePickerVisibility: (Boolean) -> Unit,
    onLocationChange: (String) -> Unit,
    onPushNotificationChange: (Boolean) -> Unit,
    onSubmitButtonClick: () -> Unit,
    onErrorRetry: () -> Unit,
    sheetNavigationController: NavHostController,
    stufe: SeesturmStufe,
    modifier: Modifier = Modifier,
    columnState: LazyListState = rememberLazyListState()
) {

    LazyColumn(
        state = columnState,
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        when (uiState.aktivitaetState) {
            is UiState.Error -> {
                item(
                    key = "AktivitaetBearbeitenErrorItem"
                ) {
                    CardErrorView(
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
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .animateItem()
                    ) {
                        RedactedText(
                            numberOfLines = 1,
                            lastLineFraction = 0.25f,
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    }
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
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .animateItem()
                    ) {
                        RedactedText(
                            numberOfLines = 1,
                            lastLineFraction = 0.5f,
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    }
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
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .animateItem()
                    ) {
                        RedactedText(
                            numberOfLines = 1,
                            lastLineFraction = 0.6f,
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                item(
                    key = "AktivitaetBearbeitenDescriptionLoadingItem"
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .animateItem()
                    ) {
                        RedactedText(
                            numberOfLines = 1,
                            lastLineFraction = 0.33f,
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    }
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
                }
                item(
                    key = "AktivitaetBearbeitenPublishLoadingItem"
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .animateItem()
                    ) {
                        RedactedText(
                            numberOfLines = 1,
                            lastLineFraction = 0.4f,
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    }
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
                    BasicListHeader("Zeit")
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
                                        type = SeesturmButtonType.Primary(
                                            buttonColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = Color.SEESTURM_GREEN
                                        ),
                                        title = startDateDateFormatted,
                                        onClick = {
                                            updateStartDatePickerVisibility(true)
                                        },
                                        enabled = !uiState.publishAktivitaetState.isLoading,
                                        modifier = Modifier
                                            .wrapContentWidth()
                                    )
                                    SeesturmButton(
                                        type = SeesturmButtonType.Primary(
                                            buttonColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = Color.SEESTURM_GREEN
                                        ),
                                        title = startDateTimeFormatted,
                                        onClick = {
                                            updateStartTimePickerVisibility(true)
                                        },
                                        enabled = !uiState.publishAktivitaetState.isLoading,
                                        modifier = Modifier
                                            .wrapContentWidth()
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
                                        type = SeesturmButtonType.Primary(
                                            buttonColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = Color.SEESTURM_GREEN
                                        ),
                                        title = endDateDateFormatted,
                                        onClick = {
                                            updateEndDatePickerVisibility(true)
                                        },
                                        enabled = !uiState.publishAktivitaetState.isLoading,
                                        modifier = Modifier
                                            .wrapContentWidth()
                                    )
                                    SeesturmButton(
                                        type = SeesturmButtonType.Primary(
                                            buttonColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = Color.SEESTURM_GREEN
                                        ),
                                        title = endDateTimeFormatted,
                                        onClick = {
                                            updateEndTimePickerVisibility(true)
                                        },
                                        enabled = !uiState.publishAktivitaetState.isLoading,
                                        modifier = Modifier
                                            .wrapContentWidth()
                                    )
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                        )
                    )
                    BasicListFooter("Zeiten in MEZ/MESZ (CH-Zeit)")
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
                                    icon = Icons.Outlined.LocationOn,
                                    enabled = !uiState.publishAktivitaetState.isLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        )
                    )
                    BasicListFooter("Treffpunkt am Anfang der Aktivität")
                }

                item(
                    key = "AktivitaetBearbeitenDescriptionItem"
                ) {
                    BasicListHeader("Beschreibung")
                    FormItem(
                        items = (0..1).toList(),
                        index = 0,
                        modifier = Modifier
                            .animateItem(),
                        mainContent = FormItemContentType.Custom(
                            content = {
                                SeesturmTextField(
                                    state = uiState.title,
                                    icon = Icons.Outlined.Title,
                                    enabled = !uiState.publishAktivitaetState.isLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        )
                    )
                    FormItem(
                        items = (0..1).toList(),
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
                }

                item(
                    key = "AktivitaetBearbeitenPublishItem"
                ) {
                    BasicListHeader(buttonTitle)
                    FormItem(
                        items = if (aktivitaetForPreview == null) {
                            (0..0).toList()
                        }
                        else {
                            (0..1).toList()
                        },
                        index = 0,
                        modifier = Modifier
                            .animateItem(),
                        mainContent = FormItemContentType.Text(
                            title = "Push-Nachricht senden"
                        ),
                        trailingElement = FormItemTertiaryElementType.Custom(
                            content = {
                                Switch(
                                    checked = uiState.sendPushNotification,
                                    onCheckedChange = { newValue ->
                                        onPushNotificationChange(newValue)
                                    },
                                    enabled = !uiState.publishAktivitaetState.isLoading
                                )
                            }
                        )
                    )
                    if (aktivitaetForPreview != null) {
                        FormItem(
                            items = (0..1).toList(),
                            index = 1,
                            modifier = Modifier
                                .animateItem(),
                            mainContent = FormItemContentType.Text(
                                title = "Vorschau"
                            ),
                            trailingElement = FormItemTertiaryElementType.DisclosureIndicator,
                            onClick = if (!uiState.publishAktivitaetState.isLoading) {
                                {
                                    sheetNavigationController.navigate(
                                        AktivitaetBearbeitenNavigationDestination.AktivitaetBearbeitenPreview(
                                            stufe = stufe
                                        )
                                    )
                                }
                            }
                            else {
                                null
                            }
                        )
                    }
                }
                item(
                    key = "AktivitaetBearbeitenButtonItem"
                ) {
                    SeesturmButton(
                        type = SeesturmButtonType.Primary(),
                        modifier = Modifier
                            .animateItem(),
                        title = buttonTitle,
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

@Serializable
sealed class AktivitaetBearbeitenNavigationDestination {
    @Serializable
    data object AktivitaetBearbeiten: AktivitaetBearbeitenNavigationDestination()
    @Serializable
    data class AktivitaetBearbeitenPreview(
        val stufe: SeesturmStufe
    ): AktivitaetBearbeitenNavigationDestination()
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AktivitaetBearbeitenViewPreview() {
    PfadiSeesturmTheme {
        AktivitaetBearbeitenForm(
            uiState = AktivitaetBearbeitenState(
                aktivitaetState = UiState.Loading,
                publishAktivitaetState = ActionState.Idle,
                title = SeesturmTextFieldState(
                    text = "",
                    label = "Titel",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = {}
                ),
                description = RichTextState(),
                location = "",
                start = ZonedDateTime.now(),
                end = ZonedDateTime.now(),
                sendPushNotification = true,
                showConfirmationDialog = false,
                startDatePickerState = rememberDatePickerState(),
                startTimePickerState = rememberTimePickerState(),
                endDatePickerState = rememberDatePickerState(),
                endTimePickerState = rememberTimePickerState(),
            ),
            startDateDateFormatted = "24.04.2025",
            startDateTimeFormatted = "14:00",
            endDateDateFormatted = "24.04.2025",
            endDateTimeFormatted = "16:00",
            updateStartDatePickerVisibility = {},
            updateStartTimePickerVisibility = {},
            updateEndDatePickerVisibility = {},
            updateEndTimePickerVisibility = {},
            onLocationChange = {},
            buttonTitle = "Veröffentlichen",
            onPushNotificationChange = {},
            onSubmitButtonClick = {},
            sheetNavigationController = rememberNavController(),
            stufe = SeesturmStufe.Wolf,
            aktivitaetForPreview = GoogleCalendarEvent(
                id = "TODO()",
                title = "",
                description = "TODO()",
                location = "TODO()",
                created = ZonedDateTime.now(),
                updated = ZonedDateTime.now(),
                createdFormatted = "TODO()",
                updatedFormatted = "TODO()",
                isAllDay = false,
                firstDayOfMonthOfStartDate = ZonedDateTime.now(),
                startDate = ZonedDateTime.now(),
                endDate = ZonedDateTime.now(),
                startDayFormatted = "TODO()",
                startMonthFormatted = "TODO()",
                endDateFormatted = "TODO()",
                timeFormatted = "TODO()",
                fullDateTimeFormatted = "TODO()"
            ),
            onErrorRetry = {}
        )
    }
}