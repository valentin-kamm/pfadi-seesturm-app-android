package ch.seesturm.pfadiseesturm.domain.fcm.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.accountModule
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.authModule
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.types.SchoepflialarmReactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SchoepflialarmNotificationActionReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val notificationId = intent?.getIntExtra("notificationId", -1)
        val reaction = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getSerializableExtra("reaction", SchoepflialarmReactionType::class.java)
        }
        else {
            intent?.getSerializableExtra("reaction") as? SchoepflialarmReactionType
        }

        if (notificationId == -1 || notificationId == null || reaction == null || context == null) {
            return
        }

        NotificationManagerCompat.from(context).cancel(notificationId)

        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {

            when (val authResult = authModule.authService.reauthenticateOnAppStart()) {
                is SeesturmResult.Error -> {
                    return@launch
                }
                is SeesturmResult.Success -> {
                    accountModule.schoepflialarmService.sendSchoepflialarmReaction(
                        userId = authResult.data.userId,
                        userDisplayNameShort = authResult.data.displayNameShort,
                        reaction = reaction
                    )
                }
            }
        }
    }
}