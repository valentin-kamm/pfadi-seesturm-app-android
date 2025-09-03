package ch.seesturm.pfadiseesturm.domain.storage.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmAppError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class JPGData private constructor(
    val originalBitmap: ImageBitmap,
    val compressedByteArray: ByteArray
) {
    companion object {
        suspend fun fromUri(uri: Uri, context: Context, compressionQuality: Int = 50): JPGData = withContext(Dispatchers.IO) {

            // load bitmap from file system
            val originalBitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: throw PfadiSeesturmAppError.JpgConversionFailed("Bild konnte nicht in JPG konvertiert werden.")

            // compress bitmap to JPG
            val compressedOutputStream = ByteArrayOutputStream()
            val compressionSucceeded = originalBitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, compressedOutputStream)
            if (!compressionSucceeded) {
                throw PfadiSeesturmAppError.JpgConversionFailed("Bild konnte nicht in JPG konvertiert werden.")
            }

            JPGData(
                originalBitmap = originalBitmap.asImageBitmap(),
                compressedByteArray = compressedOutputStream.toByteArray()
            )
        }

        suspend fun fromBitmap(bitmap: Bitmap, compressionQuality: Int = 50): JPGData = withContext(Dispatchers.IO) {

            val compressedOutputStream = ByteArrayOutputStream()
            val compressionSucceeded = bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, compressedOutputStream)
            if (!compressionSucceeded) {
                throw PfadiSeesturmAppError.JpgConversionFailed("Bild konnte nicht in JPG konvertiert werden.")
            }

            JPGData(
                originalBitmap = bitmap.asImageBitmap(),
                compressedByteArray = compressedOutputStream.toByteArray()
            )
        }

        @Composable
        fun fromResource(
            @DrawableRes resId: Int,
            compressionQuality: Int = 50
        ): JPGData {

            val originalImageBitmap = ImageBitmap.imageResource(resId)
            val originalBitmap = originalImageBitmap.asAndroidBitmap()

            val compressedOutputStream = ByteArrayOutputStream()
            val compressionSucceeded = originalBitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, compressedOutputStream)
            if (!compressionSucceeded) {
                throw PfadiSeesturmAppError.JpgConversionFailed("Bild konnte nicht in JPG konvertiert werden.")
            }

            return JPGData(
                originalBitmap = originalImageBitmap,
                compressedByteArray = compressedOutputStream.toByteArray()
            )
        }
    }
}