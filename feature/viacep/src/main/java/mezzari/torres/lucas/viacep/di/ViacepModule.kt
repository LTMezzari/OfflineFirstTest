package mezzari.torres.lucas.viacep.di

import mezzari.torres.lucas.network.source.Network
import mezzari.torres.lucas.viacep.ViacepAPI
import mezzari.torres.lucas.viacep.repository.AddressRepository
import mezzari.torres.lucas.viacep.repository.AddressRepositoryImpl
import mezzari.torres.lucas.viacep.ui.details.AddressDetailViewModel
import mezzari.torres.lucas.viacep.ui.search.SearchAddressViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * @author Lucas T. Mezzari
 * @since 30/05/2023
 */
val viacepModule = module {
    single<ViacepAPI> {
        val network: Network = get()
        network.build(ViacepAPI::class)
    }
    single<AddressRepository> {
        AddressRepositoryImpl(get())
    }
    viewModel {
        SearchAddressViewModel(get(), get())
    }
    viewModel {
        AddressDetailViewModel()
    }
}