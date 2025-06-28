package ch.seesturm.pfadiseesturm.util

import android.util.Patterns

val String.isValidUrl: Boolean
    get() = Patterns.WEB_URL.matcher(this).matches()