package ch.seesturm.pfadiseesturm.data.firestore.dto

import com.google.firebase.Timestamp

interface FirestoreDto {

    var id: String?
    var created: Timestamp?
    var modified: Timestamp?

    fun <T: FirestoreDto>contentEquals(other: T): Boolean
}