package mezzari.torres.lucas.core.resource.bound

import kotlinx.coroutines.flow.FlowCollector
import mezzari.torres.lucas.core.resource.Resource

/**
 * @author Lucas T. Mezzari
 * @since 11/11/2022
 */
interface BoundResource<T> {
    suspend fun execute(strategy: Strategy<T>)

    interface Strategy<T> {
        suspend fun execute(collector: FlowCollector<Resource<T>>)
    }
}