package ch.seesturm.pfadiseesturm.presentation.common.event_management.interfaces

import ch.seesturm.pfadiseesturm.domain.fcf.model.CloudFunctionEventPayload
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventManagementMode
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventPreviewType
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventValidationStatus
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult

interface EventManagementController {
    fun validateEvent(event: CloudFunctionEventPayload, isAllDay: Boolean, mode: EventManagementMode): EventValidationStatus
    suspend fun addEvent(event: CloudFunctionEventPayload): SeesturmResult<Unit, DataError.CloudFunctionsError>
    val eventPreviewType: EventPreviewType
}