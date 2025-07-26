package ch.seesturm.pfadiseesturm.main

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.authModule
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.dataStoreModule
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.fcmModule
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.wordpressModule
import ch.seesturm.pfadiseesturm.presentation.common.UpdateRequiredView
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.presentation.common.navigation.SeesturmNavigationController
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.ObserveAsEvents
import ch.seesturm.pfadiseesturm.util.SeesturmAppIntent
import ch.seesturm.pfadiseesturm.util.types.SeesturmAppLink
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.viewModelFactoryHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val appStateViewModel: AppStateViewModel by viewModels {
        viewModelFactoryHelper {
            AppStateViewModel(
                authService = authModule.authService,
                themeService = dataStoreModule.selectedThemeService,
                onboardingService = dataStoreModule.onboardingService,
                wordpressApi = wordpressModule.wordpressApi,
                currentAppBuild = getCurrentAppBuild(),
                fcmService = fcmModule.fcmService
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        enableEdgeToEdge()

        handleIntentIfNecessary(intent)

        setContent {

            val coroutineScope = rememberCoroutineScope()
            val overallNavController = rememberNavController()
            val appState by appStateViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(appState.allowedOrientation) {
                requestedOrientation = when (appState.allowedOrientation) {
                    AllowedOrientation.All -> {
                        ActivityInfo.SCREEN_ORIENTATION_SENSOR
                    }
                    AllowedOrientation.PortraitOnly -> {
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                }
            }

            val isDarkTheme = appState.theme.isDarkTheme

            // set color of system bar icons (clock, service, ...)
            SideEffect {
                val window = (this@MainActivity).window
                WindowCompat.setDecorFitsSystemWindows(window, false)
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.isAppearanceLightStatusBars = !isDarkTheme
                insetsController.isAppearanceLightNavigationBars = !isDarkTheme
            }

            ObserveAsEvents(
                flow = OnboardingController.events
            ) {
                coroutineScope.launch {
                    overallNavController.navigate(AppDestination.Onboarding)
                }
            }

            PfadiSeesturmTheme(
                darkTheme = isDarkTheme
            ) {
                if (appState.showAppVersionCheckOverlay) {
                    UpdateRequiredView(
                        onGoToPlayStore = {
                            val intent = Intent(Intent.ACTION_VIEW, "market://details?id=ch.seesturm.pfadiseesturm".toUri())
                            startActivity(intent)
                        }
                    )
                }
                else {
                    SeesturmAppMain(
                        appStateViewModel = appStateViewModel,
                        overallNavController = overallNavController
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntentIfNecessary(intent)
    }

    private fun handleIntentIfNecessary(intent: Intent) {

        when (val seesturmAppIntent = SeesturmAppIntent.fromIntent(intent)) {
            is SeesturmAppIntent.AppLinksIntent -> {
                handleAppLinksIntent(seesturmAppIntent)
            }
            is SeesturmAppIntent.PushNotificationIntent -> {
                handlePushNotificationIntent(seesturmAppIntent)
            }
            else -> return
        }
    }

    private fun handleAppLinksIntent(intent: SeesturmAppIntent.AppLinksIntent) {

        lifecycleScope.launch {
            SeesturmNavigationController.changeTab(intent.link.targetTab)
            when (intent.link) {
                SeesturmAppLink.Aktuell -> {
                    SeesturmNavigationController.navigateInAktuell(
                        AppDestination.MainTabView.Destinations.Aktuell.Destinations.AktuellRoot
                    )
                }
                is SeesturmAppLink.AktuellPost -> {
                    SeesturmNavigationController.navigateInAktuell(
                        AppDestination.MainTabView.Destinations.Aktuell.Destinations.AktuellDetail(
                            postId = intent.link.postId
                        )
                    )
                }
                SeesturmAppLink.Dokumente -> {
                    SeesturmNavigationController.navigateInMehr(
                        AppDestination.MainTabView.Destinations.Mehr.Destinations.Dokumente
                    )
                }
                SeesturmAppLink.Fotos -> {
                    SeesturmNavigationController.navigateInMehr(
                        AppDestination.MainTabView.Destinations.Mehr.Destinations.Pfadijahre
                    )
                }
                SeesturmAppLink.Luuchtturm -> {
                    SeesturmNavigationController.navigateInMehr(
                        AppDestination.MainTabView.Destinations.Mehr.Destinations.Luuchtturm
                    )
                }
            }
        }
    }

    private fun handlePushNotificationIntent(intent: SeesturmAppIntent.PushNotificationIntent) {

        // dismiss push notification
        NotificationManagerCompat.from(this).cancel(intent.notificationId)

        // navigate to appropriate screen
        lifecycleScope.launch {
            SeesturmNavigationController.changeTab(intent.topic.targetTab)
            delay(200)
            when (intent.topic) {
                SeesturmFCMNotificationTopic.Schoepflialarm, SeesturmFCMNotificationTopic.SchoepflialarmReaction -> {
                    SeesturmNavigationController.navigateInAccount(AppDestination.MainTabView.Destinations.Account.Destinations.AccountRoot)
                }
                SeesturmFCMNotificationTopic.Aktuell -> {
                    val postId = intent.customKey?.toIntOrNull()
                    if (postId != null) {
                        SeesturmNavigationController.navigateInAktuell(
                            AppDestination.MainTabView.Destinations.Aktuell.Destinations.AktuellDetail(
                                postId = postId
                            )
                        )
                    }
                }
                SeesturmFCMNotificationTopic.BiberAktivitaeten,
                SeesturmFCMNotificationTopic.WolfAktivitaeten,
                SeesturmFCMNotificationTopic.PfadiAktivitaeten,
                SeesturmFCMNotificationTopic.PioAktivitaeten -> {
                    val eventId = intent.customKey
                    val stufe = SeesturmStufe.fromTopic(intent.topic)
                    if (eventId != null && stufe != null) {
                        SeesturmNavigationController.navigateInHome(
                            AppDestination.MainTabView.Destinations.Home.Destinations.AktivitaetDetail(
                                stufe = stufe,
                                eventId = eventId
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getCurrentAppBuild(): Int? {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            }
            else {
                packageManager.getPackageInfo(packageName, 0)
            }
            if (Build.VERSION.SDK_INT >= 28) {
                packageInfo.longVersionCode.toInt()
            }
            else {
                packageInfo.versionCode
            }
        }
        catch (e: Exception) {
            null
        }
    }
}