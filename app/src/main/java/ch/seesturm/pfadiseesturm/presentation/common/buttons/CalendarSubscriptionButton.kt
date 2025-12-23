package ch.seesturm.pfadiseesturm.presentation.common.buttons

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenu
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenuItem
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbar
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarLocation
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import kotlinx.coroutines.launch

@Composable
fun CalendarSubscriptionButton(
    calendar: SeesturmCalendar,
    modifier: Modifier = Modifier
) {

    val showMenu = rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    fun copyToClipboard() {

        clipboardManager.setPrimaryClip(ClipData.newPlainText("Kalender URL", calendar.httpSubscriptionUrl))
        Toast.makeText(context, "URL in Zwischenablage kopiert", Toast.LENGTH_SHORT).show()
    }

    fun showError() {
        coroutineScope.launch {
            SnackbarController.showSnackbar(
                snackbar = SeesturmSnackbar.Error(
                    message = "Beim Hinzufügen des Kalenders ist ein unbekannter Fehler aufgetreten.",
                    onDismiss = {},
                    location = SeesturmSnackbarLocation.Default,
                    allowManualDismiss = true
                )
            )
        }
    }

    fun subscribeInGoogleCalendar() {

        try {
            val appUri = "https://calendar.google.com/calendar/u/0?cid=${calendar.calendarId}".toUri()
            val browserUri = "https://calendar.google.com/calendar/u/0/r?cid=${calendar.calendarId}".toUri()

            val appIntent = Intent(Intent.ACTION_VIEW, appUri)
            appIntent.setPackage("com.google.android.calendar")
            val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)

            when {
                appIntent.resolveActivity(context.packageManager) != null -> {
                    context.startActivity(appIntent)
                }
                browserIntent.resolveActivity(context.packageManager) != null -> {
                    context.startActivity(browserIntent)
                }
                else -> {
                    showError()
                }
            }
        }
        catch (e: Exception) {
            showError()
        }
    }

    Box(
        modifier = modifier
    ) {
        IconButton(
            onClick = {
                showMenu.value = true
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                tint = if (calendar.isLeitungsteam) {
                    Color.SEESTURM_RED
                }
                else {
                    Color.SEESTURM_GREEN
                }
            )
        }

        ThemedDropdownMenu(
            expanded = showMenu.value,
            onDismissRequest = { showMenu.value = false }
        ) {
            Text(
                text = "Kalender abonnieren",
                modifier = Modifier
                    .padding(8.dp)
            )

            HorizontalDivider()

            ThemedDropdownMenuItem(
                text = {
                    Text("Google Kalender")
                },
                onClick = {
                    subscribeInGoogleCalendar()
                    showMenu.value = false
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.logo_google),
                        contentDescription = null,
                        tint = Color.SEESTURM_GREEN,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            )
            ThemedDropdownMenuItem(
                text = {
                    Text("URL Kopieren")
                },
                onClick = {
                    copyToClipboard()
                    showMenu.value = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = Color.SEESTURM_GREEN
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun CalendarSubscriptionButtonPreview1() {
    PfadiSeesturmTheme {
        CalendarSubscriptionButton(
            calendar = SeesturmCalendar.TERMINE
        )
    }
}
@Preview
@Composable
private fun CalendarSubscriptionButtonPreview2() {
    PfadiSeesturmTheme {
        CalendarSubscriptionButton(
            calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM
        )
    }
}