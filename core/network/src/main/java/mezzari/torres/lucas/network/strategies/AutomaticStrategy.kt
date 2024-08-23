package mezzari.torres.lucas.network.strategies

import kotlinx.coroutines.flow.FlowCollector
import mezzari.torres.lucas.network.wrapper.OfflineResource
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.network.archive.DeferredResult
import mezzari.torres.lucas.network.archive.TransformResult
import mezzari.torres.lucas.network.wrapper.Response
import java.lang.Exception

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
abstract class AutomaticStrategy<ResponseType, ResultType>(
    call: DeferredResult<ResponseType>,
    onTransform: TransformResult<ResponseType, ResultType>? = null
) : OfflineStrategy<ResponseType, ResultType>(call, false, true, onTransform) {
    override suspend fun execute(
        collector: FlowCollector<Resource<ResultType>>
    ) {
        try {
            collector.emit(Resource.loading())
            val loadedData = onLoadData()
            val loadedResource = Resource.success(loadedData)

            if (!shouldFetch(loadedData))
                return

            when (val result = call().await()) {
                is Response.Success -> {
                    val fetchedData = transformResult(result.data)
                    collector.emit(Resource.success(fetchedData))
                    if (shouldSave(loadedData, fetchedData)) {
                        onSaveData(result.data)
                    }
                }

                is Response.Failure -> {
                    val data = transformResult(result.data)
                    collector.emit(OfflineResource.create(loadedResource, result.error, data))
                }
            }
        } catch (e: Exception) {
            collector.emit(Resource.error(e.message))
        }
    }
}