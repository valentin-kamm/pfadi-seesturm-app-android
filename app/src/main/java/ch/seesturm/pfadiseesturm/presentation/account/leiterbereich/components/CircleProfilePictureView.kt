package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.presentation.common.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.DummyData
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest

@Composable
fun CircleProfilePictureView(
    type: ProfilePictureType,
    size: Dp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    showEditBadge: Boolean = false
) {

    val borderColor = when(type) {
        ProfilePictureType.Loading -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
        is ProfilePictureType.User -> if (type.user?.profilePictureUrl == null) {
            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
        }
        else {
            Color.Transparent
        }
    }

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(1.dp, borderColor, CircleShape)
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .then(
                    if (onClick != null) {
                        Modifier
                            .clickable {
                                onClick()
                            }
                    } else {
                        Modifier
                    }
                )
        ) {
            when (type) {
                ProfilePictureType.Loading -> {
                    CircularProgressIndicator(
                        color = Color.SEESTURM_GREEN,
                        modifier = Modifier
                            .size(18.dp)
                    )
                }
                is ProfilePictureType.User -> {
                    if (type.user?.profilePictureUrl == null) {
                        Image(
                            painter = painterResource(R.drawable.logotabbar),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(2.dp)
                                .fillMaxSize()
                        )
                    }
                    else {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(type.user.profilePictureUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Transparent)
                        ) {
                            when (painter.state) {
                                AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Loading -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer()
                                            .customLoadingBlinking()
                                            .background(MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                                is AsyncImagePainter.State.Error -> {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.onSurfaceVariant)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.HideImage,
                                            contentDescription = null,
                                            tint = Color.SEESTURM_GREEN,
                                            modifier = Modifier
                                                .size(size / 2)
                                        )
                                    }
                                }
                                is AsyncImagePainter.State.Success -> {
                                    SubcomposeAsyncImageContent()
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showEditBadge) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = size / 2.5f, y = size / 2.5f)
                    .size(20.dp)
                    .background(color = Color.White, shape = CircleShape)
                    .border(1.dp, Color.SEESTURM_GREEN, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit, // or your custom pencil.circle.fill icon
                    contentDescription = null,
                    tint = Color.SEESTURM_GREEN,
                    modifier = Modifier
                        .size(15.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

sealed class ProfilePictureType {
    data object Loading: ProfilePictureType()
    data class User(
        val user: FirebaseHitobitoUser?
    ): ProfilePictureType()
}

@Preview("Plain")
@Composable
private fun CircleProfilePictureViewPreview1() {
    PfadiSeesturmTheme {
        CircleProfilePictureView(
            type = ProfilePictureType.User(user = DummyData.user1),
            size = 60.dp,
            modifier = Modifier,
            onClick = {},
            showEditBadge = false
        )
    }
}
@Preview("Custom Image")
@Composable
private fun CircleProfilePictureViewPreview2() {
    PfadiSeesturmTheme {
        CircleProfilePictureView(
            type = ProfilePictureType.User(user = DummyData.user3),
            size = 60.dp,
            modifier = Modifier,
            onClick = {},
            showEditBadge = false
        )
    }
}
@Preview("With Badge")
@Composable
private fun CircleProfilePictureViewPreview3() {
    PfadiSeesturmTheme {
        CircleProfilePictureView(
            type = ProfilePictureType.Loading,
            size = 60.dp,
            modifier = Modifier,
            onClick = {},
            showEditBadge = true
        )
    }
}
@Preview("Loading")
@Composable
private fun CircleProfilePictureViewPreview4() {
    PfadiSeesturmTheme {
        CircleProfilePictureView(
            type = ProfilePictureType.Loading,
            size = 60.dp,
            modifier = Modifier,
            onClick = {},
            showEditBadge = true
        )
    }
}