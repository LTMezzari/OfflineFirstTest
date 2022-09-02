package mezzari.torres.lucas.core.resource.bound

import kotlinx.coroutines.flow.FlowCollector

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class DataBoundResource<T> private constructor(
    private val collector: FlowCollector<mezzari.torres.lucas.core.resource.Resource<T>>,
    private val strategy: Strategy<T>,
) {
    private suspend inline fun execute() {
        strategy.execute(collector)
    }

    companion object {
        suspend operator fun <T> invoke(
            collector: FlowCollector<mezzari.torres.lucas.core.resource.Resource<T>>,
            strategy: Strategy<T>,
        ): DataBoundResource<T> {
            return DataBoundResource(
                collector,
                strategy,
            ).also {
                it.execute()
            }
        }
    }

    interface Strategy<T> {
        suspend fun execute(collector: FlowCollector<mezzari.torres.lucas.core.resource.Resource<T>>)
    }
}