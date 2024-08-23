package mezzari.torres.lucas.android.boot

import android.content.Context
import android.content.Intent

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
interface BootInterceptor {
    fun onDeviceBoot(context: Context, intent: Intent)
}