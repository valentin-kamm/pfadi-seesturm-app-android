package ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIcon
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIconType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar

@Composable
fun AnlassCardView(
    event: GoogleCalendarEvent,
    calendar: SeesturmCalendar,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomCardView(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            CustomCardView(
                shadowColor = Color.Transparent,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier
                    .width(110.dp)
                    .height(85.dp)
                    .wrapContentWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxHeight()
                ) {
                    val firstLine = if (event.endDateFormatted == null) {
                        event.startDayFormatted
                    }
                    else {
                        event.startDayFormatted + " " + event.startMonthFormatted
                    }
                    val secondLine = if (event.endDateFormatted == null) {
                        event.startMonthFormatted
                    }
                    else {
                        "bis " + event.endDateFormatted
                    }
                    Text(
                        text = firstLine,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        color = if (calendar.isLeitungsteam) Color.SEESTURM_RED else Color.SEESTURM_GREEN,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Text(
                        text = secondLine,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                TextWithIcon(
                    type = TextWithIconType.Text(
                        text = event.timeFormatted,
                        textStyle = { MaterialTheme.typography.bodyMedium }
                    ),
                    imageVector = Icons.Outlined.CalendarMonth,
                    textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    iconTint = if (calendar.isLeitungsteam) Color.SEESTURM_RED else Color.SEESTURM_GREEN,
                    maxLines = 1,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                if (event.location != null) {
                    TextWithIcon(
                        type = TextWithIconType.Text(
                            text = event.location,
                            textStyle = { MaterialTheme.typography.bodyMedium }
                        ),
                        imageVector = Icons.Outlined.LocationOn,
                        textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        iconTint = if (calendar.isLeitungsteam) Color.SEESTURM_RED else Color.SEESTURM_GREEN,
                        maxLines = 1,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .wrapContentWidth()
                    .alpha(0.4f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AnlassCardViewPreview() {
    PfadiSeesturmTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnlassCardView(
                event = DummyData.multiDayEvent,
                calendar = SeesturmCalendar.TERMINE,
                onClick = {}
            )
            AnlassCardView(
                event = DummyData.oneDayEvent,
                onClick = {},
                calendar = SeesturmCalendar.TERMINE
            )
            AnlassCardView(
                event = DummyData.allDayMultiDayEvent,
                onClick = {},
                calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM
            )
            AnlassCardView(
                event = DummyData.allDayOneDayEvent,
                onClick = {},
                calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM
            )
        }
    }
}