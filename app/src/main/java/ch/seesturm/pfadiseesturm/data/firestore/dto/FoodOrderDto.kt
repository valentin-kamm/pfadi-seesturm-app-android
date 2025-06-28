package ch.seesturm.pfadiseesturm.data.firestore.dto

import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.auth.model.getUsersById
import ch.seesturm.pfadiseesturm.domain.firestore.model.FoodOrder
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.UUID

data class FoodOrderDto(

    @DocumentId override var id: String? = null,
    @ServerTimestamp override var created: Timestamp? = null,
    @ServerTimestamp override var modified: Timestamp? = null,
    val itemDescription: String = "",
    val userIds: List<String> = emptyList()
): FirestoreDto {

    override fun <T : FirestoreDto> contentEquals(other: T): Boolean {
        if (other !is FoodOrderDto) return false
        return id == other.id &&
                itemDescription == other.itemDescription &&
                userIds == other.userIds
    }
}

fun FoodOrderDto.toFoodOrder(users: List<FirebaseHitobitoUser>): FoodOrder {
    return FoodOrder(
        id = id ?: UUID.randomUUID().toString(),
        itemDescription = itemDescription,
        totalCount = userIds.count(),
        userIds = userIds,
        users = users.getUsersById(userIds),
        ordersString = constructOrdersString(userIds, users)
    )
}
private fun constructOrdersString(userIds: List<String>, users: List<FirebaseHitobitoUser>): String {
    val userNames = users.getUsersById(userIds).map { it?.displayNameShort ?: "Unbekannt" }
    val nameCount = userNames.groupingBy { it }.eachCount()
    return nameCount.map { (name, count) -> "$name ($count×)" }.joinToString(", ")
}