package ch.seesturm.pfadiseesturm.presentation.mehr

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ContactMail
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Textsms
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.repository.PhotosRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.wordpress.service.PhotosService
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemTertiaryElementType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemTextContentColor
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.PfadijahreViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components.PhotoGalleryCell
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components.PhotoGalleryLoadingCell
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import ch.seesturm.pfadiseesturm.util.launchWebsite
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import ch.seesturm.pfadiseesturm.BuildConfig
import ch.seesturm.pfadiseesturm.presentation.common.components.DropdownButton
import ch.seesturm.pfadiseesturm.presentation.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmAppTheme
import ch.seesturm.pfadiseesturm.util.navigation.AppDestination

@Composable
fun MehrView(
    bottomNavigationInnerPadding: PaddingValues,
    mehrNavController: NavController,
    viewModel: PfadijahreViewModel,
    appStateViewModel: AppStateViewModel,
    columnState: LazyListState = rememberLazyListState()
) {

    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName ?: ""
    val versionCode = PackageInfoCompat.getLongVersionCode(packageInfo).toString()

    val footerText = "Pfadi Seesturm ${Calendar.getInstance().get(Calendar.YEAR)}\napp@seesturm.ch\n\nApp-Version $versionName ($versionCode)" + if (BuildConfig.DEBUG) {
        " (Debug)"
    }
    else {
        ""
    }

    TopBarScaffold(
        topBarStyle = TopBarStyle.Large,
        title = "Mehr"
    ) { topBarInnerPadding ->

        val photosState by viewModel.state.collectAsStateWithLifecycle()
        val appState by appStateViewModel.state.collectAsStateWithLifecycle()
        val combinedPadding = bottomNavigationInnerPadding.intersectWith(
            other = topBarInnerPadding,
            layoutDirection = LayoutDirection.Ltr,
            additionalTopPadding = 16.dp,
            additionalBottomPadding = 16.dp,
            additionalEndPadding = 16.dp,
            additionalStartPadding = 16.dp
        )
        val context = LocalContext.current
        val firstSectionCount = 5
        val secondSectionCount = 3
        val thirdSectionCount = 3
        val fourthSectionCount = 2

        LazyColumn(
            state = columnState,
            userScrollEnabled = true,
            contentPadding = combinedPadding,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                BasicListHeader(
                    "Infos und Medien".uppercase(),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }
            item {
                FormItem(
                    items = (0..<firstSectionCount).toList(),
                    index = 0,
                    mainContent = FormItemContentType.Text(
                        title = "Fotos"
                    ),
                    leadingIcon = Icons.Outlined.Photo,
                    onClick = {
                        mehrNavController.navigate(
                            AppDestination.MainTabView.Destinations.Mehr.Destinations.Pfadijahre
                        )
                    },
                    trailingElement = FormItemTertiaryElementType.DisclosureIndicator
                )
            }
            if (!photosState.isError) {
                item {
                    FormItem(
                        items = (0..<firstSectionCount).toList(),
                        index = 1,
                        mainContent = FormItemContentType.Custom(
                            content = {
                                MehrHorizontalPhotoScrollView(
                                    viewModel = viewModel,
                                    mehrNavController = mehrNavController,
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                )
                            }
                        ),
                        trailingElement = FormItemTertiaryElementType.Blank
                    )
                }
            }
            item {
                FormItem(
                    items = (0..<firstSectionCount).toList(),
                    index = 2,
                    mainContent = FormItemContentType.Text(
                        title = "Dokumente"
                    ),
                    leadingIcon = Icons.Outlined.Description,
                    onClick = {
                        mehrNavController.navigate(
                            AppDestination.MainTabView.Destinations.Mehr.Destinations.Dokumente
                        )
                    },
                    trailingElement = FormItemTertiaryElementType.DisclosureIndicator
                )
            }
            item {
                FormItem(
                    items = (0..<firstSectionCount).toList(),
                    index = 3,
                    mainContent = FormItemContentType.Text(
                        title = "Lüüchtturm"
                    ),
                    leadingIcon = Icons.AutoMirrored.Outlined.LibraryBooks,
                    onClick = {
                        mehrNavController.navigate(
                            AppDestination.MainTabView.Destinations.Mehr.Destinations.Luuchtturm
                        )
                    },
                    trailingElement = FormItemTertiaryElementType.DisclosureIndicator
                )
            }
            item {
                FormItem(
                    items = (0..<firstSectionCount).toList(),
                    index = 4,
                    mainContent = FormItemContentType.Text(
                        title = "Leitungsteam"
                    ),
                    leadingIcon = Icons.Outlined.ContactMail,
                    onClick = {
                        mehrNavController.navigate(
                            AppDestination.MainTabView.Destinations.Mehr.Destinations.Leitungsteam
                        )
                    },
                    trailingElement = FormItemTertiaryElementType.DisclosureIndicator
                )
            }
            item {
                BasicListHeader(
                    "Pfadiheim".uppercase(),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                )
            }
            item {
                FormItem(
                    items = (0..<secondSectionCount).toList(),
                    index = 0,
                    mainContent = FormItemContentType.Text(
                        title = "Belegungsplan",
                        textColor = FormItemTextContentColor.Custom(
                            color = Color.SEESTURM_GREEN
                        )
                    ),
                    leadingIcon = Icons.Outlined.CalendarMonth,
                    onClick = {
                        launchWebsite(
                            url = "https://api.belegungskalender-kostenlos.de/kalender.php?kid=24446",
                            context = context
                        )
                    },
                    trailingElement = FormItemTertiaryElementType.Blank
                )
            }
            item {
                FormItem(
                    items = (0..<secondSectionCount).toList(),
                    index = 1,
                    mainContent = FormItemContentType.Text(
                        title = "Weitere Informationen",
                        textColor = FormItemTextContentColor.Custom(
                            color = Color.SEESTURM_GREEN
                        )
                    ),
                    leadingIcon = Icons.Outlined.Info,
                    onClick = {
                        launchWebsite(
                            url = "https://seesturm.ch/pfadiheim/",
                            context = context
                        )
                    },
                    trailingElement = FormItemTertiaryElementType.Blank
                )
            }
            item {
                FormItem(
                    items = (0..<secondSectionCount).toList(),
                    index = 2,
                    mainContent = FormItemContentType.Text(
                        title = "Anfrage und Reservation",
                        textColor = FormItemTextContentColor.Custom(
                            color = Color.SEESTURM_GREEN
                        )
                    ),
                    leadingIcon = Icons.Outlined.Textsms,
                    onClick = {
                        launchWebsite(
                            url = "mailto:pfadiheim@seesturm.ch",
                            context = context
                        )
                    },
                    trailingElement = FormItemTertiaryElementType.Blank
                )
            }
            item {
                BasicListHeader(
                    "Einstellungen".uppercase(),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                )
            }
            item {
                FormItem(
                    items = (0..<thirdSectionCount).toList(),
                    index = 0,
                    mainContent = FormItemContentType.Text(
                        title = "Push-Nachrichten"
                    ),
                    leadingIcon = Icons.Outlined.Notifications,
                    onClick = {
                        mehrNavController.navigate(
                            AppDestination.MainTabView.Destinations.Mehr.Destinations.PushNotifications
                        )
                    },
                    trailingElement = FormItemTertiaryElementType.DisclosureIndicator
                )
            }
            item {
                FormItem(
                    items = (0..<thirdSectionCount).toList(),
                    index = 1,
                    mainContent = FormItemContentType.Text(
                        title = "Gespeicherte Personen"
                    ),
                    leadingIcon = Icons.Outlined.PeopleOutline,
                    onClick = {
                        mehrNavController.navigate(
                            AppDestination.MainTabView.Destinations.Mehr.Destinations.GespeichertePersonen
                        )
                    },
                    trailingElement = FormItemTertiaryElementType.DisclosureIndicator
                )
            }
            item {
                FormItem(
                    items = (0..<thirdSectionCount).toList(),
                    index = 2,
                    mainContent = FormItemContentType.Text(
                        title = "Erscheinungsbild"
                    ),
                    leadingIcon = Icons.Outlined.Contrast,
                    trailingElement = FormItemTertiaryElementType.Custom(
                        content = {
                            DropdownButton(
                                title = appState.theme.description,
                                dropdown = { isShown, dismiss ->
                                    DropdownMenu(
                                        expanded = isShown,
                                        onDismissRequest = {
                                            dismiss()
                                        }
                                    ) {
                                        SeesturmAppTheme.entries.forEach { theme ->
                                            DropdownMenuItem(
                                                text = { Text(theme.description) },
                                                onClick = {
                                                    appStateViewModel.updateTheme(theme)
                                                    dismiss()
                                                },
                                                trailingIcon = {
                                                    if (appState.theme == theme) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    )
                )
            }
            item {
                FormItem(
                    items = (0..<fourthSectionCount).toList(),
                    index = 0,
                    mainContent = FormItemContentType.Text(
                        title = "Feedback zur App geben",
                        textColor = FormItemTextContentColor.Custom(
                            color = Color.SEESTURM_GREEN
                        )
                    ),
                    leadingIcon = Icons.AutoMirrored.Outlined.Comment,
                    onClick = {
                        launchWebsite(
                            url = Constants.FEEDBACK_FORM_URL,
                            context = context
                        )
                    },
                    trailingElement = FormItemTertiaryElementType.Blank,
                    modifier = Modifier
                        .padding(top = 32.dp)
                )
            }
            item {
                FormItem(
                    items = (0..<fourthSectionCount).toList(),
                    index = 1,
                    mainContent = FormItemContentType.Text(
                        title = "Datenschutzerklärung",
                        textColor = FormItemTextContentColor.Custom(
                            color = Color.SEESTURM_GREEN
                        )
                    ),
                    leadingIcon = Icons.Outlined.Security,
                    onClick = {
                        launchWebsite(
                            url = Constants.DATENSCHUTZERKLAERUNG_URL,
                            context = context
                        )
                    },
                    trailingElement = FormItemTertiaryElementType.Blank
                )
            }
            item {
                Text(
                    footerText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 32.dp, bottom = 16.dp)
                        .alpha(0.4f)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun MehrHorizontalPhotoScrollView(
    viewModel: PfadijahreViewModel,
    mehrNavController: NavController,
    modifier: Modifier = Modifier
) {

    val photosState by viewModel.state.collectAsStateWithLifecycle()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        userScrollEnabled = !photosState.scrollingDisabled,
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier
    ) {
        when (val localState = photosState) {
            is UiState.Error -> {
            }
            UiState.Loading -> {
                items(
                    count = 5,
                    key = { index ->
                        "PhotoLoadingCell$index"
                    }
                ) {
                    PhotoGalleryLoadingCell(
                        size = 130.dp,
                        withText = true,
                        modifier = Modifier
                            .animateItem()
                    )
                }
            }
            is UiState.Success -> {
                items(
                    items = localState.data.reversed(),
                    key = { item ->
                        item.id
                    }
                ) { item ->
                    PhotoGalleryCell(
                        size = 130.dp,
                        thumbnailUrl = item.thumbnail,
                        title = item.title,
                        onClick = {
                            mehrNavController.navigate(
                                AppDestination.MainTabView.Destinations.Mehr.Destinations.Albums(
                                    id = item.id,
                                    title = item.title
                                )
                            )
                        },
                        modifier = Modifier
                            .animateItem()
                    )
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun MehrHorizontalPhotoScrollViewPreview() {
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

/*
@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun MehrViewPreview() {
    MehrView(
        PaddingValues(0.dp),
        rememberNavController(),
        PfadijahreViewModel(
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

    )
}

 */