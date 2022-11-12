package mezzari.torres.lucas.android.synchronization

import android.content.Context
import android.util.Log
import androidx.work.*
import mezzari.torres.lucas.android.synchronization.handler.SynchronizationHandler
import java.util.concurrent.TimeUnit

/**
 * @author Lucas T. Mezzari
 * @since 02/09/2022
 */
class DataSynchronizationManager(private val manager: WorkManager) : SynchronizationManager {

    override val handlers: ArrayList<SynchronizationHandler> by ::mHandlers

    override fun scheduleSynchronizations() {
        Log.d(javaClass.simpleName, "Scheduling Worker")
        manager.enqueueUniquePeriodicWork(
            this::class.java.name,
            ExistingPeriodicWorkPolicy.KEEP,
            buildWorkRequest() as PeriodicWorkRequest
        )
    }

    override fun cancelSynchronization() {
        Log.d(javaClass.simpleName, "Canceling Worker")
        manager.cancelUniqueWork(this::class.java.name)
    }

    private fun buildConstraints(): Constraints {
        return Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }

    private fun buildWorkRequest(): WorkRequest {
        return PeriodicWorkRequest.Builder(
            SynchronizationWorker::class.java,
            3,
            TimeUnit.MINUTES,
            1,
            TimeUnit.HOURS
        ).setConstraints(buildConstraints()).build()
    }

    class SynchronizationWorker(
        context: Context,
        params: WorkerParameters
    ) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            Log.d(DataSynchronizationManager::class.java.simpleName, "Starting Work")
            for (handler in mHandlers) {
                val wasSuccessful = handler.synchronize()
                Log.d(
                    javaClass.simpleName,
                    "${handler.javaClass.simpleName} was successful? $wasSuccessful"
                )
                if (!wasSuccessful)
                    return Result.Retry()
            }
            return Result.success()
        }
    }

    companion object {
        val mHandlers: ArrayList<SynchronizationHandler> = ArrayList()
    }
}