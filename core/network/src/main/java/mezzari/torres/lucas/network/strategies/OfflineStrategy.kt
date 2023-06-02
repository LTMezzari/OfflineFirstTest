package mezzari.torres.lucas.network.strategies

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.FlowCollector
import mezzari.torres.lucas.network.wrapper.OfflineResource
import mezzari.torres.lucas.core.resource.OutdatedResource
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.network.wrapper.Response
import java.lang.Exception

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
abstract class OfflineStrategy<T>(
    call: () -> Deferred<Response<T>>,
    private val strict: Boolean = true,
    private val singleEmit: Boolean = false,
) : NetworkStrategy<T>(call) {
    override suspend fun execute(
        collector: FlowCollector<Resource<T>>,
    ) {
        try {
            collector.emit(Resource.loading())
            val loadedResource = fetchFromDatabase()
            if (!singleEmit)
                collector.emit(loadedResource)

            if (!shouldFetch(loadedResource.data)) {
                if (singleEmit)
                    collector.emit(loadedResource)
                return
            }

            collector.emit(fetchFromNetwork(loadedResource))
        } catch (e: Exception) {
            collector.emit(Resource.error(e.message))
        }
    }

    abstract suspend fun onSaveData(data: T?)
    abstract suspend fun onLoadData(): T?
    open fun shouldSave(loadedData: T?, receivedData: T?): Boolean = true
    open fun shouldFetch(loadedData: T?): Boolean = true

    open suspend fun fetchFromDatabase(): Resource<T> {
        val loadedData = onLoadData()
        return Resource.success(loadedData)
    }

    open suspend fun fetchFromNetwork(loadedResource: Resource<T>): Resource<T> {
        return when (val result = call().await()) {
            is Response.Success -> {
                val fetchedData = result.data
                if (shouldSave(loadedResource.data, fetchedData)) {
                    onSaveData(fetchedData)
                }
                if (singleEmit || strict)
                    return Resource.success(fetchedData)

                return OutdatedResource.success(loadedResource, fetchedData)
            }
            is Response.Failure -> {
                OfflineResource.create(loadedResource, result.error, result.data)
            }
        }
    }
}