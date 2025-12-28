package ch.seesturm.pfadiseesturm.domain.storage.model

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class PickedGalleryItem private constructor(
    val bitmap: ImageBitmap
) {

    companion object {

        suspend fun fromUri(uri: Uri, context: Context): PickedGalleryItem = withContext(Dispatchers.IO) {

            val bitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: throw PfadiSeesturmError.JPGConversion("Bild konnte nicht aus der Gallerie geladen werden.")

            PickedGalleryItem(
                bitmap = bitmap.asImageBitmap()
            )
        }
    }
}
