package ch.seesturm.pfadiseesturm.presentation.common.event_management.interfaces

import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import kotlinx.coroutines.flow.StateFlow

interface MultiStufenCapableEventController {
    val selectedStufen: StateFlow<Set<SeesturmStufe>>
    fun setSelectedStufen(stufen: Set<SeesturmStufe>)
}