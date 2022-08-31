package mezzari.torres.lucas.offlinefirst.network.bound.strategies

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.FlowCollector
import mezzari.torres.lucas.offlinefirst.network.wrapper.OfflineResource
import mezzari.torres.lucas.offlinefirst.network.wrapper.Resource
import mezzari.torres.lucas.offlinefirst.network.wrapper.Response

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
abstract class AutomaticStrategy<T> : OfflineStrategy<T>(false) {
    override suspend fun execute(
        collector: FlowCollector<Resource<T>>,
        call: Deferred<Response<T>>
    ) {
        collector.emit(Resource.loading())
        val loadedData = onLoadData()
        val loadedResource = Resource.success(loadedData)

        if (!shouldFetch(loadedData))
            return

        when (val result = call.await()) {
            is Response.Success -> {
                val fetchedData = result.data
                collector.emit(Resource.success(fetchedData))
                if (shouldSave(loadedData, fetchedData)) {
                    onSaveData(fetchedData)
                }
            }
            is Response.Failure -> {
                collector.emit(OfflineResource.create(loadedResource, result.error, result.data))
            }
        }
    }
}