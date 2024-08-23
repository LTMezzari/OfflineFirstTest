package mezzari.torres.lucas.android.logger.crashlytics

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
class CrashlyticsHandlerImpl: CrashlyticsHandler {

//    private val crashlytics by lazy {
//        FirebaseCrashlytics.getInstance()
//    }

    override fun recordException(e: Exception?) {
        val exception = e ?: return
//        crashlytics.recordException(exception)
    }

    override fun setData(key: String, value: String) {
//        crashlytics.setCustomKey(key, value)
    }

    override fun setUserId(user: String) {
//        crashlytics.setUserId(user)
    }
}