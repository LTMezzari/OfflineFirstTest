package mezzari.torres.lucas.database.strategy

import kotlinx.coroutines.flow.FlowCollector
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.core.resource.bound.BoundResource
import java.lang.Exception

/**
 * @author Lucas T. Mezzari
 * @since 01/09/2022
 */
abstract class DatabaseStrategy<ResultType>: BoundResource.Strategy<ResultType> {
    override suspend fun execute(collector: FlowCollector<Resource<ResultType>>) {
        try {
            collector.emit(Resource.loading())
            collector.emit(Resource.success(onLoadData()))
        } catch (e: Exception) {
            collector.emit(Resource.error(e.message))
        }
    }

    abstract suspend fun onLoadData(): ResultType?

    companion object {
        inline fun <reified T> create(noinline databaseCall: suspend () -> T?): DatabaseStrategy<T> {
            return object : DatabaseStrategy<T>() {
                override suspend fun onLoadData(): T? {
                    return databaseCall.invoke()
                }
            }
        }
    }
}