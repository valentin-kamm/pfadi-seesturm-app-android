package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Textsms
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.domain.firestore.model.Schoepflialarm
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIcon
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonColor
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeaderMode
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumn
import ch.seesturm.pfadiseesturm.presentation.common.sheet.LocalScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextField
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.pushNotifcationToggle
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.SchoepflialarmReactionType
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun SchoepflialarmSheet(
    schoepflialarmResult: UiState<Schoepflialarm>,
    user: FirebaseHitobitoUser,
    newSchoepflialarmMessage: SeesturmTextFieldState,
    onSubmit: () -> Unit,
    onReaction: (SchoepflialarmReactionType) -> Unit,
    isSubmitButtonLoading: Boolean,
    isReactionButtonLoading: (SchoepflialarmReactionType) -> Boolean,
    onPushNotificationToggle: (Boolean) -> Unit,
    notificationTopicsReadingState: UiState<Set<SeesturmFCMNotificationTopic>>,
    togglePushNotificationState: ActionState<SeesturmFCMNotificationTopic>,
    modifier: Modifier = Modifier,
    columnState: LazyListState = rememberLazyListState()
) {

    val hazeState = remember { HazeState() }

    val isReactionButtonDisabled: Boolean = if (schoepflialarmResult is UiState.Success) {
        isReactionButtonLoading(SchoepflialarmReactionType.Coming) ||
        isReactionButtonLoading(SchoepflialarmReactionType.NotComing) ||
        isReactionButtonLoading(SchoepflialarmReactionType.AlreadyThere) ||
        schoepflialarmResult.data.reactions.map { it.user?.userId ?: "" }.contains(user.userId)
    }
    else {
        true
    }

    val isSendingSchoepflialarmDisabled: Boolean = if (schoepflialarmResult is UiState.Success) {
        isSubmitButtonLoading
    }
    else {
        true
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .fillMaxSize()
    ) {
        GroupedColumn(
            state = columnState,
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 120.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .then(
                    if (Build.VERSION.SDK_INT >= 30) {
                        Modifier
                            .hazeSource(hazeState)
                    } else {
                        Modifier
                    }
                )
        ) {
            when (schoepflialarmResult) {
                UiState.Loading -> {
                    section {
                        customItem(
                            key = "SchoepflialarmSheetSchoepflialarmMessageLoadingHeader"
                        ) {
                            CustomCardView {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .graphicsLayer()
                                                .size(30.dp)
                                                .customLoadingBlinking()
                                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                                                .wrapContentSize()
                                        )
                                        RedactedText(
                                            numberOfLines = 1,
                                            textStyle = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    RedactedText(
                                        numberOfLines = 1,
                                        textStyle = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                    SchoepflialarmReactionType.entries.sortedBy { it.sortingOrder }.forEach { reactionType ->
                        section(
                            header = {
                                RedactedText(
                                    numberOfLines = 1,
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    lastLineFraction = 0.4f,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp, horizontal = 16.dp)
                                        .fillMaxWidth()
                                )
                            }
                        ) {
                            textItem(
                                key = "SchoepflialarmSheetLoadingReactionSection${reactionType.rawValue}",
                                text = "Keine Reaktionen",
                                isLoading = true
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    section {
                        customItem(
                            key = "SchoepflialarmSheetErrorCell"
                        ) {
                            ErrorCardView(
                                errorDescription = schoepflialarmResult.message
                            )
                        }
                    }
                }
                is UiState.Success -> {
                    section {
                        customItem(
                            key = "SchoepflialarmSheetSchoepflialarmMessageHeader"
                        ) {
                            CustomCardView {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        CircleProfilePictureView(
                                            type = CircleProfilePictureViewType.Idle(schoepflialarmResult.data.user),
                                            size = 30.dp
                                        )
                                        Text(
                                            text = schoepflialarmResult.data.user?.displayNameShort ?: "Unbekannter Benutzer",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, hyphens = Hyphens.Auto),
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 2,
                                            textAlign = TextAlign.Start,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier
                                                .weight(1f)
                                        )
                                        Text(
                                            text = schoepflialarmResult.data.createdFormatted,
                                            style = MaterialTheme.typography.labelSmall.copy(hyphens = Hyphens.Auto),
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.End,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            maxLines = 2,
                                            modifier = Modifier
                                                .weight(1f)
                                                .alpha(0.4f)
                                        )
                                    }
                                    Text(
                                        text = schoepflialarmResult.data.message,
                                        style = MaterialTheme.typography.bodyMedium.copy(hyphens = Hyphens.Auto),
                                        textAlign = TextAlign.Start,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                    SchoepflialarmReactionType.entries.sortedBy { it.sortingOrder }.forEach { reactionType ->
                        val reactions = schoepflialarmResult.data.reactions(reactionType)
                        section(
                            header = {
                                TextWithIcon(
                                    imageVector = reactionType.icon,
                                    type = TextWithIconType.Text(
                                        text = "${reactionType.title.uppercase()} (${reactions.count()})",
                                        textStyle = { MaterialTheme.typography.bodySmall }
                                    ),
                                    iconTint = reactionType.color,
                                    textColor = MaterialTheme.colorScheme.onBackground.copy(
                                        alpha = 0.4f
                                    ),
                                    modifier = Modifier
                                        .padding(vertical = 8.dp, horizontal = 16.dp)
                                )
                            }
                        ) {
                            if (reactions.isNotEmpty()) {
                                items(
                                    items = reactions,
                                    key = { reaction ->
                                        reaction.id
                                    }
                                ) { reaction ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(
                                            16.dp
                                        ),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        CircleProfilePictureView(
                                            type = CircleProfilePictureViewType.Idle(reaction.user),
                                            size = 20.dp
                                        )
                                        Text(
                                            text = reaction.user?.displayNameShort
                                                ?: "Unbekannter Benutzer",
                                            style = MaterialTheme.typography.bodyMedium.copy(hyphens = Hyphens.Auto),
                                            textAlign = TextAlign.Start,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        )
                                        Text(
                                            text = reaction.createdFormatted,
                                            style = MaterialTheme.typography.labelSmall.copy(hyphens = Hyphens.Auto),
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.End,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            maxLines = 2,
                                            modifier = Modifier
                                                .alpha(0.4f)
                                        )
                                    }
                                }
                            }
                            else {
                                textItem(
                                    key = "SchoepflialarmSheetNoReactionCell${reactionType.rawValue}",
                                    text = "Keine Reaktionen",
                                    textStyle = { MaterialTheme.typography.bodyMedium }
                                )
                            }
                        }
                    }
                    section(
                        header = {
                            BasicListHeader(mode = BasicListHeaderMode.Normal("Reagieren".uppercase()))
                        }
                    ) {
                        item(
                            key = "SchoepflialarmSheetReactionButtons"
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                            ) {
                                SchoepflialarmReactionType.entries.sortedBy { it.sortingOrder }
                                    .forEach { reaction ->
                                        SeesturmButton(
                                            type = SeesturmButtonType.Secondary,
                                            colors = SeesturmButtonColor.Custom(
                                                buttonColor = reaction.color,
                                                contentColor = reaction.onReactionColor,
                                            ),
                                            icon = SeesturmButtonIconType.Predefined(
                                                icon = reaction.icon
                                            ),
                                            title = null,
                                            onClick = {
                                                onReaction(reaction)
                                            },
                                            isLoading = isReactionButtonLoading(reaction),
                                            enabled = !isReactionButtonDisabled,
                                            modifier = Modifier
                                                .weight(1f)
                                        )
                                    }
                            }
                        }
                        pushNotifcationToggle(
                            topic = SeesturmFCMNotificationTopic.SchoepflialarmReaction,
                            state = notificationTopicsReadingState,
                            actionState = togglePushNotificationState,
                            onToggle = {
                                onPushNotificationToggle(it)
                            },
                            key = "SchoepflialarmSheetPushNotificationToggle"
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (Build.VERSION.SDK_INT >= 30) {
                        Modifier
                            .background(Color.Transparent)
                            .hazeEffect(hazeState, style = CupertinoMaterials.thin())
                    } else {
                        Modifier
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                    }
                )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SeesturmTextField(
                    state = newSchoepflialarmMessage,
                    leadingIcon = Icons.Outlined.Textsms,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .weight(1f),
                    enabled = !isSendingSchoepflialarmDisabled
                )
                SeesturmButton(
                    type = SeesturmButtonType.Icon,
                    colors = SeesturmButtonColor.Custom(
                        buttonColor = Color.SEESTURM_GREEN,
                        contentColor = Color.White,
                    ),
                    icon = SeesturmButtonIconType.Predefined(
                        icon = Icons.AutoMirrored.Filled.Send
                    ),
                    title = null,
                    onClick = onSubmit,
                    isLoading = isSubmitButtonLoading,
                    enabled = !isSendingSchoepflialarmDisabled
                )
            }
        }
    }
}

@Preview("Loading", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Loading", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SchoepflialarmSheetPreview1() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            SchoepflialarmSheet(
                schoepflialarmResult = UiState.Loading,
                user = DummyData.user1,
                newSchoepflialarmMessage = SeesturmTextFieldState(
                    text = "Hallo",
                    label = "Schöpflialarm",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = {}
                ),
                onSubmit = {},
                onReaction = {},
                isSubmitButtonLoading = false,
                isReactionButtonLoading = { false },
                onPushNotificationToggle = {},
                notificationTopicsReadingState = UiState.Loading,
                togglePushNotificationState = ActionState.Idle,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}
@Preview("Error", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Error", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SchoepflialarmSheetPreview2() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            SchoepflialarmSheet(
                schoepflialarmResult = UiState.Error("Schwerer Fehler"),
                user = DummyData.user1,
                newSchoepflialarmMessage = SeesturmTextFieldState(
                    text = "Hallo",
                    label = "Schöpflialarm",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = {}
                ),
                onSubmit = {},
                onReaction = {},
                isSubmitButtonLoading = false,
                isReactionButtonLoading = { false },
                onPushNotificationToggle = {},
                notificationTopicsReadingState = UiState.Error("Schwerer Fehler"),
                togglePushNotificationState = ActionState.Idle,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}
@Preview("Success", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Success", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SchoepflialarmSheetPreview3() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            SchoepflialarmSheet(
                schoepflialarmResult = UiState.Success(DummyData.schoepflialarm.copy(
                    createdFormatted = "Sonntag, 22. Juni, 00:00 Uhr",
                    user = DummyData.user1.copy(pfadiname = "Ein ganz langer Pfadiname ")
                )),
                user = DummyData.user1,
                newSchoepflialarmMessage = SeesturmTextFieldState(
                    text = "Hallo",
                    label = "Schöpflialarm",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = {}
                ),
                onSubmit = {},
                onReaction = {},
                isSubmitButtonLoading = false,
                isReactionButtonLoading = { false },
                onPushNotificationToggle = {},
                notificationTopicsReadingState = UiState.Success(setOf(SeesturmFCMNotificationTopic.SchoepflialarmReaction)),
                togglePushNotificationState = ActionState.Idle,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}