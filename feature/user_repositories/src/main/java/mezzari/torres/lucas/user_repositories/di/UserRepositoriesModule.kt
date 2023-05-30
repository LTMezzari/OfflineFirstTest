package mezzari.torres.lucas.user_repositories.di

import mezzari.torres.lucas.android.synchronization.handler.SynchronizationHandler
import mezzari.torres.lucas.user_repositories.GithubAPI
import mezzari.torres.lucas.user_repositories.repository.GithubRepositoryImpl
import mezzari.torres.lucas.user_repositories.repository.GithubRepository
import mezzari.torres.lucas.user_repositories.synchronization.RepositoryHandler
import mezzari.torres.lucas.user_repositories.ui.repositories.RepositoriesViewModel
import mezzari.torres.lucas.user_repositories.ui.search.SearchViewModel
import mezzari.torres.lucas.network.source.Network
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * @author Lucas T. Mezzari
 * @since 01/09/2022
 */
val userRepositoriesModule = module {
    single<GithubRepository> {
        GithubRepositoryImpl(
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel { SearchViewModel(get(), get(), get(), get()) }
    viewModel {
        RepositoriesViewModel(
            get(),
            get(),
            get()
        )
    }
    single<SynchronizationHandler> { RepositoryHandler(get(), get()) }
    single<GithubAPI> {
        val network: Network = get()
        network.build(GithubAPI::class)
    }
}