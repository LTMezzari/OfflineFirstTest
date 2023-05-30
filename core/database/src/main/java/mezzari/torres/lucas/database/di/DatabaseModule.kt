package mezzari.torres.lucas.database.di

import android.app.Application
import androidx.room.Room
import mezzari.torres.lucas.database.AppDatabase
import mezzari.torres.lucas.database.store.cache.CacheStoreImpl
import mezzari.torres.lucas.database.store.cache.CacheStore
import mezzari.torres.lucas.database.store.repository.RepositoriesStore
import mezzari.torres.lucas.database.store.repository.RepositoriesStoreImpl
import mezzari.torres.lucas.database.store.user.UserStore
import mezzari.torres.lucas.database.store.user.UserStoreImpl
import org.koin.dsl.module

/**
 * @author Lucas T. Mezzari
 * @since 01/09/2022
 */
fun getDatabaseModule(application: Application) = module {
    single {
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "my-db"
        ).fallbackToDestructiveMigration().build()
    }
    single {
        val dataBase: AppDatabase = get()
        dataBase.getUserDao()
    }
    single {
        val dataBase: AppDatabase = get()
        dataBase.getRepositoryDao()
    }
    single {
        val dataBase: AppDatabase = get()
        dataBase.getCacheDao()
    }

    single<UserStore> {
        UserStoreImpl(get())
    }
    single<RepositoriesStore> {
        RepositoriesStoreImpl(get())
    }
    single<CacheStore> {
        CacheStoreImpl(get())
    }
}