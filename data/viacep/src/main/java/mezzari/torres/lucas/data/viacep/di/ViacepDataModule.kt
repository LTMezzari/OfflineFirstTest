package mezzari.torres.lucas.data.viacep.di

import mezzari.torres.lucas.data.viacep.ViacepAPI
import mezzari.torres.lucas.data.viacep.repository.AddressRepository
import mezzari.torres.lucas.data.viacep.repository.AddressRepositoryImpl
import mezzari.torres.lucas.network.source.Network
import org.koin.dsl.module

/**
 * @author Lucas T. Mezzari
 * @author lucas.torres@dietbox.me
 * @author lucas.mezzari1@gmail.com
 *
 * @location Rio Grande do Sul, Brasil
 * @since 22/08/24
 */
val viacepDataModule = module {
    single<ViacepAPI> {
        val network: Network = get()
        network.build(ViacepAPI::class)
    }
    single<AddressRepository> {
        AddressRepositoryImpl(get())
    }
}