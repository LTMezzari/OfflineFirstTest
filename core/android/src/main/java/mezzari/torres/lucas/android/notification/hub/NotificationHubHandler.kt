package mezzari.torres.lucas.android.notification.hub

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
interface NotificationHubHandler {
    fun registerWithCurrentToken()

    suspend fun registerToken(token: String): Boolean

    fun unregisterToken(): Boolean
}