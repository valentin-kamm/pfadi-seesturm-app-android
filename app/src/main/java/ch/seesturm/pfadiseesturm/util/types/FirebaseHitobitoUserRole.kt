package ch.seesturm.pfadiseesturm.util.types

import ch.seesturm.pfadiseesturm.data.auth.FirebaseUserClaims
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmAppError

enum class FirebaseHitobitoUserRole(val role: String) {
    User("hitobito_user"),
    Admin("hitobito_admin");

    companion object {
        fun fromClaims(claims: FirebaseUserClaims): FirebaseHitobitoUserRole {
            val roleClaim = claims["role"] as? String
            return entries.find { it.role == roleClaim }
                ?: throw PfadiSeesturmAppError.AuthError("Du hast keine Berechtigung, um dich in der Pfadi Seesturm App anzumelden. Melde dich erneut via MiData an.")
        }
        fun fromRole(role: String): FirebaseHitobitoUserRole {
            return entries.find { it.role == role }
                ?: throw PfadiSeesturmAppError.AuthError("Unbekannte Rolle. Melde dich erneut via MiData an.")
        }
    }
}