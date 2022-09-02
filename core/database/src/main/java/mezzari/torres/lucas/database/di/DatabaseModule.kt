package mezzari.torres.lucas.database.di

import android.app.Application
import androidx.room.Room
import mezzari.torres.lucas.database.AppDatabase
import mezzari.torres.lucas.database.repositories.cache.CacheRepository
import mezzari.torres.lucas.database.repositories.cache.ICacheRepository
import mezzari.torres.lucas.database.repositories.repository.IRepositoriesRepository
import mezzari.torres.lucas.database.repositories.repository.RepositoriesRepository
import mezzari.torres.lucas.database.repositories.user.IUserRepository
import mezzari.torres.lucas.database.repositories.user.UserRepository
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

    single<IUserRepository> {
        UserRepository(get())
    }
    single<IRepositoriesRepository> {
        RepositoriesRepository(get())
    }
    single<ICacheRepository> {
        CacheRepository(get())
    }
}