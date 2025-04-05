package ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material.icons.outlined.TagFaces
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.seesturm.pfadiseesturm.data.data_store.dao.SeesturmPreferencesDao
import ch.seesturm.pfadiseesturm.data.data_store.repository.GespeichertePersonenRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.data_store.service.GespeichertePersonenService
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.viewModelFactoryHelper
import ch.seesturm.pfadiseesturm.util.SeesturmTextField

@Composable
fun GespeichertePersonHinzufuegenView(
    viewModel: GespeichertePersonenViewModel,
    modifier: Modifier,
    columnState: LazyListState = rememberLazyListState()
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val textFieldCount = 3

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
                items = (0..<textFieldCount).toList(),
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
                items = (0..<textFieldCount).toList(),
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
                items = (0..<textFieldCount).toList(),
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
            Text(
                "Füge die Angaben von Personen hinzu, die du of von Aktivitäten abmeldest. So musst du sie nicht jedes Mal neu eintragen.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .alpha(0.4f)
            )
        }
        item {
            SeesturmButton(
                type = SeesturmButtonType.Primary(
                    icon = SeesturmButtonIconType.Predefined(
                        icon = Icons.Default.PersonAddAlt
                    )
                ),
                title = "Speichern",
                onClick = {
                    viewModel.addPerson()
                }
            )
        }
    }
}

@Preview
@Composable
fun GespeichertePersonHinzufuegenViewPreview() {
    GespeichertePersonHinzufuegenView(
        viewModel = viewModel<GespeichertePersonenViewModel>(
            factory = viewModelFactoryHelper {
                GespeichertePersonenViewModel(
                    service = GespeichertePersonenService(
                        repository = GespeichertePersonenRepositoryImpl(
                            dataStore = FakeDataStore(
                                initialValue = SeesturmPreferencesDao()
                            )
                        )
                    ),
                    updateSheetContent = {}
                )
            }
        ),
        modifier = Modifier
    )
}