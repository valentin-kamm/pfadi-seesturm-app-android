package ch.seesturm.pfadiseesturm.presentation.anlaesse.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.CalendarSubscriptionAlert
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIcon
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIconType
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.subscribeToCalendar
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle

@Composable
fun AnlaesseDetailView(
    viewModel: AnlaesseDetailViewModel,
    calendar: SeesturmCalendar,
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    CalendarSubscriptionAlert (
        isShown = uiState.showCalendarSubscriptionAlert,
        title = "Kalender kann nicht abonniert werden",
        calendar = calendar,
        onDismiss = {
            viewModel.updateAlertVisibility(false)
        }
    )

    AnlaesseDetailContentView(
        terminState = uiState.eventState,
        calendar = calendar,
        navController = navController,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        onSubscribeToCalendar = {
            val subscriptionResult = subscribeToCalendar(
                subscriptionUrl = calendar.subscriptionUrl,
                context = context
            )
            if (subscriptionResult is SeesturmResult.Error) {
                viewModel.updateAlertVisibility(true)
            }
        },
        onRetry = {
            viewModel.getEvent()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnlaesseDetailContentView(
    terminState: UiState<GoogleCalendarEvent>,
    calendar: SeesturmCalendar,
    navController: NavController,
    bottomNavigationInnerPadding: PaddingValues,
    onSubscribeToCalendar: () -> Unit,
    onRetry: () -> Unit,
    columnState: LazyListState = rememberLazyListState()
) {

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        onNavigateBack = {
            navController.navigateUp()
        },
        actions = {
            IconButton(
                onClick = onSubscribeToCalendar
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = if (calendar.isLeitungsteam) Color.SEESTURM_RED else Color.SEESTURM_GREEN
                )
            }
        }
    ) { topBarInnerPadding ->

        val combinedPadding =
            bottomNavigationInnerPadding.intersectWith(
                other = topBarInnerPadding,
                layoutDirection = LayoutDirection.Ltr,
                additionalEndPadding = 16.dp,
                additionalStartPadding = 16.dp,
                additionalBottomPadding = 16.dp,
                additionalTopPadding = 16.dp
            )

        LazyColumn(
            state = columnState,
            userScrollEnabled = !terminState.scrollingDisabled,
            contentPadding = combinedPadding,
            modifier = Modifier
                .fillMaxSize()
        ) {

            when (terminState) {
                UiState.Loading -> {
                    item(
                        key = "AnlaesseDetailLoadingView"
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                        ) {
                            RedactedText(
                                2,
                                MaterialTheme.typography.displaySmall,
                                lastLineFraction = 0.4f
                            )
                            RedactedText(
                                10,
                                MaterialTheme.typography.bodyLarge,
                                lastLineFraction = 0.75f
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "AnlaesseDetailErrorView"
                    ) {
                        ErrorCardView(
                            modifier = Modifier
                                .animateItem(),
                            errorDescription = terminState.message
                        ) {
                            onRetry()
                        }
                    }
                }
                is UiState.Success -> {
                    item(
                        key = "AnlaesseDetailContentView"
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                terminState.data.title,
                                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            if (calendar.isLeitungsteam) {
                                CustomCardView(
                                    shadowColor = Color.Transparent,
                                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Leitungsteam",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 2,
                                        color = Color.SEESTURM_RED,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    )
                                }
                            }
                            TextWithIcon(
                                type = TextWithIconType.Text(
                                    text = terminState.data.fullDateTimeFormatted,
                                    textStyle = { MaterialTheme.typography.bodyLarge }
                                ),
                                imageVector = Icons.Outlined.CalendarMonth,
                                textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                iconTint = if (calendar.isLeitungsteam) Color.SEESTURM_RED else Color.SEESTURM_GREEN,
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            if (terminState.data.location != null) {
                                TextWithIcon(
                                    type = TextWithIconType.Text(
                                        text = terminState.data.location,
                                        textStyle = { MaterialTheme.typography.bodyLarge }
                                    ),
                                    imageVector = Icons.Outlined.LocationOn,
                                    textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                    iconTint = if (calendar.isLeitungsteam) Color.SEESTURM_RED else Color.SEESTURM_GREEN,
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                            if (terminState.data.description != null) {
                                Text(
                                    text = terminState.data.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview("Loading")
@Composable
private fun AnlaesseDetailViewPreview1() {
    PfadiSeesturmTheme {
        AnlaesseDetailContentView(
            terminState = UiState.Loading,
            calendar = SeesturmCalendar.TERMINE,
            navController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onSubscribeToCalendar = {},
            onRetry = {}
        )
    }
}
@Preview("Error")
@Composable
private fun AnlaesseDetailViewPreview2() {
    PfadiSeesturmTheme {
        AnlaesseDetailContentView(
            terminState = UiState.Error("Schwerer Fehler"),
            calendar = SeesturmCalendar.TERMINE,
            navController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onSubscribeToCalendar = {},
            onRetry = {}
        )
    }
}
@Preview("Multi day event")
@Composable
private fun AnlaesseDetailViewPreview3() {
    PfadiSeesturmTheme {
        AnlaesseDetailContentView(
            terminState = UiState.Success(DummyData.multiDayEvent),
            calendar = SeesturmCalendar.TERMINE,
            navController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onSubscribeToCalendar = {},
            onRetry = {}
        )
    }
}
@Preview("One day event")
@Composable
private fun AnlaesseDetailViewPreview4() {
    PfadiSeesturmTheme {
        AnlaesseDetailContentView(
            terminState = UiState.Success(DummyData.oneDayEvent),
            calendar = SeesturmCalendar.TERMINE,
            navController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onSubscribeToCalendar = {},
            onRetry = {}
        )
    }
}
@Preview("All-day, multi day event")
@Composable
private fun AnlaesseDetailViewPreview5() {
    PfadiSeesturmTheme {
        AnlaesseDetailContentView(
            terminState = UiState.Success(DummyData.allDayMultiDayEvent),
            calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM,
            navController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onSubscribeToCalendar = {},
            onRetry = {}
        )
    }
}
@Preview("All-day, one day event")
@Composable
private fun AnlaesseDetailViewPreview6() {
    PfadiSeesturmTheme {
        AnlaesseDetailContentView(
            terminState = UiState.Success(DummyData.allDayOneDayEvent),
            calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM,
            navController = rememberNavController(),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onSubscribeToCalendar = {},
            onRetry = {}
        )
    }
}