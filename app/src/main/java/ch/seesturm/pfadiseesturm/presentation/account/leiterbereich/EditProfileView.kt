package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.seesturm.pfadiseesturm.domain.account.service.LeiterbereichService
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components.CircleProfilePictureView
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components.ProfilePictureType
import ch.seesturm.pfadiseesturm.presentation.common.alert.SimpleAlert
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemTextContentColor
import ch.seesturm.pfadiseesturm.presentation.common.image_cropper.CircularImageCropperView
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.AnonymousActionState
import ch.seesturm.pfadiseesturm.util.viewModelFactoryHelper

@Composable
fun EditProfileView(
    viewModelStoreOwner: ViewModelStoreOwner,
    leiterbereichService: LeiterbereichService,
    leiterbereichViewModel: LeiterbereichViewModel,
    dismiss: () -> Unit,
    user: FirebaseHitobitoUser,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val density = LocalDensity.current

    val viewModel: EditProfileViewModel = viewModel(
        factory = viewModelFactoryHelper {
            EditProfileViewModel(
                userId = user.userId,
                leiterbereichService = leiterbereichService
            )
        },
        viewModelStoreOwner = viewModelStoreOwner
    )

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val photosPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        viewModel.updateSelectedImageUri(uri, context)
    }

    SimpleAlert(
        isShown = uiState.showDeleteImageAlert,
        title = "Profilbild löschen",
        description = "Möchtest du dein Profilbild wirklich löschen?",
        icon = Icons.Outlined.Delete,
        confirmButtonText = "Löschen",
        onConfirm = {
            viewModel.deleteProfilePicture()
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

            val viewModelStore = remember { ViewModelStore() }

            val viewModelStoreOwner = remember {
                object : ViewModelStoreOwner {
                    override val viewModelStore: ViewModelStore = viewModelStore
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    viewModelStoreOwner.viewModelStore.clear()
                }
            }

            BoxWithConstraints {

                val size = with(density) {
                    Size(width = maxWidth.toPx(), height = maxHeight.toPx())
                }

                CircularImageCropperView(
                    viewModelStoreOwner = viewModelStoreOwner,
                    viewSize = size,
                    image = currentImageSelectionState.action,
                    onCrop = { cropResult ->
                        viewModel.uploadProfilePicture(cropResult)
                        viewModel.updateSelectedImageUri(null, context)
                    },
                    onCancel = {
                        viewModel.updateSelectedImageUri(null, context)
                    }
                )
            }
        }
    }

    EditProfileContentView(
        user = user,
        profilePictureType = if (viewModel.isCircularImageViewLoading) {
            ProfilePictureType.Loading
        }
        else {
            ProfilePictureType.User(user = user)
        },
        imageUploadState = uiState.imageUploadState,
        onLaunchImagePicker = {
            photosPicker.launch(
                PickVisualMediaRequest(
                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        },
        onSignOut = {
            dismiss()
            leiterbereichViewModel.updateSignOutAlertVisibility(true)
        },
        onDeleteAccount = { 
            dismiss()
            leiterbereichViewModel.updateDeleteAccountAlertVisibility(true)
        },
        onDeleteProfilePicture = {
            viewModel.updateDeleteImageAlertVisibility(true)
        },
        modifier = modifier
    )
}

@Composable
private fun EditProfileContentView(
    user: FirebaseHitobitoUser,
    profilePictureType: ProfilePictureType,
    imageUploadState: ActionState<Unit>,
    onLaunchImagePicker: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    onDeleteProfilePicture: () -> Unit,
    modifier: Modifier
) {

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    CircleProfilePictureView(
                        type = profilePictureType,
                        size = 120.dp
                    )
                    Text(
                        text = user.displayNameFull,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    if (user.email != null) {
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
            item {
                FormItem(
                    items = (0..<2).toList(),
                    index = 0,
                    mainContent = FormItemContentType.Text(
                        title = "Profilbild wählen",
                        textColor = FormItemTextContentColor.Custom(Color.SEESTURM_GREEN)
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),
                    leadingIcon = Icons.Outlined.PhotoLibrary,
                    onClick = onLaunchImagePicker
                )
                FormItem(
                    items = (0..<2).toList(),
                    index = 1,
                    mainContent = FormItemContentType.Text(
                        title = "Profilbild löschen",
                        textColor = FormItemTextContentColor.Custom(
                            if (user.hasProfilePicture) {
                                Color.SEESTURM_RED
                            } else {
                                MaterialTheme.colorScheme.onBackground
                            }
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),
                    leadingIcon = Icons.Outlined.Delete,
                    onClick = if (user.hasProfilePicture) {
                        { onDeleteProfilePicture() }
                    } else {
                        null
                    }
                )
            }
            item {
                FormItem(
                    items = (0..<2).toList(),
                    index = 0,
                    mainContent = FormItemContentType.Text(
                        title = "Abmelden",
                        textColor = FormItemTextContentColor.Custom(Color.SEESTURM_GREEN)
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),
                    leadingIcon = Icons.AutoMirrored.Outlined.Logout,
                    onClick = onSignOut
                )
                FormItem(
                    items = (0..<2).toList(),
                    index = 1,
                    mainContent = FormItemContentType.Text(
                        title = "App-Account löschen",
                        textColor = FormItemTextContentColor.Custom(Color.SEESTURM_RED)
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),
                    leadingIcon = Icons.Outlined.PersonRemove,
                    onClick = onDeleteAccount
                )
            }
        }
        if (imageUploadState is ActionState.Loading) {
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

@Preview
@Composable
private fun EditProfileViewPreview() {
    PfadiSeesturmTheme {
        EditProfileContentView(
            user = DummyData.user1,
            profilePictureType = ProfilePictureType.Loading,
            imageUploadState = ActionState.Loading(Unit),
            onLaunchImagePicker = {},
            onSignOut = {},
            onDeleteAccount = {},
            onDeleteProfilePicture = {},
            modifier = Modifier
        )
    }
}