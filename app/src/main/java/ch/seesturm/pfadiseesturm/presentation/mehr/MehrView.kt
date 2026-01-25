package ch.seesturm.pfadiseesturm.presentation.mehr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeaderMode
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumn
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumnItemLeadingContentType
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumnItemTrailingContentType
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.GalleriesViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components.MehrHorizontalPhotoScrollView
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.launchWebsite
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.SeesturmAppTheme
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
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

        GroupedColumn(
            state = columnState,
            contentPadding = combinedPadding,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            section(
                header = {
                    BasicListHeader(
                        mode = BasicListHeaderMode.Normal("Infos und Medien"),
                    )
                }
            ) {
                textItem(
                    text = "Fotos",
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.Outlined.Photo
                    ),
                    trailingContent = GroupedColumnItemTrailingContentType.DisclosureIndicator,
                    onClick = {
                        mehrNavController.navigate(
                            AppDestination.MainTabView.Destinations.Mehr.Destinations.Pfadijahre
                        )
                    }
                )
                if (!photosState.isError) {
                    item {
                        MehrHorizontalPhotoScrollView(
                            photosState = photosState,
                            mehrNavController = mehrNavController,
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                        )
                    }
                }
                textItem(
                    text = "Dokumente",
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.Outlined.Description
                    ),
                    trailingContent = GroupedColumnItemTrailingContentType.DisclosureIndicator,
                    onClick = {
                        mehrNavController.navigate(
                            AppDestination.MainTabView.Destinations.Mehr.Destinations.Dokumente
                        )
                    }
                )
                textItem(
                    text = "Lüüchtturm",
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.AutoMirrored.Outlined.LibraryBooks
                    ),
                    trailingContent = GroupedColumnItemTrailingContentType.DisclosureIndicator,
                    onClick = {
                        mehrNavController.navigate(
                            AppDestination.MainTabView.Destinations.Mehr.Destinations.Luuchtturm
                        )
                    }
                )
                textItem(
                    text = "Leitungsteam",
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.Outlined.ContactMail
                    ),
                    trailingContent = GroupedColumnItemTrailingContentType.DisclosureIndicator,
                    onClick = {
                        mehrNavController.navigate(
                            AppDestination.MainTabView.Destinations.Mehr.Destinations.Leitungsteam
                        )
                    }
                )
            }

            section(
                header = {
                    BasicListHeader(
                        mode = BasicListHeaderMode.Normal("Pfadiheim")
                    )
                }
            ) {
                textItem(
                    text = "Belegungsplan",
                    textColor = { Color.SEESTURM_GREEN },
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.Outlined.CalendarMonth
                    ),
                    onClick = {
                        onLaunchWebsite("https://api.belegungskalender-kostenlos.de/kalender.php?kid=24446")
                    }
                )
                textItem(
                    text = "Weitere Informationen",
                    textColor = { Color.SEESTURM_GREEN },
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.Outlined.Info
                    ),
                    onClick = {
                        onLaunchWebsite("https://seesturm.ch/pfadiheim/")
                    }
                )
                textItem(
                    text = "Anfrage und Reservation",
                    textColor = { Color.SEESTURM_GREEN },
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.Outlined.Textsms
                    ),
                    onClick = {
                        onLaunchWebsite("mailto:pfadiheim@seesturm.ch")
                    }
                )
            }

            section(
                header = {
                    BasicListHeader(
                        mode = BasicListHeaderMode.Normal("Einstellungen")
                    )
                }
            ) {
                textItem(
                    text = "Push-Nachrichten",
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.Outlined.Notifications
                    ),
                    trailingContent = GroupedColumnItemTrailingContentType.DisclosureIndicator,
                    onClick = {
                        mehrNavController.navigate(
                            AppDestination.MainTabView.Destinations.Mehr.Destinations.PushNotifications
                        )
                    }
                )
                textItem(
                    text = "Gespeicherte Personen",
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.Outlined.PeopleOutline
                    ),
                    trailingContent = GroupedColumnItemTrailingContentType.DisclosureIndicator,
                    onClick = {
                        mehrNavController.navigate(
                            AppDestination.MainTabView.Destinations.Mehr.Destinations.GespeichertePersonen
                        )
                    }
                )
                textItem(
                    text = "Erscheinungsbild",
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.Outlined.Contrast
                    ),
                    trailingContent = GroupedColumnItemTrailingContentType.Custom {
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
            }

            section {
                textItem(
                    text = "Feedback zur App geben",
                    textColor = { Color.SEESTURM_GREEN },
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Comment
                    ),
                    onClick = {
                        onLaunchWebsite(Constants.FEEDBACK_FORM_URL)
                    }
                )
                textItem(
                    text = "Datenschutzerklärung",
                    textColor = { Color.SEESTURM_GREEN },
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.Outlined.Security
                    ),
                    onClick = {
                        onLaunchWebsite(Constants.DATENSCHUTZERKLAERUNG_URL)
                    }
                )
            }

            section {
                customItem {
                    Text(
                        footerText,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(16.dp)
                            .alpha(0.4f)
                            .fillMaxWidth()
                    )
                }
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
@Preview("Error")
@Composable
private fun MehrViewPreview2() {
    PfadiSeesturmTheme {
        MehrContentView(
            photosState = UiState.Error(""),
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
private fun MehrViewPreview3() {
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