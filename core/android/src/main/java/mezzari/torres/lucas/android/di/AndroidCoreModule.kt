package mezzari.torres.lucas.android.di

import android.content.Context
import androidx.work.WorkManager
import mezzari.torres.lucas.android.persistence.preferences.IPreferencesManager
import mezzari.torres.lucas.android.persistence.preferences.PreferencesManager
import mezzari.torres.lucas.android.persistence.session.ISessionManager
import mezzari.torres.lucas.android.persistence.session.SessionManager
import mezzari.torres.lucas.android.syncronization.DataSynchronizationManager
import mezzari.torres.lucas.android.syncronization.SynchronizationManager
import mezzari.torres.lucas.android.syncronization.adapter.DataSynchronizationAdapter
import mezzari.torres.lucas.android.syncronization.adapter.SynchronizationAdapter
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * @author Lucas T. Mezzari
 * @since 02/09/2022
 */
fun getAndroidModule(context: Context): Module {
    return module {
        //Persistence
        single<IPreferencesManager> { PreferencesManager(context) }
        single<ISessionManager> { SessionManager(get()) }

        //Synchronize
        single { WorkManager.getInstance(context) }
        single<SynchronizationManager> { DataSynchronizationManager(get()) }
        single<SynchronizationAdapter> { DataSynchronizationAdapter(get(), get()) }

        //Workers
        worker { DataSynchronizationManager.SynchronizationWorker(context, get()) }
    }
}