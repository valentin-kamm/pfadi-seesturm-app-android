package ch.seesturm.pfadiseesturm.domain.firestore.model

import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser

data class FoodOrder(
    var id: String,
    var itemDescription: String,
    var totalCount: Int,
    var userIds: List<String>,
    var users: List<FirebaseHitobitoUser?>,
    var ordersString: String
)