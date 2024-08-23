package mezzari.torres.lucas.android.notification.handler

import com.google.firebase.messaging.RemoteMessage

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
interface NotificationMessageHandler {
    /**
     * Checks if a message received from FirebaseMessagingService
     * should be intercepted by this handler
     *
     * @param remoteMessage The remote message received from the service
     * @return Should return true if the remote message should be handled by the this handler
     */
    fun shouldIntercept(remoteMessage: RemoteMessage): Boolean

    /**
     * Intercepts a message received from FirebaseMessagingService
     *
     * @param remoteMessage The remote message received from the service
     * @return Should return true if the remote message was handled to completion
     */
    fun interceptMessage(remoteMessage: RemoteMessage): Boolean
}