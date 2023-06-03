package mezzari.torres.lucas.android.archive

import android.util.Log
import mezzari.torres.lucas.android.BuildConfig
import java.lang.Exception
import java.util.UUID

/**
 * @author Lucas T. Mezzari
 * @since 03/06/2023
 */
fun logError(exception: Exception?) {
    if (!BuildConfig.DEBUG || exception == null) return
    exception.printStackTrace()
}

fun logError(error: String?, tag: String = UUID.randomUUID().toString().substring(0, 23)) {
    if (!BuildConfig.DEBUG || error == null) return
    Log.e(tag, error)
}