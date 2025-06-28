package ch.seesturm.pfadiseesturm.util.types

import android.net.Uri
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination

sealed class SeesturmAppLink {
    data object Aktuell: SeesturmAppLink()
    data class AktuellPost(
        val postId: Int
    ): SeesturmAppLink()
    data object Fotos: SeesturmAppLink()
    data object Dokumente: SeesturmAppLink()
    data object Luuchtturm: SeesturmAppLink()

    val targetTab: AppDestination.MainTabView.Destinations
        get() {
            return when (this) {
                Aktuell, is AktuellPost -> AppDestination.MainTabView.Destinations.Aktuell
                Dokumente, Fotos, Luuchtturm -> AppDestination.MainTabView.Destinations.Mehr
            }
        }

    companion object {

        fun fromUrl(url: Uri?): SeesturmAppLink? {

            if (url == null || url.host != "seesturm.ch") {
                return null
            }

            val pathSegments = url.pathSegments
            val pathSegmentsCount = pathSegments.size

            return when {
                pathSegmentsCount == 1 && pathSegments.last() == "aktuell" -> Aktuell
                pathSegmentsCount == 2 && pathSegments[0] == "aktuell" -> {
                    pathSegments.lastOrNull()?.toIntOrNull()?.let {
                        AktuellPost(it)
                    }
                }
                pathSegmentsCount == 2 && pathSegments[0] == "medien" && pathSegments.last() == "fotos" -> Fotos
                pathSegmentsCount == 2 && pathSegments[0] == "medien" && pathSegments.last() == "downloads" -> Dokumente
                pathSegmentsCount == 2 && pathSegments[0] == "medien" && pathSegments.last() == "luuchtturm" -> Luuchtturm
                else -> null
            }
        }
    }
}