package mezzari.torres.lucas.android.worker.boot

import android.content.Context
import android.content.Intent
import mezzari.torres.lucas.android.boot.BootInterceptor
import mezzari.torres.lucas.android.worker.WorkGroupManager

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
class BootWorkers(private val manager: WorkGroupManager): BootInterceptor {
    override fun onDeviceBoot(context: Context, intent: Intent) {
        manager.dispatchWorkers()
    }
}