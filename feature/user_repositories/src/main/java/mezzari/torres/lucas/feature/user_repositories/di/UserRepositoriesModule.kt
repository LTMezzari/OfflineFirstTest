package mezzari.torres.lucas.feature.user_repositories.di

import mezzari.torres.lucas.feature.user_repositories.ui.repositories.RepositoriesViewModel
import mezzari.torres.lucas.feature.user_repositories.ui.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * @author Lucas T. Mezzari
 * @since 01/09/2022
 */
val userRepositoriesFeatureModule = module {
    viewModel { SearchViewModel(get(), get(), get(), get()) }
    viewModel {
        RepositoriesViewModel(
            get(),
            get(),
            get()
        )
    }
}