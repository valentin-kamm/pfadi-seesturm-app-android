package ch.seesturm.pfadiseesturm.presentation.common.event_management.controllers

import ch.seesturm.pfadiseesturm.domain.fcf.model.CloudFunctionEventPayload
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AnlaesseService
import ch.seesturm.pfadiseesturm.presentation.common.event_management.interfaces.EventManagementController
import ch.seesturm.pfadiseesturm.presentation.common.event_management.interfaces.UpdateCapableEventController
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventManagementMode
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventPreviewType
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventValidationStatus
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventValidationStatusErrorType
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import java.time.ZonedDateTime

class ManageTerminController(
    private val service: AnlaesseService,
    private val calendar: SeesturmCalendar
): EventManagementController, UpdateCapableEventController {

    override val eventPreviewType: EventPreviewType
        get() = EventPreviewType.Termin(calendar)

    override fun validateEvent(
        event: CloudFunctionEventPayload,
        isAllDay: Boolean,
        mode: EventManagementMode
    ): EventValidationStatus {

        // errors
        if (event.end < event.start) {
            return EventValidationStatus.Error(
                type = EventValidationStatusErrorType.Snackbar(
                    message = "Das Enddatum darf nicht vor dem Startdatum sein."
                )
            )
        }
        if (event.summary.isEmpty()) {
            return EventValidationStatus.Error(
                type = EventValidationStatusErrorType.TitleTextField(
                    message = "Der Titel darf nicht leer sein."
                )
            )
        }

        // warnings
        if (ZonedDateTime.now().isAfter(event.start)) {
            return EventValidationStatus.Warning(
                title = "Startdatum in der Vergangenheit",
                description = "Das Startdatum ist in der Vergangenheit. Möchtest du den Anlass trotzdem ${mode.verb}?"
            )
        }
        if (ZonedDateTime.now().isAfter(event.end)) {
            return EventValidationStatus.Warning(
                title = "Enddatum in der Vergangenheit",
                description = "Das Enddatum ist in der Vergangenheit. Möchtest du den Anlass trotzdem ${mode.verb}?"
            )
        }
        if (event.description.isEmpty()) {
            return EventValidationStatus.Warning(
                title = "Beschreibung leer",
                description = "Die Beschreibung ist leer. Möchtest du den Anlass trotzdem ${mode.verb}?"
            )
        }
        if (event.location.isEmpty()) {
            return EventValidationStatus.Warning(
                title = "Kein Treffpunkt",
                description = "Der Treffpunkt ist leer. Möchtest du den Anlass trotzdem ${mode.verb}?"
            )
        }

        return EventValidationStatus.Valid
    }

    override suspend fun addEvent(event: CloudFunctionEventPayload): SeesturmResult<Unit, DataError.CloudFunctionsError> =
        service.addEvent(event = event, calendar = this.calendar)

    override suspend fun fetchEvent(eventId: String): SeesturmResult<GoogleCalendarEvent, DataError.Network> =
        service.getOrFetchEvent(calendar = this.calendar, eventId = eventId, cacheIdentifier = MemoryCacheIdentifier.ForceReload)

    override suspend fun updateEvent(
        eventId: String,
        event: CloudFunctionEventPayload
    ): SeesturmResult<Unit, DataError.CloudFunctionsError> =
        service.updateEvent(eventId = eventId, event = event, calendar = this.calendar)
}