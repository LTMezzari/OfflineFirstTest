package mezzari.torres.lucas.offlinefirst

import mezzari.torres.lucas.offlinefirst.di.getModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@Application)
            modules(getModules(this@Application))
        }
    }
}