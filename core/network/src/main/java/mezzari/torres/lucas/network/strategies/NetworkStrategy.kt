package mezzari.torres.lucas.network.strategies

import mezzari.torres.lucas.core.resource.bound.BoundResource
import mezzari.torres.lucas.network.archive.DeferredResult
import mezzari.torres.lucas.network.archive.TransformResult

/**
 * @author Lucas T. Mezzari
 * @since 01/06/2023
 */
abstract class NetworkStrategy<ResponseType, ResultType>(
    protected val call: DeferredResult<ResponseType>,
    protected val onTransform: TransformResult<ResponseType, ResultType>? = null
): BoundResource.Strategy<ResultType> {
    open fun transformResult(response: ResponseType?): ResultType? {
        onTransform?.run {
            return invoke(response)
        }
        return response as? ResultType
    }
}