package ch.seesturm.pfadiseesturm.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult

fun subscribeToCalendar(subscriptionUrl: String, context: Context): SeesturmResult<Unit, DataError.Network> {
    try {
        val browserIntent = Intent(Intent.ACTION_VIEW, subscriptionUrl.toUri())
        context.startActivity(browserIntent)
        return SeesturmResult.Success(Unit)
    }
    catch (e: Exception) {
        return SeesturmResult.Error(DataError.Network.IO_EXCEPTION)
    }
}