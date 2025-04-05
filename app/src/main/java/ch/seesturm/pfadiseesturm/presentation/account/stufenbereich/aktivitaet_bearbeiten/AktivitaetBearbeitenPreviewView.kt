package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailCardView
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailCardViewType
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.SeesturmStufe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AktivitaetBearbeitenPreviewView(
    aktivitaetForPreview: GoogleCalendarEvent?,
    sheetNavigationController: NavHostController,
    stufe: SeesturmStufe,
    modifier: Modifier = Modifier,
    columnState: LazyListState = rememberLazyListState()
) {
    val vorschauScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxHeight(0.95f),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vorschau ${stufe.aktivitaetDescription}",
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
                scrollBehavior = vorschauScrollBehavior,
                navigationIcon = {
                    IconButton(
                        onClick = { sheetNavigationController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Zurück",
                            tint = Color.SEESTURM_GREEN
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            state = columnState,
            contentPadding = innerPadding,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (aktivitaetForPreview) {
                null -> {
                    item(
                        key = "AktivitaetBearbeitenPreviewError"
                    ) {
                        CardErrorView(
                            errorDescription = "Die Vorschau ist nicht verfügbar. Die Aktivität ist fehlerhaft.",
                            modifier = Modifier
                                .padding(16.dp)
                        )
                    }
                }
                else -> {
                    item(
                        key = "AktivitaetBearbeitenPreview"
                    ) {
                        AktivitaetDetailCardView(
                            stufe = stufe,
                            aktivitaet = aktivitaetForPreview,
                            type = AktivitaetDetailCardViewType.Preview,
                            modifier = Modifier
                                .padding(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AktivitaetBearbeitenPreviewViewPreview() {
    AktivitaetBearbeitenPreviewView(
        aktivitaetForPreview = null,
        sheetNavigationController = rememberNavController(),
        stufe = SeesturmStufe.Wolf
    )
}