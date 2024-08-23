package mezzari.torres.lucas.network.archive

import kotlinx.coroutines.Deferred
import mezzari.torres.lucas.network.wrapper.Response

/**
 * @author Lucas T. Mezzari
 * @author lucas.torres@dietbox.me
 * @author lucas.mezzari1@gmail.com
 *
 * @location Rio Grande do Sul, Brasil
 * @since 23/08/24
 */
typealias DeferredResult<ResponseType> = () -> Deferred<Response<ResponseType>>

typealias TransformResult<ResponseType, ResultType> = (ResponseType?) -> ResultType?
