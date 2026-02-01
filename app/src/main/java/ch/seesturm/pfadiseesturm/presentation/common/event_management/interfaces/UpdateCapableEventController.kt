package ch.seesturm.pfadiseesturm.presentation.common.event_management.interfaces

import ch.seesturm.pfadiseesturm.domain.fcf.model.CloudFunctionEventPayload
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult

interface UpdateCapableEventController {
    suspend fun fetchEvent(eventId: String): SeesturmResult<GoogleCalendarEvent, DataError.Network>
    suspend fun updateEvent(eventId: String, event: CloudFunctionEventPayload): SeesturmResult<Unit, DataError.CloudFunctionsError>
}