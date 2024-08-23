package mezzari.torres.lucas.android.signaler

import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
class EventSignalerImpl(private val context: Context, private val listeners: List<EventSignaler.EventListener>): EventSignaler {
    override fun dispatchEvent(event: String, bundle: Bundle?) {
        var wasHandled: Boolean
        for (listener in listeners) {
            if (!listener.shouldHandleEvent(event)) {
                continue
            }

            wasHandled = listener.handleEvent(bundle)
            if (wasHandled) {
                break
            }
        }

        val intent = Intent(EventSignaler.SIGNAL_NOT_HANDLED)
        intent.replaceExtras(bundle)
        context.sendOrderedBroadcast(intent, null)
    }
}