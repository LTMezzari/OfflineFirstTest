package mezzari.torres.lucas.android.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import mezzari.torres.lucas.android.logger.AppLogger
import mezzari.torres.lucas.android.notification.handler.NotificationMessageHandler
import mezzari.torres.lucas.android.notification.hub.NotificationHubHandler
import mezzari.torres.lucas.core.archive.launch
import mezzari.torres.lucas.core.interfaces.AppDispatcher
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
class NotificationService: FirebaseMessagingService() {

    private val messageHandlers: List<NotificationMessageHandler> by lazy {
        getKoin().getAll()
    }
    private val notificationHubHandler: NotificationHubHandler by inject()
    private val logger: AppLogger by inject()
    private val appDispatcher: AppDispatcher by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        appDispatcher.io.launch {
            notificationHubHandler.registerToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        logger.logMessage("Remote Message Received from Firebase {${message.data}}")
        for (handler in messageHandlers) {
            if (!handler.shouldIntercept(message)) {
                continue
            }

            val hasCompleted = handler.interceptMessage(message)
            if (hasCompleted) {
                break
            }
        }
    }
}