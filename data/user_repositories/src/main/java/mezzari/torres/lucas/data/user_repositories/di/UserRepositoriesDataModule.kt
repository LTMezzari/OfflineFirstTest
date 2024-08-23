package mezzari.torres.lucas.data.user_repositories.di

import mezzari.torres.lucas.android.synchronization.handler.SynchronizationHandler
import mezzari.torres.lucas.network.source.Network
import mezzari.torres.lucas.data.user_repositories.GithubAPI
import mezzari.torres.lucas.data.user_repositories.repository.GithubRepository
import mezzari.torres.lucas.data.user_repositories.repository.GithubRepositoryImpl
import mezzari.torres.lucas.data.user_repositories.synchronization.RepositoryHandler
import org.koin.dsl.module

/**
 * @author Lucas T. Mezzari
 * @since 22/08/24
 */
val userRepositoriesDataModule = module {
    single<GithubRepository> {
        GithubRepositoryImpl(
            get(),
            get(),
            get(),
            get()
        )
    }
    single<SynchronizationHandler> {
        RepositoryHandler(
            get(),
            get(),
            get(),
        )
    }
    single<GithubAPI> {
        val network: Network = get()
        network.build(GithubAPI::class)
    }
}