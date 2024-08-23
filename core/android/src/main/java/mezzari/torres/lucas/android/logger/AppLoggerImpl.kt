package mezzari.torres.lucas.android.logger

import mezzari.torres.lucas.android.BuildConfig
import mezzari.torres.lucas.android.logger.crashlytics.CrashlyticsHandler
import timber.log.Timber

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
class AppLoggerImpl(private val crashlyticsHandler: CrashlyticsHandler): AppLogger {

    init {
        if (BuildConfig.DEBUG)
            Timber.plant(LoggerTree())
    }

    override fun logMessage(message: String?) {
        val m = message ?: return
        Timber.d(m)
    }

    override fun logError(e: Exception?) {
        val error = e ?: return
        Timber.e(error)
    }

    override fun printError(e: Exception?) {
        val error = e ?: return
        if (BuildConfig.DEBUG) {
            error.printStackTrace()
        }
    }

    override fun recordError(e: Exception?) {
        crashlyticsHandler.recordException(e)
    }

    class LoggerTree: Timber.DebugTree() {
        private val ignoredClasses: List<String> = listOf(
            LoggerTree::class.java.name,
            AppLoggerImpl::class.java.name,
            Timber::class.java.name,
            Timber.Forest::class.java.name,
            Timber.Tree::class.java.name,
            Timber.DebugTree::class.java.name
        )

        private val tag: String?
            get() = Throwable().stackTrace
                .first { it.className !in ignoredClasses }
                .let(::createStackElementTag)

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            super.log(priority, this.tag, message, t)
        }
    }
}