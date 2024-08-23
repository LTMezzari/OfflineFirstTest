package mezzari.torres.lucas.android.signaler

import android.os.Bundle

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
interface EventSignaler {
    fun dispatchEvent(event: String, bundle: Bundle? = null)

    interface EventListener {
        fun shouldHandleEvent(event: String): Boolean

        fun handleEvent(bundle: Bundle?): Boolean
    }

    companion object {
        const val SIGNAL_NOT_HANDLED = "EventSignaler::EVENT_NOT_HANDLED"
        const val SIGNAL_UNAUTHENTICATED = "EventSignaler::SIGNAL_UNAUTHENTICATED"
        const val SIGNAL_AUTHENTICATED = "EventSignaler::SIGNAL_AUTHENTICATED"

        const val KEY_UNAUTHENTICATED = "EventSignaler::KEY_UNAUTHENTICATED"
        const val KEY_ERROR = "EventSignaler::KEY_ERROR"
    }
}