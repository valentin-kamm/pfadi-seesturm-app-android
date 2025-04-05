package ch.seesturm.pfadiseesturm.presentation.common.forms

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.House
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.repository.PhotosRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.wordpress.service.PhotosService
import ch.seesturm.pfadiseesturm.presentation.common.components.RedactedText
import ch.seesturm.pfadiseesturm.presentation.mehr.MehrHorizontalPhotoScrollView
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.PfadijahreViewModel
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random

@Composable
fun <T>FormItem(
    items: List<T>,
    index: Int,
    mainContent: FormItemContentType,
    leadingIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    trailingElement: FormItemTertiaryElementType = FormItemTertiaryElementType.Blank,
    modifier: Modifier = Modifier,
    separatorColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
    separatorWidth: Float = 0.87f
) {
    val isFirst = index == 0
    val isLast = index == items.lastIndex

    val shape = when {
        isFirst && isLast -> RoundedCornerShape(16.dp)
        isFirst -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        isLast -> RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        else -> RoundedCornerShape(0.dp)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier
                        .clickable {
                            onClick()
                        }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.BottomEnd
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    when (mainContent) {
                        is FormItemContentType.Text -> {
                            Modifier
                                .padding(horizontal = 16.dp)
                                .padding(vertical = 14.dp)
                        }
                        is FormItemContentType.Custom -> {
                            Modifier
                                .padding(mainContent.contentPadding)
                        }
                    }
                )
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = Color.SEESTURM_GREEN,
                    modifier = Modifier
                        .size(20.dp)
                        .wrapContentSize()
                )
            }
            when (mainContent) {
                is FormItemContentType.Text -> {
                    val textColor = when (mainContent.textColor) {
                        is FormItemTextContentColor.Custom -> { mainContent.textColor.color }
                        FormItemTextContentColor.Default -> { MaterialTheme.colorScheme.onBackground }
                    }
                    if (mainContent.isLoading) {
                        RedactedText(
                            numberOfLines = 1,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            lastLineFraction = Random.nextFloat() * (0.8f - 0.4f) + 0.4f,
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                    else {
                        Text(
                            text = mainContent.title,
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }
                is FormItemContentType.Custom -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        mainContent.content()
                    }
                }
            }
            when (trailingElement) {
                is FormItemTertiaryElementType.Blank -> {}
                is FormItemTertiaryElementType.DisclosureIndicator -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier
                            .size(14.dp)
                            .alpha(0.4f)
                    )
                }
                is FormItemTertiaryElementType.Custom -> {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                    ) {
                        trailingElement.content()
                    }
                }
            }
        }
        if (index != items.lastIndex) {
            HorizontalDivider(
                color = separatorColor,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth(separatorWidth)
            )
        }
    }
}

sealed class FormItemContentType {
    data class Text(
        val title: String,
        val isLoading: Boolean = false,
        val textColor: FormItemTextContentColor = FormItemTextContentColor.Default,
    ): FormItemContentType()
    data class Custom(
        val content: @Composable () -> Unit,
        val contentPadding: PaddingValues = PaddingValues(0.dp)
    ): FormItemContentType()
}

sealed class FormItemTextContentColor {
    data class Custom(
        val color: Color
    ): FormItemTextContentColor()
    data object Default: FormItemTextContentColor()
}

sealed class FormItemTertiaryElementType {
    data object DisclosureIndicator: FormItemTertiaryElementType()
    data object Blank: FormItemTertiaryElementType()
    data class Custom(
        val content: @Composable () -> Unit
    ): FormItemTertiaryElementType()
}

@Preview("Text Content")
@Composable
fun FormItemPreview1() {
    FormItem(
        items = (0..<2).toList(),
        index = 0,
        mainContent = FormItemContentType.Text(
            title = "Fotos"
        ),
        leadingIcon = Icons.Default.House,
    )
}
@SuppressLint("ViewModelConstructorInComposable")
@Preview("Custom Content")
@Composable
fun FormItemPreview2() {
    FormItem(
        items = (0..<2).toList(),
        index = 0,
        mainContent = FormItemContentType.Custom(
            content = {
                MehrHorizontalPhotoScrollView(
                    viewModel = PfadijahreViewModel(
                        service = PhotosService(
                            repository = PhotosRepositoryImpl(
                                api = Retrofit.Builder()
                                    .baseUrl(Constants.SEESTURM_API_BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build()
                                    .create(WordpressApi::class.java)
                            )
                        )
                    ),
                    mehrNavController = rememberNavController()
                )
            }
        )
    )
}