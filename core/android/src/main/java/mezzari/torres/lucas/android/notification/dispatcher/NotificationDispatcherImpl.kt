package mezzari.torres.lucas.android.notification.dispatcher

import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import mezzari.torres.lucas.android.archive.createNotificationChannel
import mezzari.torres.lucas.core.archive.elvis

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
class NotificationDispatcherImpl(private val application: Application) : NotificationDispatcher {

    private val notificationManager: NotificationManager? by lazy {
        application.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    override fun createChannel(
        channelId: String,
        channelName: String,
        description: String?
    ): Boolean {
        createNotificationChannel(application, channelId, channelName, description)
        return true
    }

    override fun sendNotification(notification: Notification, id: Int, tag: String?): Boolean {
        val manager = notificationManager elvis {
            return false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !manager.areNotificationsEnabled()) {
            return false
        }

        tag?.run {
            manager.notify(this, id, notification)
            return true
        }
        manager.notify(id, notification)
        return true
    }

    override fun cancelNotification(id: Int): Boolean {
        val manager = notificationManager elvis {
            return false
        }
        manager.cancel(id)
        return true
    }
}