package ch.seesturm.pfadiseesturm.domain.storage.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                val bitmap = ImageDecoder.decodeBitmap(source)

                PickedGalleryItem(
                    bitmap = bitmap.asImageBitmap()
                )
            }
            else {
                val resolver = context.contentResolver

                val bitmap = resolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                } ?: throw PfadiSeesturmError.JPGConversion("Bild konnte nicht aus der Gallerie geladen werden.")

                val rotatedBitmap = resolver.openInputStream(uri)?.use { inputStream ->
                    val exif = ExifInterface(inputStream)
                    val rotation = when (
                        exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL
                        )
                    ) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270
                        else -> 0
                    }
                    if (rotation != 0) {
                        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
                        Bitmap.createBitmap(
                            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                        )
                    }
                    else {
                        bitmap
                    }
                } ?: bitmap

                PickedGalleryItem(
                    bitmap = rotatedBitmap.asImageBitmap()
                )
            }
        }
    }
}
