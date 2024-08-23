package mezzari.torres.lucas.android.di

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.work.WorkManager
import mezzari.torres.lucas.android.boot.BootInterceptor
import mezzari.torres.lucas.android.file.FileProvider
import mezzari.torres.lucas.android.file.provider.ImageProvider
import mezzari.torres.lucas.android.logger.AppLogger
import mezzari.torres.lucas.android.logger.AppLoggerImpl
import mezzari.torres.lucas.android.logger.crashlytics.CrashlyticsHandler
import mezzari.torres.lucas.android.logger.crashlytics.CrashlyticsHandlerImpl
import mezzari.torres.lucas.android.notification.dispatcher.NotificationDispatcher
import mezzari.torres.lucas.android.notification.dispatcher.NotificationDispatcherImpl
import mezzari.torres.lucas.android.notification.local.LocalNotificationScheduler
import mezzari.torres.lucas.android.permissions.PermissionsManager
import mezzari.torres.lucas.android.permissions.PermissionsManagerImpl
import mezzari.torres.lucas.android.persistence.preferences.PreferencesManager
import mezzari.torres.lucas.android.persistence.preferences.PreferencesManagerImpl
import mezzari.torres.lucas.android.persistence.session.SessionManager
import mezzari.torres.lucas.android.persistence.session.SessionManagerImpl
import mezzari.torres.lucas.android.review.ReviewHandler
import mezzari.torres.lucas.android.review.ReviewHandlerImpl
import mezzari.torres.lucas.android.shortcut.ShortcutHandler
import mezzari.torres.lucas.android.shortcut.ShortcutHandlerImpl
import mezzari.torres.lucas.android.signaler.EventSignaler
import mezzari.torres.lucas.android.signaler.EventSignalerImpl
import mezzari.torres.lucas.android.synchronization.DataSynchronizationManager
import mezzari.torres.lucas.android.synchronization.SynchronizationManager
import mezzari.torres.lucas.android.translation.StringTranslation
import mezzari.torres.lucas.android.translation.StringTranslationImpl
import mezzari.torres.lucas.android.worker.WorkGroupManager
import mezzari.torres.lucas.android.worker.WorkGroupManagerImpl
import mezzari.torres.lucas.android.worker.WorkScheduler
import mezzari.torres.lucas.android.worker.boot.BootWorkers
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

/**
 * @author Lucas T. Mezzari
 * @since 02/09/2022
 */
fun getAndroidModule(context: Context): Module {
    return module {
        //Logs
        single<AppLogger> { AppLoggerImpl(get()) }

        //Signalers
        single<EventSignaler> { EventSignalerImpl(get(), getAll()) }

        //Persistence
        single<PreferencesManager> { PreferencesManagerImpl(context) }
        single<SessionManager> { SessionManagerImpl(get()) }

        //Android
        single<ShortcutHandler> { ShortcutHandlerImpl(get()) }
        single<ReviewHandler> { ReviewHandlerImpl(get(), get()) }
        single<PermissionsManager> { PermissionsManagerImpl(get(), get()) }
        single<StringTranslation> { StringTranslationImpl(get()) }

        //Providers
        single<FileProvider<Bitmap>>(named("bitmap")) { ImageProvider(get(), get(), get()) }

        //Notifications
        single<WorkScheduler> { LocalNotificationScheduler(get(), get(), get()) }
        single<NotificationDispatcher> { NotificationDispatcherImpl(get()) }

        //Crashlytics
        single<CrashlyticsHandler> { CrashlyticsHandlerImpl() }

        //Boot
        single<List<BootInterceptor>> { getAll() }
        single<BootInterceptor> { BootWorkers(get()) }

        //Workers
        single<WorkGroupManager> {
            val managers = getAll<WorkScheduler>()
            return@single WorkGroupManagerImpl(managers)
        }

        //Synchronize
        single { WorkManager.getInstance(context) }
        single<SynchronizationManager> {
            val manager = DataSynchronizationManager(get(), get())
            manager.handlers.addAll(getAll())
            return@single manager
        }

        //Workers
        worker { DataSynchronizationManager.SynchronizationWorker(context, get()) }
    }
}