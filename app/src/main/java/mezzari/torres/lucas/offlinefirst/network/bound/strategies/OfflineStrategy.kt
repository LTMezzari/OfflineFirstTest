package mezzari.torres.lucas.offlinefirst.network.bound.strategies

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.FlowCollector
import mezzari.torres.lucas.offlinefirst.network.bound.NetworkBoundResource
import mezzari.torres.lucas.offlinefirst.network.wrapper.OutdatedResource
import mezzari.torres.lucas.offlinefirst.network.wrapper.Resource
import mezzari.torres.lucas.offlinefirst.network.wrapper.Response

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
abstract class OfflineStrategy<T>(private val strict: Boolean = true) : NetworkBoundResource.Strategy<T> {
    override suspend fun execute(
        collector: FlowCollector<Resource<T>>,
        call: Deferred<Response<T>>
    ) {
        collector.emit(Resource.loading())
        val loadedData = onLoadData()
        val loadedResource = Resource.success(loadedData)
        collector.emit(loadedResource)

        if (!shouldFetch(loadedData))
            return

        when (val result = call.await()) {
            is Response.Success -> {
                val fetchedData = result.data
                collector.emit(
                    if (strict) {
                        Resource.success(fetchedData)
                    } else {
                        OutdatedResource.success(loadedResource, fetchedData)
                    }
                )
                if (shouldSave(loadedData, fetchedData)) {
                    onSaveData(fetchedData)
                }
            }
            is Response.Failure -> {
                collector.emit(Resource.error(result.error, result.data))
            }
        }
    }

    abstract suspend fun onSaveData(data: T?)
    abstract suspend fun onLoadData(): T?
    open fun shouldSave(loadedData: T?, receivedData: T?): Boolean = true
    open fun shouldFetch(loadedData: T?): Boolean = true
}