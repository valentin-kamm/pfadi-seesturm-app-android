package ch.seesturm.pfadiseesturm.domain.fcm.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.domain.fcm.repository.FCMRepository
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException

class FCMService(
    private val fcmRepository: FCMRepository,
    private val firestoreRepository: FirestoreRepository,
    private val context: Context
) {

    suspend fun subscribe(
        topic: SeesturmFCMNotificationTopic,
        requestPermission: suspend () -> Boolean
    ): SeesturmResult<SeesturmFCMNotificationTopic, DataError.Messaging> =
        try {
            requestOrCheckNotificationPermission(requestPermission)
            fcmRepository.subscribeToTopic(topic)
            SeesturmResult.Success(topic)
        }
        catch (e: Exception) {
            SeesturmResult.Error(
                when (e) {
                    is PfadiSeesturmError.MessagingPermissionError -> {
                        DataError.Messaging.PERMISSION_ERROR
                    }
                    is SerializationException -> {
                        DataError.Messaging.DATA_SAVING_ERROR(topic)
                    }
                    else -> {
                        DataError.Messaging.SUBSCRIPTION_FAILED(topic)
                    }
                }
            )
        }

    suspend fun unsubscribe(topic: SeesturmFCMNotificationTopic): SeesturmResult<SeesturmFCMNotificationTopic, DataError.Messaging> =
        try {
            fcmRepository.unsubscribeFromTopic(topic)
            SeesturmResult.Success(topic)
        }
        catch (e: Exception) {
            SeesturmResult.Error(
                when (e) {
                    is SerializationException -> {
                        DataError.Messaging.DATA_SAVING_ERROR(topic)
                    }
                    else -> {
                        DataError.Messaging.UNSUBSCRIPTION_FAILED(topic)
                    }
                }
            )
        }

    suspend fun requestOrCheckNotificationPermission(
        requestPermission: suspend () -> Boolean
    ) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }
        if (!hasNotificationPermission) {
            val isGranted = requestPermission()
            if (!isGranted) {
                throw PfadiSeesturmError.MessagingPermissionError("Berechtigung für Push-Nachrichten nicht erteilt.")
            }
        }
    }

    private val hasNotificationPermission: Boolean
        get() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                return true
            }
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }

    fun readSubscribedTopics(): Flow<SeesturmResult<Set<SeesturmFCMNotificationTopic>, DataError.Messaging>> =
        fcmRepository.getSubscribedTopics()
            .map<_, SeesturmResult<Set<SeesturmFCMNotificationTopic>, DataError.Messaging>> { list ->
                SeesturmResult.Success(list)
            }
            .catch { e ->
                emit(
                    SeesturmResult.Error(
                        when (e) {
                            is SerializationException -> {
                                DataError.Messaging.DATA_READING_ERROR
                            }
                            else -> {
                                DataError.Messaging.UNKNOWN
                            }
                        }
                    )
                )
            }

    suspend fun updateFCMToken(userId: String, newToken: String): SeesturmResult<Unit, DataError.RemoteDatabase> =
        try {
            fcmRepository.updateFCMToken(
                userId = userId,
                newToken = newToken
            )
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.RemoteDatabase.SAVING_ERROR)
        }
}