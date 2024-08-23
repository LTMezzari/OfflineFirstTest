package mezzari.torres.lucas.android.notification.dispatcher

import android.app.Notification

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
interface NotificationDispatcher {
    fun createChannel(
        channelId: String,
        channelName: String,
        description: String? = null
    ): Boolean

    fun sendNotification(notification: Notification, id: Int, tag: String? = null): Boolean

    fun cancelNotification(id: Int): Boolean

    companion object {
        const val DEFAULT_CHANNEL = "default"
    }
}