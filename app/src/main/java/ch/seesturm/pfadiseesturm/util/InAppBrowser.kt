package ch.seesturm.pfadiseesturm.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult

fun launchWebsite(url: String, context: Context) {

    val builder = CustomTabsIntent.Builder()
    builder.setShowTitle(true)
    builder.setShareState(CustomTabsIntent.SHARE_STATE_ON)
    builder.setInstantAppsEnabled(true)
    val customTabsIntent = builder.build()

    // launch custom tab and fall back to normal browser if it does not work
    try {
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }
    catch (e: Exception) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (browserIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(browserIntent)
        }
    }
}

fun subscribeToCalendar(subscriptionUrl: String, context: Context): SeesturmResult<Unit, DataError.Network> {
    try {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(subscriptionUrl))
        context.startActivity(browserIntent)
        return SeesturmResult.Success(Unit)
    }
    catch (e: Exception) {
        return SeesturmResult.Error(DataError.Network.IO_EXCEPTION)
    }
}