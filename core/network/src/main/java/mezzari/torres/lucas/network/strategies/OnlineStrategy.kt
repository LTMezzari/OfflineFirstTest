package mezzari.torres.lucas.network.strategies

import kotlinx.coroutines.flow.FlowCollector
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.network.archive.DeferredResult
import mezzari.torres.lucas.network.archive.TransformResult
import mezzari.torres.lucas.network.wrapper.Response
import java.lang.Exception

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
class OnlineStrategy<ResponseType, ResultType>(
    call: DeferredResult<ResponseType>,
    private val onResultSuccess: (suspend (ResultType?) -> Unit)? = null,
    onTransform: TransformResult<ResponseType, ResultType>? = null
) : NetworkStrategy<ResponseType, ResultType>(call, onTransform) {
    override suspend fun execute(collector: FlowCollector<Resource<ResultType>>) {
        try {
            collector.emit(Resource.loading())

            when (val result = call().await()) {
                is Response.Success -> {
                    val fetchedData = transformResult(result.data)
                    collector.emit(Resource.success(fetchedData))
                    onResultSuccess?.invoke(fetchedData)
                }

                is Response.Failure -> {
                    val data = transformResult(result.data)
                    collector.emit(Resource.error(result.error, data))
                }
            }
        } catch (e: Exception) {
            collector.emit(Resource.error(e.message))
        }
    }
}