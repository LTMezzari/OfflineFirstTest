package mezzari.torres.lucas.offlinefirst.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import mezzari.torres.lucas.network.source.Network
import mezzari.torres.lucas.network.source.module.client.LogModule
import mezzari.torres.lucas.network.source.module.retrofit.GsonConverterModule
import mezzari.torres.lucas.offlinefirst.database.AppDatabase
import mezzari.torres.lucas.offlinefirst.database.dao.UserDao
import mezzari.torres.lucas.offlinefirst.database.repository.repositories.IRepositoriesRepository
import mezzari.torres.lucas.offlinefirst.database.repository.repositories.RepositoriesRepository
import mezzari.torres.lucas.offlinefirst.database.repository.user.IUserRepository
import mezzari.torres.lucas.offlinefirst.database.repository.user.UserRepository
import mezzari.torres.lucas.offlinefirst.interfaces.IAppDispatcher
import mezzari.torres.lucas.offlinefirst.network.IGithubAPI
import mezzari.torres.lucas.offlinefirst.network.module.DeferredCallModule
import mezzari.torres.lucas.offlinefirst.network.service.GithubService
import mezzari.torres.lucas.offlinefirst.network.service.IGithubService
import mezzari.torres.lucas.offlinefirst.persistence.ISessionManager
import mezzari.torres.lucas.offlinefirst.persistence.SessionManager
import mezzari.torres.lucas.offlinefirst.ui.repositories.RepositoriesViewModel
import mezzari.torres.lucas.offlinefirst.ui.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
private val coroutinesModule = module {
    single<IAppDispatcher> {
        object : IAppDispatcher {
            override var main: CoroutineContext = Dispatchers.Main
            override var io: CoroutineContext = Dispatchers.IO
        }
    }
}

private val networkModule = module {
    single {
        Network.initialize(
            retrofitLevelModules = listOf(GsonConverterModule(), DeferredCallModule()),
            okHttpClientLevelModule = listOf(LogModule())
        )
        return@single Network
    }
    single<IGithubAPI> {
        val network: Network = get()
        network.build(IGithubAPI::class)
    }
    single<IGithubService> { GithubService(get(), get(), get()) }
}

private val persistenceModule = module {
    single<ISessionManager> { SessionManager() }
}

private fun getDatabaseModule(application: Application) = module {
    single {
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "my-db"
        ).build()
    }
    single {
        val dataBase: AppDatabase = get()
        dataBase.getUserDao()
    }
    single {
        val dataBase: AppDatabase = get()
        dataBase.getRepositoryDao()
    }

    single<IUserRepository> {
        UserRepository(get())
    }
    single<IRepositoriesRepository> {
        RepositoriesRepository(get())
    }
}

private val viewModelModule = module {
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { RepositoriesViewModel(get(), get(), get()) }
}

fun getModules(application: Application): List<Module> {
    return listOf(
        coroutinesModule,
        getDatabaseModule(application),
        networkModule,
        persistenceModule,
        viewModelModule
    )
}