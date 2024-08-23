package mezzari.torres.lucas.commons.archive

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 0 /* Ignored for PNGs */, stream)
    return stream.toByteArray()
}