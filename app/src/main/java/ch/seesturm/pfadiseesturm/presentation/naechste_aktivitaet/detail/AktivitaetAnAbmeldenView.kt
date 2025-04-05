package ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.TagFaces
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.seesturm.pfadiseesturm.data.data_store.dao.SeesturmPreferencesDao
import ch.seesturm.pfadiseesturm.data.data_store.repository.GespeichertePersonenRepositoryImpl
import ch.seesturm.pfadiseesturm.data.data_store.repository.SelectedStufenRepositoryImpl
import ch.seesturm.pfadiseesturm.data.firestore.FirestoreApiImpl
import ch.seesturm.pfadiseesturm.data.firestore.repository.FirestoreRepositoryImpl
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventStartEndDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toGoogleCalendarEvent
import ch.seesturm.pfadiseesturm.data.wordpress.repository.NaechsteAktivitaetRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.data_store.service.GespeichertePersonenService
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.service.NaechsteAktivitaetService
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.components.DropdownButton
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemTertiaryElementType
import ch.seesturm.pfadiseesturm.presentation.common.viewModelFactoryHelper
import ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen.FakeDataStore
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.SeesturmTextField
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun AktivitaetAnAbmeldenView(
    viewModel: AktivitaetDetailViewModel,
    aktivitaet: GoogleCalendarEvent,
    stufe: SeesturmStufe,
    columnState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val firstTextFieldCount = 3

    LazyColumn(
        state = columnState,
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            FormItem(
                items = (0..<firstTextFieldCount).toList(),
                index = 0,
                mainContent = FormItemContentType.Custom(
                    content = {
                        SeesturmTextField(
                            state = uiState.vornameState,
                            icon = Icons.Outlined.AccountBox,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    },
                    contentPadding = PaddingValues(16.dp)
                )
            )
            FormItem(
                items = (0..<firstTextFieldCount).toList(),
                index = 1,
                mainContent = FormItemContentType.Custom(
                    content = {
                        SeesturmTextField(
                            state = uiState.nachnameState,
                            icon = Icons.Filled.AccountBox,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    },
                    contentPadding = PaddingValues(16.dp)
                )
            )
            FormItem(
                items = (0..<firstTextFieldCount).toList(),
                index = 2,
                mainContent = FormItemContentType.Custom(
                    content = {
                        SeesturmTextField(
                            state = uiState.pfadinameState,
                            icon = Icons.Outlined.TagFaces,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    },
                    contentPadding = PaddingValues(16.dp)
                )
            )
        }
        item {
            BasicListHeader("Bemerkung (optional)")
            FormItem(
                items = (0..<1).toList(),
                index = 0,
                mainContent = FormItemContentType.Custom(
                    content = {
                        SeesturmTextField(
                            state = uiState.bemerkungState,
                            icon = Icons.AutoMirrored.Outlined.Comment,
                            singleLine = false,
                            hideLabel = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                    },
                    contentPadding = PaddingValues(16.dp)
                )
            )
        }
        item {
            FormItem(
                items = (0..<1).toList(),
                index = 0,
                mainContent = FormItemContentType.Text(
                    title = "An-/Abmeldung"
                ),
                trailingElement = FormItemTertiaryElementType.Custom(
                    content = {
                        DropdownButton(
                            title = uiState.selectedSheetMode.nomen,
                            contentColor = uiState.selectedSheetMode.color,
                            dropdown = { isShown, dismiss ->
                                DropdownMenu(
                                    expanded = isShown,
                                    onDismissRequest = {
                                        dismiss()
                                    }
                                ) {
                                    stufe.allowedAktivitaetInteractions.forEach { interaction ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(interaction.nomen)
                                            },
                                            onClick = {
                                                dismiss()
                                                viewModel.changeSheetMode(interaction)
                                            },
                                            trailingIcon = {
                                                Icon(
                                                    imageVector = interaction.icon,
                                                    contentDescription = null
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }
                )
            )
        }
        item {
            SeesturmButton(
                type = SeesturmButtonType.Primary(
                    buttonColor = uiState.selectedSheetMode.color
                ),
                title = "${uiState.selectedSheetMode.nomen} senden",
                isLoading = uiState.anAbmeldenState.isLoading,
                onClick = {
                    viewModel.sendAnAbmeldung()
                }
            )
        }
    }
}

@Preview
@Composable
private fun AktivitaetAnAbmeldenViewPreview() {
    val na = GoogleCalendarEventDto(
        id = "17v15laf167s75oq47elh17a3t",
        summary = "Biberstufen-Aktivität",
        description = "Ob uns wohl der Pfadi-Chlaus dieses Jahr wieder viele Nüssli und Schöggeli bringt? Die genauen Zeiten werden später kommuniziert.",
        location = "Geiserparkplatz",
        created = "2022-08-28T15:25:45.701Z",
        updated = "2022-08-28T15:19:45.726Z",
        start = GoogleCalendarEventStartEndDto(
            dateTime = "2022-12-10T13:00:00Z",
            date = null
        ),
        end = GoogleCalendarEventStartEndDto(
            dateTime = "2022-12-10T15:00:00Z",
            date = null
        )
    ).toGoogleCalendarEvent()
    AktivitaetAnAbmeldenView(
        viewModel = viewModel<AktivitaetDetailViewModel>(
            factory = viewModelFactoryHelper {
                AktivitaetDetailViewModel(
                    service = NaechsteAktivitaetService(
                        repository = NaechsteAktivitaetRepositoryImpl(
                            api = Retrofit.Builder()
                                .baseUrl(Constants.SEESTURM_API_BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                                .create(WordpressApi::class.java)
                        ),
                        firestoreRepository = FirestoreRepositoryImpl(
                            api = FirestoreApiImpl(
                                Firebase.firestore
                            ),
                            db = Firebase.firestore
                        ),
                        selectedStufenRepository = SelectedStufenRepositoryImpl(
                            dataStore = FakeDataStore(
                                initialValue = SeesturmPreferencesDao()
                            )
                        )
                    ),
                    gespeichertePersonenService = GespeichertePersonenService(
                        repository = GespeichertePersonenRepositoryImpl(
                            dataStore = FakeDataStore(
                                initialValue = SeesturmPreferencesDao()
                            )
                        )
                    ),
                    stufe = SeesturmStufe.Biber,
                    eventId = "",
                    dismiss = {},
                    userId = null
                )
            }
        ),
        aktivitaet = na,
        stufe = SeesturmStufe.Pio,
        modifier = Modifier
            .fillMaxSize()
    )
}