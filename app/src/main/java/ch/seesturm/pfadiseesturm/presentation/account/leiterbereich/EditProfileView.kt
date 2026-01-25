package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich

import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.storage.model.ProfilePicture
import ch.seesturm.pfadiseesturm.main.AllowedOrientation
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.main.AuthViewModel
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components.CircleProfilePictureView
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components.CircleProfilePictureViewType
import ch.seesturm.pfadiseesturm.presentation.common.alert.SimpleAlert
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumn
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumnItemLeadingContentType
import ch.seesturm.pfadiseesturm.presentation.common.profile_picture_cropper.ProfilePictureCropperView
import ch.seesturm.pfadiseesturm.presentation.common.sheet.LocalScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbar
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarLocation
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.PhotoSliderView
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.PhotoSliderViewItem
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.PhotoSliderViewMode
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.AnonymousActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import kotlinx.coroutines.launch

@Composable
fun EditProfileView(
    user: FirebaseHitobitoUser,
    viewModel: EditProfileViewModel,
    appStateViewModel: AppStateViewModel,
    authViewModel: AuthViewModel,
    onDeleteAccount: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    var imageUploadState by retain { mutableStateOf<ActionState<Unit>>(ActionState.Idle) }
    var imageDeleteState by retain { mutableStateOf<ActionState<Unit>>(ActionState.Idle) }

    val isCircularImageViewLoading: Boolean by remember {
        derivedStateOf {
            imageUploadState.isLoading ||
            imageDeleteState.isLoading ||
            uiState.imageSelectionState.isLoading
        }
    }

    val photosPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        viewModel.updateSelectedImageUri(uri, context)
    }

    fun uploadProfilePicture(picture: ProfilePicture) {

        imageUploadState = ActionState.Loading(Unit)

        scope.launch {
            when (val result = authViewModel.uploadProfilePicture(picture)) {
                is SeesturmResult.Error -> {
                    val message = result.error.defaultMessage
                    imageUploadState = ActionState.Error(Unit, message)
                    SnackbarController.showSnackbar(
                        snackbar = SeesturmSnackbar.Error(
                            message = message,
                            onDismiss = {
                                imageUploadState = ActionState.Idle
                            },
                            location = SeesturmSnackbarLocation.Sheet,
                            allowManualDismiss = true
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    val message = "Das Profilbild wurde erfolgreich gespeichert."
                    imageUploadState = ActionState.Success(Unit, message)
                    SnackbarController.showSnackbar(
                        snackbar = SeesturmSnackbar.Success(
                            message = message,
                            onDismiss = {
                                imageUploadState = ActionState.Idle
                            },
                            location = SeesturmSnackbarLocation.Sheet,
                            allowManualDismiss = true
                        )
                    )
                }
            }
        }

    }

    fun deleteProfilePicture() {

        imageDeleteState = ActionState.Loading(Unit)

        scope.launch {
            when (val result = authViewModel.deleteProfilePicture()) {
                is SeesturmResult.Error -> {
                    val message = result.error.defaultMessage
                    imageDeleteState = ActionState.Error(Unit, message)
                    SnackbarController.showSnackbar(
                        snackbar = SeesturmSnackbar.Error(
                            message = message,
                            onDismiss = {
                                imageDeleteState = ActionState.Idle
                            },
                            location = SeesturmSnackbarLocation.Sheet,
                            allowManualDismiss = true
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    val message = "Das Profilbild wurde erfolgreich gelöscht."
                    imageDeleteState = ActionState.Success(Unit, message)
                    SnackbarController.showSnackbar(
                        snackbar = SeesturmSnackbar.Success(
                            message = message,
                            onDismiss = {
                                imageDeleteState = ActionState.Idle
                            },
                            location = SeesturmSnackbarLocation.Sheet,
                            allowManualDismiss = true
                        )
                    )
                }
            }
        }
    }

    SimpleAlert(
        isShown = uiState.showDeleteImageAlert,
        title = "Profilbild löschen",
        description = "Möchtest du dein Profilbild wirklich löschen?",
        icon = Icons.Outlined.Delete,
        confirmButtonText = "Löschen",
        onConfirm = {
            deleteProfilePicture()
        },
        onDismiss = {
            viewModel.updateDeleteImageAlertVisibility(false)
        },
        isConfirmButtonCritical = true
    )

    val currentImageSelectionState = uiState.imageSelectionState

    if (currentImageSelectionState is AnonymousActionState.Success) {
        Dialog(
            onDismissRequest = {
                viewModel.updateSelectedImageUri(null, context)
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            BoxWithConstraints {
                ProfilePictureCropperView(
                    image = currentImageSelectionState.action,
                    viewDpSize = DpSize(width = maxWidth, height = maxHeight),
                    onCrop = { result ->
                        viewModel.updateSelectedImageUri(null, context)
                        when (result) {
                            is SeesturmResult.Error -> {
                                scope.launch {
                                    SnackbarController.showSnackbar(
                                        snackbar = SeesturmSnackbar.Error(
                                            message = "Das Bild konnte nicht zugeschnitten werden.",
                                            onDismiss = {},
                                            location = SeesturmSnackbarLocation.Sheet,
                                            allowManualDismiss = true
                                        )
                                    )
                                }
                            }
                            is SeesturmResult.Success -> {
                                uploadProfilePicture(result.data)
                            }
                        }
                    },
                    onCancel = {
                        viewModel.updateSelectedImageUri(null, context)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }

    var profilePictureToDisplay by retain { mutableStateOf<PhotoSliderViewItem?>(null) }
    val localProfilePictureToDisplay = profilePictureToDisplay

    if (localProfilePictureToDisplay != null) {
        Dialog(
            onDismissRequest = {
                profilePictureToDisplay = null
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            PhotoSliderView(
                mode = PhotoSliderViewMode.Single(
                    image = localProfilePictureToDisplay
                ),
                onClose = {
                    profilePictureToDisplay = null
                    appStateViewModel.updateAllowedOrientation(AllowedOrientation.PortraitOnly)
                },
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }

    EditProfileContentView(
        user = user,
        profilePictureType = if (isCircularImageViewLoading) {
            CircleProfilePictureViewType.Loading
        }
        else {
            CircleProfilePictureViewType.Idle(user)
        },
        imageUploadState = imageUploadState,
        onLaunchImagePicker = {
            photosPicker.launch(
                PickVisualMediaRequest(
                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        },
        onDeleteAccount = onDeleteAccount,
        onSignOut = onSignOut,
        onDeleteProfilePicture = {
            viewModel.updateDeleteImageAlertVisibility(true)
        },
        onProfilePictureClick = { url ->
            profilePictureToDisplay = PhotoSliderViewItem(
                url = url,
                aspectRatio = 1f
            )
            appStateViewModel.updateAllowedOrientation(AllowedOrientation.All)
        },
        modifier = modifier
    )
}

@Composable
private fun EditProfileContentView(
    user: FirebaseHitobitoUser,
    profilePictureType: CircleProfilePictureViewType,
    imageUploadState: ActionState<Unit>,
    onLaunchImagePicker: () -> Unit,
    onDeleteAccount: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteProfilePicture: () -> Unit,
    onProfilePictureClick: (String) -> Unit,
    modifier: Modifier
) {

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        GroupedColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            section {
                customItem {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircleProfilePictureView(
                            type = profilePictureType,
                            size = 120.dp,
                            onClick = if (user.profilePictureUrl != null) {
                                { onProfilePictureClick(user.profilePictureUrl) }
                            } else {
                                null
                            },
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                        )
                        Text(
                            text = user.displayNameFull,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        )
                        if (user.email != null) {
                            Text(
                                text = user.email,
                                style = MaterialTheme.typography.bodyMedium,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
            section {
                textItem(
                    text = "Profilbild wählen",
                    textColor = { Color.SEESTURM_GREEN },
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.Outlined.PhotoLibrary
                    ),
                    onClick = onLaunchImagePicker
                )
                textItem(
                    text = "Profilbild löschen",
                    textColor = if (user.profilePictureUrl != null) {
                        { Color.SEESTURM_RED }
                    } else {
                        { MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f) }
                    },
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.Outlined.Delete,
                        color = if (user.profilePictureUrl != null) {
                            { Color.SEESTURM_RED }
                        } else {
                            { MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f) }
                        }
                    ),
                    onClick = if (user.profilePictureUrl != null) {
                        { onDeleteProfilePicture() }
                    } else {
                        null
                    }
                )
            }
            section {
                textItem(
                    text = "Abmelden",
                    textColor = { Color.SEESTURM_GREEN },
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout
                    ),
                    onClick = onSignOut
                )
                textItem(
                    text = "App-Account löschen",
                    textColor = { Color.SEESTURM_RED },
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.Outlined.PersonRemove,
                        color = { Color.SEESTURM_RED }
                    ),
                    onClick = onDeleteAccount
                )
            }
        }
        if (imageUploadState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(),
                color = Color.SEESTURM_GREEN,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Preview("Loading", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Loading", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EditProfileViewPreview1() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            EditProfileContentView(
                user = DummyData.user1,
                profilePictureType = CircleProfilePictureViewType.Loading,
                imageUploadState = ActionState.Loading(Unit),
                onLaunchImagePicker = {},
                onDeleteAccount = {},
                onSignOut = {},
                onDeleteProfilePicture = {},
                onProfilePictureClick = {},
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}

@Preview("Idle", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Idle", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EditProfileViewPreview2() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            EditProfileContentView(
                user = DummyData.user3,
                profilePictureType = CircleProfilePictureViewType.Idle(DummyData.user3),
                imageUploadState = ActionState.Idle,
                onLaunchImagePicker = {},
                onDeleteAccount = {},
                onSignOut = {},
                onDeleteProfilePicture = {},
                onProfilePictureClick = {},
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}