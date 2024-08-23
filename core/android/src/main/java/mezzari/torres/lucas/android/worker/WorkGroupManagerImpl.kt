package mezzari.torres.lucas.android.worker

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
class WorkGroupManagerImpl(private val schedulers: List<WorkScheduler>): WorkGroupManager {
    override fun dispatchWorkers(): Boolean {
        return try {
            schedulers.forEach {
                it.scheduleWork()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun cancelWorkers(): Boolean {
        return try {
            schedulers.forEach {
                it.cancelWork()
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}