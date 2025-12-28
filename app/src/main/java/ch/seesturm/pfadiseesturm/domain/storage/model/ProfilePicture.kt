package ch.seesturm.pfadiseesturm.domain.storage.model

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import ch.seesturm.pfadiseesturm.presentation.common.shrink
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

data class ProfilePicture private constructor(
    val compressedByteArray: ByteArray
) {

    companion object {
        suspend fun fromBitmap(bitmap: Bitmap): ProfilePicture = withContext(Dispatchers.IO) {

            val shrunkenBitmap = bitmap.asImageBitmap().shrink(Constants.PROFILE_PICTURE_SIZE)

            val compressedJpgOutputStream = ByteArrayOutputStream()
            val compressedJpgConversionSucceeded = shrunkenBitmap.asAndroidBitmap().compress(
                Bitmap.CompressFormat.JPEG,
                Constants.PROFILE_PICTURE_COMPRESSION_QUALITY,
                compressedJpgOutputStream
            )
            if (!compressedJpgConversionSucceeded) {
                throw PfadiSeesturmError.JPGConversion("Bild konnte nicht in JPG konvertiert werden.")
            }

            ProfilePicture(
                compressedByteArray = compressedJpgOutputStream.toByteArray()
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProfilePicture

        if (!compressedByteArray.contentEquals(other.compressedByteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        return compressedByteArray.contentHashCode()
    }
}