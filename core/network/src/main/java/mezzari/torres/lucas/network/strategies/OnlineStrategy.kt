package mezzari.torres.lucas.network.strategies

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.FlowCollector
import mezzari.torres.lucas.core.resource.bound.DataBoundResource
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.network.wrapper.Response

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
class OnlineStrategy<T>(private val call: Deferred<Response<T>>): DataBoundResource.Strategy<T> {
    override suspend fun execute(collector: FlowCollector<Resource<T>>) {
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