package mezzari.torres.lucas.offlinefirst.network.bound.strategies

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.FlowCollector
import mezzari.torres.lucas.offlinefirst.network.bound.NetworkBoundResource
import mezzari.torres.lucas.offlinefirst.network.wrapper.Resource
import mezzari.torres.lucas.offlinefirst.network.wrapper.Response

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
class OnlineStrategy<T>: NetworkBoundResource.Strategy<T> {
    override suspend fun execute(collector: FlowCollector<Resource<T>>, call: Deferred<Response<T>>) {
        collector.emit(Resource.loading())

        when (val result = call.await()) {
            is Response.Success -> {
                val fetchedData = result.data
                collector.emit(Resource.success(fetchedData))
            }
            is Response.Failure -> {
                collector.emit(Resource.error(result.error, result.data))
            }
        }
    }
}