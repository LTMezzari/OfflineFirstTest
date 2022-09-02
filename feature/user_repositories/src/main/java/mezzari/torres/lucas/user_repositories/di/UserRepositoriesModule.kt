package mezzari.torres.lucas.user_repositories.di

import mezzari.torres.lucas.user_repositories.ui.repositories.RepositoriesViewModel
import mezzari.torres.lucas.user_repositories.ui.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * @author Lucas T. Mezzari
 * @since 01/09/2022
 */
val userRepositoriesModule = module {
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel {
        RepositoriesViewModel(
            get(),
            get(),
            get()
        )
    }
}