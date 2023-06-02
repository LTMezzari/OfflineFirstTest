package mezzari.torres.lucas.network.strategies

import kotlinx.coroutines.Deferred
import mezzari.torres.lucas.core.resource.bound.DataBoundResource
import mezzari.torres.lucas.network.wrapper.Response

/**
 * @author Lucas T. Mezzari
 * @since 01/06/2023
 */
abstract class NetworkStrategy<T>(val call: () -> Deferred<Response<T>>): DataBoundResource.Strategy<T>