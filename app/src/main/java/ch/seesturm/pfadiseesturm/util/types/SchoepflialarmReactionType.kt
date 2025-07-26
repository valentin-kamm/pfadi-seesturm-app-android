package ch.seesturm.pfadiseesturm.util.types

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmAppError
import kotlinx.serialization.Serializable

@Serializable
enum class SchoepflialarmReactionType {
    Coming,
    NotComing,
    AlreadyThere;

    val rawValue: String
        get() = when (this) {
            Coming -> "coming"
            NotComing -> "notComing"
            AlreadyThere -> "alreadyThere"
        }

    val sortingOrder: Int
        get() = when (this) {
            Coming -> 10
            AlreadyThere -> 20
            NotComing -> 30
        }

    val title: String
        get() = when (this) {
            Coming -> "Bin unterwegs"
            NotComing -> "Heute nicht"
            AlreadyThere -> "Schon da"
        }

    val icon: ImageVector
        get() = when (this) {
            Coming -> Icons.Filled.CheckCircle
            NotComing -> Icons.Filled.Cancel
            AlreadyThere -> Icons.Filled.Home
        }

    val iconRef: Int
        get() = when (this) {
            Coming -> R.drawable.baseline_check_circle_outline_24
            NotComing -> R.drawable.outline_cancel_24
            AlreadyThere -> R.drawable.outline_home_24
        }
    val color: Color
        @Composable
        get() = when (this) {
            Coming -> Color.SEESTURM_GREEN
            NotComing -> Color.SEESTURM_RED
            AlreadyThere -> MaterialTheme.colorScheme.onBackground
        }
    val onReactionColor: Color
        @Composable
        get() = when (this) {
            Coming -> Color.White
            NotComing -> Color.White
            AlreadyThere -> MaterialTheme.colorScheme.background
        }

    companion object {
        fun fromString(rawValue: String): SchoepflialarmReactionType {
            return when (rawValue) {
                "coming" -> Coming
                "notComing" -> NotComing
                "alreadyThere" -> AlreadyThere
                else -> {
                    throw PfadiSeesturmAppError.UnknownSchoepflialarmReactionType("Unbekannte Reaktions-Art für Schöpflialarm")
                }
            }
        }
    }
}