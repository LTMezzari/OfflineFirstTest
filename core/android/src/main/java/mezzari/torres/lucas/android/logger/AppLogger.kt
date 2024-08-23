package mezzari.torres.lucas.android.logger

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
interface AppLogger {
    fun logMessage(message: String?)

    fun logError(e: Exception?)

    fun printError(e: Exception?)

    fun recordError(e: Exception?)
}