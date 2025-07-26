package ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPhotoGallery
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.state.UiState

@Composable
fun MehrHorizontalPhotoScrollView(
    photosState: UiState<List<WordpressPhotoGallery>>,
    mehrNavController: NavController,
    modifier: Modifier = Modifier
) {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        userScrollEnabled = !photosState.scrollingDisabled,
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier
    ) {
        if (photosState is UiState.Loading) {
            items(
                count = 5,
                key = { index ->
                    "PhotoLoadingCell$index"
                }
            ) {
                PhotoGalleryLoadingCell(
                    size = 110.dp,
                    withText = true,
                    modifier = Modifier
                        .animateItem()
                )
            }
        }
        else if (photosState is UiState.Success) {
            items(
                items = photosState.data.reversed(),
                key = { item ->
                    item.id
                }
            ) { item ->
                PhotoGalleryCell(
                    size = 110.dp,
                    thumbnailUrl = item.thumbnailUrl,
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

@Preview("Loading", showBackground = true)
@Composable
private fun MehrHorizontalPhotoScrollViewPreview1() {
    PfadiSeesturmTheme {
        MehrHorizontalPhotoScrollView(
            photosState = UiState.Loading,
            mehrNavController = rememberNavController(),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
@Preview("Success", showBackground = true)
@Composable
private fun MehrHorizontalPhotoScrollViewPreview2() {
    PfadiSeesturmTheme {
        MehrHorizontalPhotoScrollView(
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
            mehrNavController = rememberNavController(),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}