package mezzari.torres.lucas.offlinefirst.di

import android.app.Application
import mezzari.torres.lucas.core.di.coreModule
import mezzari.torres.lucas.database.di.getDatabaseModule
import mezzari.torres.lucas.network.di.networkModule
import mezzari.torres.lucas.offlinefirst.ui.repositories.RepositoriesViewModel
import mezzari.torres.lucas.offlinefirst.ui.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
private val viewModelModule = module {
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { RepositoriesViewModel(get(), get(), get()) }
}

fun getModules(application: Application): List<Module> {
    return listOf(
        coreModule,
        getDatabaseModule(application),
        networkModule,
        viewModelModule
    )
}