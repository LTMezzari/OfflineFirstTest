package mezzari.torres.lucas.android.syncronization

/**
 * @author Lucas T. Mezzari
 * @since 02/09/2022
 */
interface SynchronizationManager {
    fun scheduleSynchronizations()
    fun cancelSynchronization()
}