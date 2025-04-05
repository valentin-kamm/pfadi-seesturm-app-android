package ch.seesturm.pfadiseesturm.domain.fcm.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import ch.seesturm.pfadiseesturm.domain.fcm.repository.FCMRepository
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmAppError
import ch.seesturm.pfadiseesturm.util.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException

interface PermissionRequester {
    suspend fun requestPermission(): Boolean
}

class FCMSubscriptionService(
    private val repository: FCMRepository,
    private val context: Context
) {

    suspend fun subscribe(
        topic: SeesturmFCMNotificationTopic,
        requestPermission: suspend () -> Boolean
    ): SeesturmResult<SeesturmFCMNotificationTopic, DataError.Messaging> {
        return try {
            // check or request permissions
            requestOrCheckNotificationPermission(requestPermission)
            // subscribe
            repository.subscribeToTopic(topic)
            repository.addNewTopic(topic)
            SeesturmResult.Success(topic)
        }
        catch (e: Exception) {
            SeesturmResult.Error(
                when (e) {
                    is PfadiSeesturmAppError.MessagingPermissionError -> {
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
    }
    suspend fun unsubscribe(topic: SeesturmFCMNotificationTopic): SeesturmResult<SeesturmFCMNotificationTopic, DataError.Messaging> {
        return try {
            repository.unsubscribeFromTopic(topic)
            repository.deleteTopic(topic)
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
    }

    private suspend fun requestOrCheckNotificationPermission(
        requestPermission: suspend () -> Boolean
    ) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            if (!hasNotificationPermission()) {
                val isGranted = requestPermission()
                if (!isGranted) {
                    throw PfadiSeesturmAppError.MessagingPermissionError("Berechtigung für Push-Nachrichten nicht erteilt.")
                }
            }
        }
    }
    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
        else {
            true
        }
    }

    fun readSubscribedTopics(): Flow<SeesturmResult<Set<SeesturmFCMNotificationTopic>, DataError.Messaging>> {
        return repository.getSubscribedTopics()
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
    }
}