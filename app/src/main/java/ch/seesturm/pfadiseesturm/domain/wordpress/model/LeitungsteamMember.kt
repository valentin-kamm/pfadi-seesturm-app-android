package ch.seesturm.pfadiseesturm.domain.wordpress.model

import android.util.Patterns

data class LeitungsteamMember(
    val name: String,
    val job: String,
    val contact: String,
    val photo: String
)

val String.toEmail: String?
    get() = if (Patterns.EMAIL_ADDRESS.matcher(this).matches()) {
        this
    }
    else {
        null
    }
