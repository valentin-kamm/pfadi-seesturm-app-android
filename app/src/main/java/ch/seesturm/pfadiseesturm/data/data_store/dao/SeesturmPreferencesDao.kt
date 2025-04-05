package ch.seesturm.pfadiseesturm.data.data_store.dao

import ch.seesturm.pfadiseesturm.presentation.main.SeesturmAppTheme
import ch.seesturm.pfadiseesturm.util.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import kotlinx.serialization.Serializable

@Serializable
data class SeesturmPreferencesDao(
    val savedPersons: List<GespeichertePersonDao> = emptyList(),
    val subscribedFcmTopics: Set<SeesturmFCMNotificationTopic> = emptySet(),
    val selectedStufen: Set<SeesturmStufe> = setOf(
        SeesturmStufe.Biber,
        SeesturmStufe.Wolf,
        SeesturmStufe.Pfadi,
        SeesturmStufe.Pio
    ),
    val selectedTheme: SeesturmAppTheme = SeesturmAppTheme.System
)