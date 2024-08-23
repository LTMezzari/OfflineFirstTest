package mezzari.torres.lucas.android.logger.crashlytics

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
interface CrashlyticsHandler {
    fun recordException(e: Exception?)

    fun setData(key: String, value: String)

    fun setUserId(user: String)
}