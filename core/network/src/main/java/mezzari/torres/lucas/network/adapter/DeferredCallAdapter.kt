package mezzari.torres.lucas.network.adapter

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import mezzari.torres.lucas.network.wrapper.Response
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import java.lang.reflect.Type

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class DeferredCallAdapter<R>(
    private val responseType: Type,
): CallAdapter<R, Deferred<Response<R>>> {
    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): Deferred<Response<R>> {
        val deferred = CompletableDeferred<Response<R>>()
        call.enqueue(object: Callback<R> {
            override fun onResponse(call: Call<R>, response: retrofit2.Response<R>) {
                deferred.complete(Response.create(response))
            }

            override fun onFailure(call: Call<R>, t: Throwable) {
                deferred.complete(Response.create(t))
            }
        })
        return deferred
    }
}