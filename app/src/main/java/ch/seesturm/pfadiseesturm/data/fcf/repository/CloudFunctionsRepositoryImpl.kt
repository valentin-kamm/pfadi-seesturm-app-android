package ch.seesturm.pfadiseesturm.data.fcf.repository

import ch.seesturm.pfadiseesturm.data.fcf.CloudFunctionsApi
import ch.seesturm.pfadiseesturm.data.fcf.SeesturmCloudFunction
import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionAddEventRequestDto
import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionAddEventResponseDto
import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionEventPayloadDto
import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionPushNotificationRequestDto
import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionPushNotificationResponseDto
import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionTokenRequestDto
import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionTokenResponseDto
import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionUpdateEventRequestDto
import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionUpdateEventResponseDto
import ch.seesturm.pfadiseesturm.domain.auth.repository.FirebaseAuthToken
import ch.seesturm.pfadiseesturm.domain.auth.repository.HitobitoAccessToken
import ch.seesturm.pfadiseesturm.domain.fcf.repository.CloudFunctionsRepository
import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationType
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmError
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar

class CloudFunctionsRepositoryImpl(
    private val api: CloudFunctionsApi
): CloudFunctionsRepository {

    override suspend fun getFirebaseAuthToken(userId: String, hitobitoAccessToken: HitobitoAccessToken): FirebaseAuthToken {

        val payload = CloudFunctionTokenRequestDto(
            userId = userId,
            hitobitoAccessToken = hitobitoAccessToken
        )
        val response = api.invokeCloudFunction(
            function = SeesturmCloudFunction.GetFirebaseAuthToken,
            data = payload,
            inputSerializer = CloudFunctionTokenRequestDto.serializer(),
            outputSerializer = CloudFunctionTokenResponseDto.serializer()
        )
        if (response.userId != userId) {
            throw PfadiSeesturmError.AuthError("Ungültige Benutzer-ID im Authentifizierungs-Token.")
        }
        if (response.firebaseAuthToken.trim().isEmpty()) {
            throw PfadiSeesturmError.AuthError("Authentifizierungs-Token ist leer.")
        }
        return response.firebaseAuthToken
    }

    override suspend fun addEvent(
        calendar: SeesturmCalendar,
        event: CloudFunctionEventPayloadDto
    ): CloudFunctionAddEventResponseDto {

        val payload = CloudFunctionAddEventRequestDto(
            calendarId = calendar.calendarId,
            payload = event
        )
        return api.invokeCloudFunction(
            function = SeesturmCloudFunction.PublishGoogleCalendarEvent,
            data = payload,
            inputSerializer = CloudFunctionAddEventRequestDto.serializer(),
            outputSerializer = CloudFunctionAddEventResponseDto.serializer()
        )
    }

    override suspend fun updateEvent(
        calendar: SeesturmCalendar,
        eventId: String,
        event: CloudFunctionEventPayloadDto
    ): CloudFunctionUpdateEventResponseDto {

        val payload = CloudFunctionUpdateEventRequestDto(
            calendarId = calendar.calendarId,
            eventId = eventId,
            payload = event
        )
        return api.invokeCloudFunction(
            function = SeesturmCloudFunction.UpdateGoogleCalendarEvent,
            data = payload,
            inputSerializer = CloudFunctionUpdateEventRequestDto.serializer(),
            outputSerializer = CloudFunctionUpdateEventResponseDto.serializer()
        )
    }

    override suspend fun sendPushNotification(type: SeesturmFCMNotificationType): CloudFunctionPushNotificationResponseDto {

        val payload = CloudFunctionPushNotificationRequestDto(
            topic = type.topic.topic,
            title = type.content.title,
            body = type.content.body,
            customKey = type.content.customKey
        )
        return api.invokeCloudFunction(
            function = SeesturmCloudFunction.SendPushNotificationToTopic,
            data = payload,
            inputSerializer = CloudFunctionPushNotificationRequestDto.serializer(),
            outputSerializer = CloudFunctionPushNotificationResponseDto.serializer()
        )
    }
}