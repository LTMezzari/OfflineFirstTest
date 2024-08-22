package mezzari.torres.lucas.feature.viacep.di

import mezzari.torres.lucas.feature.viacep.ui.details.AddressDetailViewModel
import mezzari.torres.lucas.feature.viacep.ui.search.SearchAddressViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * @author Lucas T. Mezzari
 * @since 30/05/2023
 */
val viacepFeatureModule = module {
    viewModel {
        SearchAddressViewModel(get(), get())
    }
    viewModel {
        AddressDetailViewModel()
    }
}