package mezzari.torres.lucas.database.strategy

import kotlinx.coroutines.flow.FlowCollector
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.core.resource.bound.DataBoundResource
import java.lang.Exception

/**
 * @author Lucas T. Mezzari
 * @since 01/09/2022
 */
abstract class DatabaseStrategy<T>: DataBoundResource.Strategy<T> {
    override suspend fun execute(collector: FlowCollector<Resource<T>>) {
        try {
            collector.emit(Resource.loading())
            collector.emit(Resource.success(onLoadData()))
        } catch (e: Exception) {
            collector.emit(Resource.error(e.message))
        }
    }

    abstract suspend fun onLoadData(): T?
}