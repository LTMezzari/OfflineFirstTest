package mezzari.torres.lucas.network.strategies

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.FlowCollector
import mezzari.torres.lucas.network.wrapper.OfflineResource
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.network.wrapper.Response
import java.lang.Exception

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
abstract class AutomaticStrategy<T>(call: () -> Deferred<Response<T>>) :
    OfflineStrategy<T>(call, false, true) {
    override suspend fun execute(
        collector: FlowCollector<Resource<T>>
    ) {
        try {
            collector.emit(Resource.loading())
            val loadedData = onLoadData()
            val loadedResource = Resource.success(loadedData)

            if (!shouldFetch(loadedData))
                return

            when (val result = call().await()) {
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
        } catch (e: Exception) {
            collector.emit(Resource.error(e.message))
        }
    }
}