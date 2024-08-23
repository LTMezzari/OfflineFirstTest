package mezzari.torres.lucas.android.synchronization

import android.content.Context
import android.util.Log
import androidx.work.*
import mezzari.torres.lucas.android.logger.AppLogger
import mezzari.torres.lucas.android.synchronization.handler.SynchronizationHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

/**
 * @author Lucas T. Mezzari
 * @since 02/09/2022
 */
class DataSynchronizationManager(
    private val manager: WorkManager,
    private val logger: AppLogger,
) : SynchronizationManager {

    override val handlers: ArrayList<SynchronizationHandler> by ::mHandlers

    override fun scheduleSynchronizations() {
        logger.logMessage("Scheduling Worker")
        manager.enqueueUniquePeriodicWork(
            this::class.java.name,
            ExistingPeriodicWorkPolicy.KEEP,
            buildWorkRequest() as PeriodicWorkRequest
        )
    }

    override fun cancelSynchronization() {
        logger.logMessage("Canceling Worker")
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
            15,
            TimeUnit.MINUTES,
            1,
            TimeUnit.HOURS
        ).setConstraints(buildConstraints()).build()
    }

    class SynchronizationWorker(
        context: Context,
        params: WorkerParameters
    ) : CoroutineWorker(context, params), KoinComponent {

        private val logger: AppLogger by inject()

        override suspend fun doWork(): Result {
            logger.logMessage("Starting Work")
            for (handler in mHandlers) {
                val wasSuccessful = handler.synchronize()
                logger.logMessage(
                    "${handler.javaClass.simpleName} was successful? $wasSuccessful"
                )
                if (!wasSuccessful)
                    return Result.failure()
            }
            return Result.success()
        }
    }

    companion object {
        val mHandlers: ArrayList<SynchronizationHandler> = ArrayList()
    }
}