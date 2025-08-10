package ch.seesturm.pfadiseesturm.domain.storage.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmAppError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class JPGByteArray private constructor(
    val wrappedByteArray: ByteArray
) {
    companion object {
        suspend fun fromUri(uri: Uri, context: Context, compressionQuality: Int = 50): JPGByteArray = withContext(Dispatchers.IO) {

            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
            else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }

            val outputStream = ByteArrayOutputStream()
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, outputStream)) {
                throw PfadiSeesturmAppError.JpgConversionFailed("Bild konnte nicht in JPG konvertiert werden.")
            }
            val jpegData = outputStream.toByteArray()

            JPGByteArray(jpegData)
        }
    }
}