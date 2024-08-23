package mezzari.torres.lucas.android.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
class BootService : BroadcastReceiver(), KoinComponent {

    private val interceptors: List<BootInterceptor> by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        val mContext = context ?: return
        val mIntent = intent ?: return
        if (Intent.ACTION_BOOT_COMPLETED != mIntent.action) {
            return
        }

        interceptors.forEach {
            it.onDeviceBoot(mContext, mIntent)
        }
    }
}