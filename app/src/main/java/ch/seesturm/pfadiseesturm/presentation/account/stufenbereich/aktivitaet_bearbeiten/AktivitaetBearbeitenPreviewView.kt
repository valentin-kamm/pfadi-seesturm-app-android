package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.common.sheet.LocalScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailCardView
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailViewMode
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe

@Composable
fun AktivitaetBearbeitenPreviewView(
    aktivitaet: GoogleCalendarEvent,
    stufe: SeesturmStufe,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
            .fillMaxSize()
    ) {
        item {
            AktivitaetDetailCardView(
                aktivitaet = aktivitaet,
                stufe = stufe,
                mode = AktivitaetDetailViewMode.ViewOnly,
                isDarkTheme = isDarkTheme,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AktivitaetBearbeitenPreviewViewPreview() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            AktivitaetBearbeitenPreviewView(
                aktivitaet = DummyData.aktivitaet1,
                stufe = SeesturmStufe.Wolf,
                isDarkTheme = false,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}