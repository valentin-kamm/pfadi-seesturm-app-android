package ch.seesturm.pfadiseesturm.util

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

fun launchWebsite(url: String, context: Context) {

    val builder = CustomTabsIntent.Builder()
    builder.setShowTitle(true)
    builder.setShareState(CustomTabsIntent.SHARE_STATE_ON)
    builder.setInstantAppsEnabled(true)
    val customTabsIntent = builder.build()

    // launch custom tab and fall back to normal browser if it does not work
    try {
        customTabsIntent.launchUrl(context, url.toUri())
    }
    catch (e: Exception) {
        val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
        if (browserIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(browserIntent)
        }
    }
}