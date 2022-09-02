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
    private val strict: Boolean = true
) : DataBoundResource.Strategy<T> {
    override suspend fun execute(
        collector: FlowCollector<Resource<T>>,
    ) {
        collector.emit(Resource.loading())
        try {
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
                            OutdatedResource.success(
                                loadedResource,
                                fetchedData
                            )
                        }
                    )
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

    abstract suspend fun onSaveData(data: T?)
    abstract suspend fun onLoadData(): T?
    open fun shouldSave(loadedData: T?, receivedData: T?): Boolean = true
    open fun shouldFetch(loadedData: T?): Boolean = true
}