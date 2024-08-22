package mezzari.torres.lucas.core.resource.bound

import kotlinx.coroutines.flow.FlowCollector
import mezzari.torres.lucas.core.resource.Resource

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class DataBoundResource<T>(
    private val collector: FlowCollector<Resource<T>>,
) : BoundResource<T> {
    override suspend fun execute(strategy: BoundResource.Strategy<T>) {
        strategy.execute(collector)
    }
}