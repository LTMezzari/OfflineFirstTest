package mezzari.torres.lucas.android.syncronization

import android.content.Context
import android.util.Log
import androidx.work.*
import mezzari.torres.lucas.android.syncronization.adapter.SynchronizationAdapter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

/**
 * @author Lucas T. Mezzari
 * @since 02/09/2022
 */
class DataSynchronizationManager(private val manager: WorkManager) : SynchronizationManager {
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
            TimeUnit.MINUTES
        ).setConstraints(buildConstraints()).build()
    }

    class SynchronizationWorker(
        context: Context,
        params: WorkerParameters
    ) :
        CoroutineWorker(context, params), KoinComponent {
        private val adapter: SynchronizationAdapter by inject()

        override suspend fun doWork(): Result {
            Log.d(DataSynchronizationManager::class.java.simpleName, "Starting Work")
            for (i in 0 until adapter.handlersCount()) {
                val handler = adapter.buildHandler(i)
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
}