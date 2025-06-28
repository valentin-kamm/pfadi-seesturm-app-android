package ch.seesturm.pfadiseesturm.presentation.common.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import ch.seesturm.pfadiseesturm.R

@Composable
fun AppDestination.MainTabView.Destinations.GetIcon(
    isSelected: Boolean
) {
    return when (this) {
        AppDestination.MainTabView.Destinations.Aktuell -> {
            Icon(
                Icons.Filled.Newspaper,
                contentDescription = "Aktuell"
            )
        }
        AppDestination.MainTabView.Destinations.Home -> {
            Icon(
                painter = painterResource(id = R.drawable.logotabbar),
                contentDescription = "Home"
            )
        }
        AppDestination.MainTabView.Destinations.Account -> {
            Icon(
                if (isSelected) Icons.Filled.AccountCircle else Icons.Outlined.AccountCircle,
                contentDescription = "Account"
            )
        }
        AppDestination.MainTabView.Destinations.Anlaesse -> {
            Icon(
                if (isSelected) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                contentDescription = "Anlässe"
            )
        }
        AppDestination.MainTabView.Destinations.Mehr -> {
            Icon(
                Icons.Filled.MoreHoriz,
                contentDescription = "Mehr"
            )
        }
    }
}