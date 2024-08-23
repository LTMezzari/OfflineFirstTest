package mezzari.torres.lucas.network.archive

import kotlinx.coroutines.Deferred
import mezzari.torres.lucas.network.wrapper.Response

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
typealias DeferredResult<ResponseType> = () -> Deferred<Response<ResponseType>>

typealias TransformResult<ResponseType, ResultType> = (ResponseType?) -> ResultType?
