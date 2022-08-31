package mezzari.torres.lucas.offlinefirst.network.module

import mezzari.torres.lucas.network.source.Network
import mezzari.torres.lucas.offlinefirst.network.adapter.DeferredCallAdapterFactory
import retrofit2.Retrofit

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class DeferredCallModule: Network.RetrofitLevelModule {
    override fun onRetrofitBuilderCreated(retrofitBuilder: Retrofit.Builder) {
        retrofitBuilder.addCallAdapterFactory(DeferredCallAdapterFactory())
    }
}