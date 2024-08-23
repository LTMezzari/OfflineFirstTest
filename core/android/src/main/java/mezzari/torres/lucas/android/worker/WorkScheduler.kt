package mezzari.torres.lucas.android.worker

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
interface WorkScheduler {
    fun scheduleWork()
    fun cancelWork()
}