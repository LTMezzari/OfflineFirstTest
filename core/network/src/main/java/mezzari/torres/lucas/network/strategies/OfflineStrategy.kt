package mezzari.torres.lucas.network.strategies

import kotlinx.coroutines.flow.FlowCollector
import mezzari.torres.lucas.network.wrapper.OfflineResource
import mezzari.torres.lucas.core.resource.OutdatedResource
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.network.archive.DeferredResult
import mezzari.torres.lucas.network.archive.TransformResult
import mezzari.torres.lucas.network.wrapper.Response
import java.lang.Exception

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
abstract class OfflineStrategy<ResponseType, ResultType>(
    call: DeferredResult<ResponseType>,
    private val strict: Boolean = true,
    private val singleEmit: Boolean = false,
    onTransform: TransformResult<ResponseType, ResultType>? = null
) : NetworkStrategy<ResponseType, ResultType>(call, onTransform) {
    override suspend fun execute(
        collector: FlowCollector<Resource<ResultType>>,
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

    abstract suspend fun onSaveData(data: ResponseType?)
    abstract suspend fun onLoadData(): ResultType?
    open fun shouldSave(loadedData: ResultType?, receivedData: ResultType?): Boolean = true
    open fun shouldFetch(loadedData: ResultType?): Boolean = true

    open suspend fun fetchFromDatabase(): Resource<ResultType> {
        val loadedData = onLoadData()
        return Resource.success(loadedData)
    }

    open suspend fun fetchFromNetwork(loadedResource: Resource<ResultType>): Resource<ResultType> {
        return when (val result = call().await()) {
            is Response.Success -> {
                val fetchedData = transformResult(result.data)
                if (shouldSave(loadedResource.data, fetchedData)) {
                    onSaveData(result.data)
                }
                if (singleEmit || strict)
                    return Resource.success(fetchedData)

                return OutdatedResource.success(loadedResource, fetchedData)
            }
            is Response.Failure -> {
                val data = transformResult(result.data)
                OfflineResource.create(loadedResource, result.error, data)
            }
        }
    }
}