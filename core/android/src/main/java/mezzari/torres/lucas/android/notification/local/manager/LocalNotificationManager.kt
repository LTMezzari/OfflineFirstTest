package mezzari.torres.lucas.android.notification.local.manager

import android.content.Context

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
interface LocalNotificationManager {
    suspend fun isTimeToNotify(time: Long): Boolean

    suspend fun getNotificationsTimeInMillis(): List<Long>

    suspend fun showNotificationAtTime(timeInMillis: Long, context: Context)
}