package mezzari.torres.lucas.network.strategies

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.FlowCollector
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.network.wrapper.Response
import java.lang.Exception

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
class OnlineStrategy<T>(
    call: () -> Deferred<Response<T>>,
    private val onResultSuccess: (suspend (T?) -> Unit)? = null
) : NetworkStrategy<T>(call) {
    override suspend fun execute(collector: FlowCollector<Resource<T>>) {
        try {
            collector.emit(Resource.loading())

            when (val result = call().await()) {
                is Response.Success -> {
                    val fetchedData = result.data
                    collector.emit(Resource.success(fetchedData))
                    onResultSuccess?.invoke(fetchedData)
                }

                is Response.Failure -> {
                    collector.emit(Resource.error(result.error, result.data))
                }
            }
        } catch (e: Exception) {
            collector.emit(Resource.error(e.message))
        }
    }
}