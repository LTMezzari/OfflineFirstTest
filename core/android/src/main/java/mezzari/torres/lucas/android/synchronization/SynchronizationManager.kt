package mezzari.torres.lucas.android.synchronization

import mezzari.torres.lucas.android.synchronization.handler.SynchronizationHandler

/**
 * @author Lucas T. Mezzari
 * @since 02/09/2022
 */
interface SynchronizationManager {
    val handlers: MutableList<SynchronizationHandler>
    fun scheduleSynchronizations()
    fun cancelSynchronization()
}