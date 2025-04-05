package ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.House
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemTertiaryElementType
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState

@Composable
fun PushNotificationToggle(
    items: List<SeesturmFCMNotificationTopic>,
    topic: SeesturmFCMNotificationTopic,
    state: UiState<Set<SeesturmFCMNotificationTopic>>,
    actionState: ActionState<SeesturmFCMNotificationTopic>,
    isOn: Boolean,
    modifier: Modifier = Modifier,
    onToggle: (Boolean) -> Unit
) {

    var isPushNotificationToggleLoading: Boolean = when (actionState) {
        is ActionState.Loading -> {
            actionState.action == topic
        }
        else -> { false }
    }

    val disabledAlpha = 0.6f

    FormItem(
        items = items,
        index = items.indexOf(topic),
        modifier = modifier,
        mainContent = FormItemContentType.Custom(
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 4.dp,
                bottom = 4.dp
            ),
            content = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = topic.topicName,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                    )
                    if (isPushNotificationToggleLoading) {
                        CircularProgressIndicator(
                            color = Color.SEESTURM_GREEN,
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                }
            }
        ),
        trailingElement = FormItemTertiaryElementType.Custom(
            content = {
                Checkbox(
                    checked = isOn,
                    onCheckedChange = onToggle,
                    enabled = !actionState.isLoading && !state.isLoading,
                    colors = CheckboxColors(

                        // enabled
                        // checked
                        checkedCheckmarkColor = Color.White,
                        checkedBoxColor = Color.SEESTURM_GREEN,
                        checkedBorderColor = Color.SEESTURM_GREEN,
                        // unchecked
                        uncheckedCheckmarkColor = Color.Transparent,
                        uncheckedBoxColor = Color.Transparent,
                        uncheckedBorderColor = Color.SEESTURM_GREEN,

                        // disabled
                        disabledBorderColor = Color.SEESTURM_GREEN.copy(alpha = disabledAlpha),
                        disabledIndeterminateBorderColor = Color.SEESTURM_GREEN.copy(alpha = disabledAlpha),
                        disabledIndeterminateBoxColor = Color.Transparent,
                        // checked
                        disabledCheckedBoxColor = Color.SEESTURM_GREEN.copy(alpha = disabledAlpha),
                        // unchecked
                        disabledUncheckedBorderColor = Color.SEESTURM_GREEN.copy(alpha = disabledAlpha),
                        disabledUncheckedBoxColor = Color.Transparent
                    )
                )
            }
        )
    )
}

@Preview(showBackground = false)
@Composable
private fun PushNotificationTogglePreview() {
    PushNotificationToggle(
        items = listOf(SeesturmFCMNotificationTopic.Aktuell),
        topic = SeesturmFCMNotificationTopic.Aktuell,
        state = UiState.Loading,
        actionState = ActionState.Loading(SeesturmFCMNotificationTopic.Aktuell),
        isOn = true
    ) { }
}