package ch.seesturm.pfadiseesturm.presentation.mehr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import ch.seesturm.pfadiseesturm.BuildConfig
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPhotoGallery
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenu
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenuItem
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.buttons.DropdownButton
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListHeaderMode
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemTextContentColor
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemTrailingElementType
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.GalleriesViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components.MehrHorizontalPhotoScrollView
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.types.SeesturmAppTheme
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.launchWebsite
import ch.seesturm.pfadiseesturm.util.state.UiState
import java.util.Calendar

@Composable
fun MehrView(
    bottomNavigationInnerPadding: PaddingValues,
    mehrNavController: NavController,
    viewModel: GalleriesViewModel,
    appStateViewModel: AppStateViewModel
) {

    val photosState by viewModel.state.collectAsStateWithLifecycle()
    val appState by appStateViewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName ?: ""
    val versionCode = PackageInfoCompat.getLongVersionCode(packageInfo).toString()

    MehrContentView(
        photosState = photosState,
        selectedTheme = appState.theme,
        versionName = versionName,
        versionCode = versionCode,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        mehrNavController = mehrNavController,
        onChangeTheme = { theme ->
            appStateViewModel.updateTheme(theme)
        },
        onLaunchWebsite = { url ->
            launchWebsite(
                url = url,
                context = context
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MehrContentView(
    photosState: UiState<List<WordpressPhotoGallery>>,
    selectedTheme: SeesturmAppTheme,
    versionName: String,
    versionCode: String,
    bottomNavigationInnerPadding: PaddingValues,
    mehrNavController: NavController,
    onChangeTheme: (SeesturmAppTheme) -> Unit,
    onLaunchWebsite: (String) -> Unit,
    columnState: LazyListState = rememberLazyListState()
) {

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

        val combinedPadding = bottomNavigationInnerPadding.intersectWith(
            other = topBarInnerPadding,
            layoutDirection = LayoutDirection.Ltr,
            additionalTopPadding = 16.dp,
            additionalBottomPadding = 16.dp,
            additionalEndPadding = 16.dp,
            additionalStartPadding = 16.dp
        )
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
                    mode = BasicListHeaderMode.Normal("Infos und Medien"),
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
                    trailingElement = FormItemTrailingElementType.DisclosureIndicator
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
                                    photosState = photosState,
                                    mehrNavController = mehrNavController,
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                )
                            }
                        ),
                        trailingElement = FormItemTrailingElementType.Blank,
                        modifier = Modifier
                            .animateItem()
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
                    trailingElement = FormItemTrailingElementType.DisclosureIndicator
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
                    trailingElement = FormItemTrailingElementType.DisclosureIndicator
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
                    trailingElement = FormItemTrailingElementType.DisclosureIndicator
                )
            }
            item {
                BasicListHeader(
                    mode = BasicListHeaderMode.Normal("Pfadiheim"),
                    modifier = Modifier
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
                        onLaunchWebsite("https://api.belegungskalender-kostenlos.de/kalender.php?kid=24446")
                    },
                    trailingElement = FormItemTrailingElementType.Blank
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
                        onLaunchWebsite("https://seesturm.ch/pfadiheim/")
                    },
                    trailingElement = FormItemTrailingElementType.Blank
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
                        onLaunchWebsite("mailto:pfadiheim@seesturm.ch")
                    },
                    trailingElement = FormItemTrailingElementType.Blank
                )
            }
            item {
                BasicListHeader(
                    mode = BasicListHeaderMode.Normal("Einstellungen"),
                    modifier = Modifier
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
                    trailingElement = FormItemTrailingElementType.DisclosureIndicator
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
                    trailingElement = FormItemTrailingElementType.DisclosureIndicator
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
                    trailingElement = FormItemTrailingElementType.Custom(
                        content = {
                            DropdownButton(
                                title = selectedTheme.description,
                                dropdown = { isShown, dismiss ->
                                    ThemedDropdownMenu(
                                        expanded = isShown,
                                        onDismissRequest = {
                                            dismiss()
                                        }
                                    ) {
                                        SeesturmAppTheme.entries.forEach { theme ->
                                            ThemedDropdownMenuItem(
                                                text = { Text(theme.description) },
                                                onClick = {
                                                    onChangeTheme(theme)
                                                    dismiss()
                                                },
                                                trailingIcon = {
                                                    if (theme == selectedTheme) {
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
                        onLaunchWebsite(Constants.FEEDBACK_FORM_URL)
                    },
                    trailingElement = FormItemTrailingElementType.Blank,
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
                        onLaunchWebsite(Constants.DATENSCHUTZERKLAERUNG_URL)
                    },
                    trailingElement = FormItemTrailingElementType.Blank
                )
            }
            item {
                Text(
                    footerText,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
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

@Preview("Loading")
@Composable
private fun MehrViewPreview1() {
    PfadiSeesturmTheme {
        MehrContentView(
            photosState = UiState.Loading,
            selectedTheme = SeesturmAppTheme.Dark,
            versionName = "2.0.0",
            versionCode = "20",
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            mehrNavController = rememberNavController(),
            onChangeTheme = {},
            onLaunchWebsite = {}
        )
    }
}
@Preview("Success")
@Composable
private fun MehrViewPreview2() {
    PfadiSeesturmTheme {
        MehrContentView(
            photosState = UiState.Success(
                listOf(
                    WordpressPhotoGallery(
                        title = "Test",
                        id = "123",
                        thumbnailUrl = "https://seesturm.ch/wp-content/uploads/2022/04/190404_Infobroschuere-Pfadi-Thurgau-pdf-212x300.jpg"
                    ),
                    WordpressPhotoGallery(
                        title = "Test",
                        id = "456",
                        thumbnailUrl = "https://seesturm.ch/wp-content/uploads/2022/04/190404_Infobroschuere-Pfadi-Thurgau-pdf-212x300.jpg"
                    ),
                    WordpressPhotoGallery(
                        title = "Test",
                        id = "789",
                        thumbnailUrl = "https://seesturm.ch/wp-content/uploads/2022/04/190404_Infobroschuere-Pfadi-Thurgau-pdf-212x300.jpg"
                    )
                )
            ),
            selectedTheme = SeesturmAppTheme.Dark,
            versionName = "2.0.0",
            versionCode = "20",
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            mehrNavController = rememberNavController(),
            onChangeTheme = {},
            onLaunchWebsite = {}
        )
    }
}