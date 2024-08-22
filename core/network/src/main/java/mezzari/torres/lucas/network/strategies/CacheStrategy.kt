package mezzari.torres.lucas.network.strategies

import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Deferred
import mezzari.torres.lucas.core.model.bo.Cache
import mezzari.torres.lucas.database.store.cache.CacheStore
import mezzari.torres.lucas.network.wrapper.Response
import java.lang.IllegalArgumentException
import java.lang.reflect.Type
import mezzari.torres.lucas.network.archive.*

/**
 * @author Lucas T. Mezzari
 * @since 02/09/2022
 */
class CacheStrategy<T> (
    private val callId: String,
    private val store: CacheStore,
    call: () -> Deferred<Response<T>>,
    strict: Boolean = true,
    singleEmit: Boolean = false,
    private val type: Type,
) : OfflineStrategy<T>(call, strict, singleEmit) {
    override suspend fun onSaveData(data: T?) {
        val response = data ?: return
        store.saveCache(transform(response))
    }

    override suspend fun onLoadData(): T? {
        if (callId.trim().isEmpty())
            throw IllegalArgumentException("Call Id should not be empty when using Cache Strategy")
        return store.getCache(callId)?.parse(type) as? T
    }

    private fun <T> Cache.parse(type: Type): T? {
        if (response.isBlank() || response.isEmpty())
            return null
        return fromJson(response, type)
    }

    private fun transform(response: T): Cache {
        return Cache(
            callId,
            response.toJson()
        )
    }

    companion object {
        inline operator fun <reified T> invoke(
            callId: String,
            repository: CacheStore,
            noinline call: () -> Deferred<Response<T>>,
            strict: Boolean = true,
            singleEmit: Boolean = false
        ): CacheStrategy<T> {
            return CacheStrategy(
                callId,
                repository,
                call,
                strict,
                singleEmit,
                type = object : TypeToken<T>() {}.type
            )
        }
    }
}