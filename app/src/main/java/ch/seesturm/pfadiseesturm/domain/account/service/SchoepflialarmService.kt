package ch.seesturm.pfadiseesturm.domain.account.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import ch.seesturm.pfadiseesturm.BuildConfig
import ch.seesturm.pfadiseesturm.data.firestore.dto.SchoepflialarmDto
import ch.seesturm.pfadiseesturm.data.firestore.dto.SchoepflialarmReactionDto
import ch.seesturm.pfadiseesturm.domain.fcf.repository.CloudFunctionsRepository
import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationType
import ch.seesturm.pfadiseesturm.domain.fcm.service.FCMService
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmAppError
import ch.seesturm.pfadiseesturm.util.SchoepflialarmError
import ch.seesturm.pfadiseesturm.util.SchoepflialarmLocalizedError
import ch.seesturm.pfadiseesturm.util.types.SchoepflialarmReactionType
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.Timestamp
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.round

class SchoepflialarmService(
    private val firestoreRepository: FirestoreRepository,
    private val fcmService: FCMService,
    private val fcfRepository: CloudFunctionsRepository,
    private val context: Context,
    private val locationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
) {

    fun observeSchoepflialarm(): Flow<SeesturmResult<SchoepflialarmDto, DataError.RemoteDatabase>> {
        return firestoreRepository.observeDocument(
            document = FirestoreRepository.SeesturmFirestoreDocument.Schoepflialarm,
            type = SchoepflialarmDto::class.java
        )
    }

    fun observeSchoepflialarmReactions(): Flow<SeesturmResult<List<SchoepflialarmReactionDto>, DataError.RemoteDatabase>> {
        return firestoreRepository.observeCollection(
            collection = FirestoreRepository.SeesturmFirestoreCollection.SchoepflialarmReactions,
            type = SchoepflialarmReactionDto::class.java
        )
    }

    suspend fun sendSchoepflialarm(
        messageType: SchoepflialarmMessageType,
        userId: String,
        userDisplayNameShort: String,
        requestMessagingPermission: suspend () -> Boolean,
        requestLocationPermission: suspend () -> Boolean
    ): SeesturmResult<Unit, SchoepflialarmError> = coroutineScope {

        val pushNotificationType: SeesturmFCMNotificationType = when (messageType) {
            is SchoepflialarmMessageType.Custom -> {
                SeesturmFCMNotificationType.SchoepflialarmCustom(
                    userName = userDisplayNameShort,
                    body = messageType.message
                )
            }
            SchoepflialarmMessageType.Generic -> {
                SeesturmFCMNotificationType.SchoepflialarmGeneric(
                    userName = userDisplayNameShort
                )
            }
        }

        try {

            fcmService.requestOrCheckNotificationPermission(requestMessagingPermission)
            checkLastSchoepflialarmTime()
            checkUserLocation(requestLocationPermission)

            // first send push notification and only if this has finished, execute the other requests in parallel (usability reasons)
            val sendNotificationResult = async { fcfRepository.sendPushNotification(pushNotificationType) }

            sendNotificationResult.await()

            val updateSchoepflialarmResult = async {
                firestoreRepository.performTransaction(
                    document = FirestoreRepository.SeesturmFirestoreDocument.Schoepflialarm,
                    type = SchoepflialarmDto::class.java,
                    forceNewCreatedDate = true,
                    update = { _ ->
                        SchoepflialarmDto(
                            created = Timestamp.now(),
                            modified = Timestamp.now(),
                            message = pushNotificationType.content.body,
                            userId = userId
                        )
                    }
                )
            }
            val deleteReactionsResult = async {
                firestoreRepository.deleteAllDocumentsInCollection(FirestoreRepository.SeesturmFirestoreCollection.SchoepflialarmReactions)
            }

            updateSchoepflialarmResult.await()
            deleteReactionsResult.await()

            SeesturmResult.Success(Unit)
        }
        catch (e: SchoepflialarmLocalizedError) {
            SeesturmResult.Error(e.toSchoepflialarmError())
        }
        catch (e: PfadiSeesturmAppError) {
            when (e) {
                is PfadiSeesturmAppError.MessagingPermissionError -> {
                    SeesturmResult.Error(SchoepflialarmError.MessagingPermissionMissing)
                }
                else -> {
                    SeesturmResult.Error(SchoepflialarmError.Unknown("Beim Senden des Schöpflialarm ist ein unbekannter Fehler aufgetreten. ${e.message}"))
                }
            }
        }
        catch (e: Exception) {
            SeesturmResult.Error(SchoepflialarmError.Unknown("Beim Senden des Schöpflialarm ist ein unbekannter Fehler aufgetreten. ${e.message}"))
        }
    }

    suspend fun sendSchoepflialarmReaction(
        userId: String,
        userDisplayNameShort: String,
        reaction: SchoepflialarmReactionType
    ): SeesturmResult<Unit, DataError.RemoteDatabase> {

        val payload = SchoepflialarmReactionDto(
            created = Timestamp.now(),
            modified = Timestamp.now(),
            userId = userId,
            reaction = reaction.rawValue
        )

        return try {
            fcfRepository.sendPushNotification(
                type = SeesturmFCMNotificationType.SchoepflialarmReactionGeneric(
                    userName = userDisplayNameShort,
                    type = reaction
                )
            )
            firestoreRepository.insertDocument(
                item = payload,
                collection = FirestoreRepository.SeesturmFirestoreCollection.SchoepflialarmReactions
            )
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.RemoteDatabase.SAVING_ERROR)
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun checkUserLocation(
        requestPermission: suspend () -> Boolean
    ) {
        requestOrCheckLocationPermission(requestPermission)

        val userLocation = locationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).await()

        val schoepfliLocation = Location("schoepfliLocation")
        schoepfliLocation.latitude = Constants.SCHOPFLI_LATITUDE
        schoepfliLocation.longitude = Constants.SCHOPFLI_LONGITUDE

        val distance = abs(schoepfliLocation.distanceTo(userLocation))
        val distanceForDisplay = if (distance < 1000) {
            "${round(distance).toInt()} m"
        }
        else {
            "${round(distance/1000).toInt()} km"
        }

        if (!BuildConfig.DEBUG && distance > Constants.SCHOPFLIALARM_MAX_DISTANCE) {
            throw SchoepflialarmLocalizedError.TooFarAway(distanceForDisplay)
        }
    }

    private suspend fun requestOrCheckLocationPermission(
        requestPermission: suspend () -> Boolean
    ) {
        if (!hasLocationPermission) {
            val isGranted = requestPermission()
            if (!isGranted) {
                throw SchoepflialarmLocalizedError.LocationPermissionError()
            }
        }
    }
    private val hasLocationPermission: Boolean
        get() = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private suspend fun checkLastSchoepflialarmTime() {

        val lastSchoepflialarm = firestoreRepository.readDocument(
            document = FirestoreRepository.SeesturmFirestoreDocument.Schoepflialarm,
            type = SchoepflialarmDto::class.java
        )
        val lastCreatedDate = DateTimeUtil.shared.convertFirestoreTimestampToDate(lastSchoepflialarm.created)
        val timeDiff = abs(ChronoUnit.SECONDS.between(lastCreatedDate, ZonedDateTime.now()))
        if (!BuildConfig.DEBUG && timeDiff < Constants.SCHOPFLIALARM_MIN_PAUSE) {
            throw SchoepflialarmLocalizedError.TooEarly("Schöpflialarm wurde bereits ausgelöst. Es ist nur ein Schöpflialarm pro Stunde erlaubt.")
        }
    }
}

sealed class SchoepflialarmMessageType {
    data object Generic: SchoepflialarmMessageType()
    data class Custom(
        val message: String
    ): SchoepflialarmMessageType()
}