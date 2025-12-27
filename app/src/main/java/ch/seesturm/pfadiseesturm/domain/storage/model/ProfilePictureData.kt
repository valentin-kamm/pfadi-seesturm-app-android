package ch.seesturm.pfadiseesturm.domain.storage.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

data class ProfilePictureData(
    val originalBitmap: ImageBitmap,
    val compressedByteArray: ByteArray
) {
    companion object {

        private const val DEFAULT_COMPRESSION_QUALITY: Int = 50

        suspend fun fromUri(uri: Uri, context: Context): ProfilePictureData = withContext(Dispatchers.IO) {

            val originalBitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: throw PfadiSeesturmError.JPGConversion("Bild konnte nicht in JPG konvertiert werden.")

            val compressedOutputStream = ByteArrayOutputStream()
            val compressionSucceeded = originalBitmap.compress(Bitmap.CompressFormat.JPEG, DEFAULT_COMPRESSION_QUALITY, compressedOutputStream)
            if (!compressionSucceeded) {
                throw PfadiSeesturmError.JPGConversion("Bild konnte nicht in JPG konvertiert werden.")
            }

            ProfilePictureData(
                originalBitmap = originalBitmap.asImageBitmap(),
                compressedByteArray = compressedOutputStream.toByteArray()
            )
        }

        suspend fun fromBitmap(bitmap: Bitmap): ProfilePictureData = withContext(Dispatchers.IO) {

            val compressedOutputStream = ByteArrayOutputStream()
            val compressionSucceeded = bitmap.compress(Bitmap.CompressFormat.JPEG, DEFAULT_COMPRESSION_QUALITY, compressedOutputStream)
            if (!compressionSucceeded) {
                throw PfadiSeesturmError.JPGConversion("Bild konnte nicht in JPG konvertiert werden.")
            }

            ProfilePictureData(
                originalBitmap = bitmap.asImageBitmap(),
                compressedByteArray = compressedOutputStream.toByteArray()
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProfilePictureData

        if (originalBitmap != other.originalBitmap) return false
        if (!compressedByteArray.contentEquals(other.compressedByteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = originalBitmap.hashCode()
        result = 31 * result + compressedByteArray.contentHashCode()
        return result
    }
}
