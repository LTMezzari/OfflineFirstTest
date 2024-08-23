package mezzari.torres.lucas.android.worker

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
interface WorkGroupManager {
    fun dispatchWorkers(): Boolean

    fun cancelWorkers(): Boolean
}