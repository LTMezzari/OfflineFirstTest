package mezzari.torres.lucas.network.strategies

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.FlowCollector
import mezzari.torres.lucas.core.resource.bound.DataBoundResource
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
    private val call: Deferred<Response<T>>,
    private val strict: Boolean = true,
    private val singleEmit: Boolean = false,
) : DataBoundResource.Strategy<T> {
    override suspend fun execute(
        collector: FlowCollector<Resource<T>>,
    ) {
        collector.emit(Resource.loading())
        val loadedResource = fetchFromDatabase()
        if (!singleEmit)
            collector.emit(loadedResource)

        if (!shouldFetch(loadedResource.data))
            return

        collector.emit(fetchFromNetwork(loadedResource))
    }

    abstract suspend fun onSaveData(data: T?)
    abstract suspend fun onLoadData(): T?
    open fun shouldSave(loadedData: T?, receivedData: T?): Boolean = true
    open fun shouldFetch(loadedData: T?): Boolean = true

    open suspend fun fetchFromDatabase(): Resource<T> {
        return try {
            val loadedData = onLoadData()
            Resource.success(loadedData)
        } catch (e: Exception) {
            Resource.error(e.message)
        }
    }

    open suspend fun fetchFromNetwork(loadedResource: Resource<T>): Resource<T> {
        return when (val result = call.await()) {
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