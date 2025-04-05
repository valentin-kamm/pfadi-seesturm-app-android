package ch.seesturm.pfadiseesturm.domain.fcf.repository

import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionAddEventResponseDto
import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionEventPayloadDto
import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionTokenRequestDto
import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionTokenResponseDto
import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionUpdateEventResponseDto
import ch.seesturm.pfadiseesturm.util.SeesturmCalendar
import kotlin.reflect.KClass

interface CloudFunctionsRepository {

    suspend fun getFirebaseAuthToken(
        userId: String,
        hitobitoAccessToken: String
    ): String

    suspend fun addEvent(calendar: SeesturmCalendar, event: CloudFunctionEventPayloadDto): CloudFunctionAddEventResponseDto
    suspend fun updateEvent(calendar: SeesturmCalendar, eventId: String, event: CloudFunctionEventPayloadDto): CloudFunctionUpdateEventResponseDto
    suspend fun sendPushNotification()
}