package mezzari.torres.lucas.network.strategies

import com.google.gson.reflect.TypeToken
import mezzari.torres.lucas.core.model.bo.Cache
import mezzari.torres.lucas.database.store.cache.CacheStore
import java.lang.IllegalArgumentException
import java.lang.reflect.Type
import mezzari.torres.lucas.network.archive.*

/**
 * @author Lucas T. Mezzari
 * @since 02/09/2022
 */
class CacheStrategy<ResponseType, ResultType>(
    private val callId: String,
    private val store: CacheStore,
    call: DeferredResult<ResponseType>,
    strict: Boolean = true,
    singleEmit: Boolean = false,
    onTransform: TransformResult<ResponseType, ResultType>? = null,
    private val type: Type,
) : OfflineStrategy<ResponseType, ResultType>(call, strict, singleEmit, onTransform) {
    override suspend fun onSaveData(data: ResponseType?) {
        val response = data ?: return
        store.saveCache(transform(response))
    }

    override suspend fun onLoadData(): ResultType? {
        if (callId.trim().isEmpty())
            throw IllegalArgumentException("Call Id should not be empty when using Cache Strategy")
        val response: ResponseType? = store.getCache(callId)?.parse(type) as? ResponseType
        return transformResult(response)
    }

    private fun <T> Cache.parse(type: Type): T? {
        if (response.isBlank() || response.isEmpty())
            return null
        return fromJson(response, type)
    }

    private fun transform(response: ResponseType): Cache {
        return Cache(
            callId,
            response.toJson()
        )
    }

    companion object {
        inline operator fun <reified ResponseType, reified ResultType> invoke(
            callId: String,
            repository: CacheStore,
            noinline call: DeferredResult<ResponseType>,
            strict: Boolean = true,
            singleEmit: Boolean = false,
            noinline onTransform: TransformResult<ResponseType, ResultType>? = null
        ): CacheStrategy<ResponseType, ResultType> {
            return CacheStrategy(
                callId,
                repository,
                call,
                strict,
                singleEmit,
                onTransform,
                type = object : TypeToken<ResponseType>() {}.type
            )
        }
    }
}