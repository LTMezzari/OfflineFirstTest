package mezzari.torres.lucas.offlinefirst.network.bound

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.FlowCollector
import mezzari.torres.lucas.offlinefirst.network.wrapper.Resource
import mezzari.torres.lucas.offlinefirst.network.wrapper.Response

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class NetworkBoundResource<T> private constructor(
    private val collector: FlowCollector<Resource<T>>,
    private val call: Deferred<Response<T>>,
    private val strategy: Strategy<T>,
) {
    private suspend inline fun execute() {
        strategy.execute(collector, call)
    }

    companion object {
        suspend operator fun <T> invoke(
            collector: FlowCollector<Resource<T>>,
            call: Deferred<Response<T>>,
            strategy: Strategy<T>,
        ): NetworkBoundResource<T> {
            return NetworkBoundResource(
                collector,
                call,
                strategy,
            ).also {
                it.execute()
            }
        }
    }

    interface Strategy<T> {
        suspend fun execute(collector: FlowCollector<Resource<T>>, call: Deferred<Response<T>>)
    }
}