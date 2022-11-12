package mezzari.torres.lucas.offlinefirst

import mezzari.torres.lucas.android.synchronization.SynchronizationManager
import mezzari.torres.lucas.offlinefirst.di.getModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class Application : android.app.Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@Application)
            workManagerFactory()
            modules(getModules(this@Application))
        }

        val synchronizationManager: SynchronizationManager = get()
        synchronizationManager.scheduleSynchronizations()
    }
}