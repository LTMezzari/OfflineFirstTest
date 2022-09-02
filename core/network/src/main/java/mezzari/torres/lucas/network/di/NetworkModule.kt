package mezzari.torres.lucas.network.di

import mezzari.torres.lucas.network.module.DeferredCallModule
import mezzari.torres.lucas.network.source.Network
import mezzari.torres.lucas.network.source.module.client.LogModule
import mezzari.torres.lucas.network.source.module.retrofit.GsonConverterModule
import org.koin.dsl.module

/**
 * @author Lucas T. Mezzari
 * @since 01/09/2022
 */
val networkModule = module {
    single {
        Network.initialize(
            retrofitLevelModules = listOf(
                GsonConverterModule(),
                DeferredCallModule()
            ),
            okHttpClientLevelModule = listOf(LogModule())
        )
        return@single Network
    }
    single<mezzari.torres.lucas.network.IGithubAPI> {
        val network: Network = get()
        network.build(mezzari.torres.lucas.network.IGithubAPI::class)
    }
    single<mezzari.torres.lucas.network.service.IGithubService> {
        mezzari.torres.lucas.network.service.GithubService(
            get(),
            get(),
            get(),
            get()
        )
    }
}