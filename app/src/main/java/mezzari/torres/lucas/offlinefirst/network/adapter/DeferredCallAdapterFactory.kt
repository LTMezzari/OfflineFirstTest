package mezzari.torres.lucas.offlinefirst.network.adapter

import kotlinx.coroutines.Deferred
import mezzari.torres.lucas.offlinefirst.network.wrapper.Response
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class DeferredCallAdapterFactory: CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) == Deferred::class.java) {
            val enclosingType = getParameterUpperBound(0, returnType as ParameterizedType)
            val rawType = getRawType(enclosingType)

            if (rawType != Response::class.java) {
                throw IllegalArgumentException("Deferred call should pass Response as type")
            }

            if (enclosingType !is ParameterizedType) {
                throw IllegalArgumentException("Enclosing type must be parameterized")
            }

            val bodyType = getParameterUpperBound(0, enclosingType)
            return DeferredCallAdapter<Any>(bodyType)
        }
        return null
    }
}