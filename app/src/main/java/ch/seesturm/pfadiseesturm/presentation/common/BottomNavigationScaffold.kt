package ch.seesturm.pfadiseesturm.presentation.common

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarHost
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarLocation
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun BottomNavigationScaffold(
    tabNavController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {

    val hazeState = remember { HazeState() }

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .then(
                        if (Build.VERSION.SDK_INT >= 30) {
                            Modifier
                                .hazeEffect(hazeState, style = CupertinoMaterials.thin())
                        }
                        else {
                            Modifier
                        }
                    )
            ) {
                MainBottomNavigationBar(
                    tabNavController = tabNavController
                )
            }
        },
        snackbarHost = {
            SeesturmSnackbarHost(
                location = SeesturmSnackbarLocation.Default
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .then(
                    if (Build.VERSION.SDK_INT >= 30) {
                        Modifier
                            .hazeSource(hazeState)
                    }
                    else {
                        Modifier
                    }
                )
        ) {
            content(innerPadding)
        }
    }
}