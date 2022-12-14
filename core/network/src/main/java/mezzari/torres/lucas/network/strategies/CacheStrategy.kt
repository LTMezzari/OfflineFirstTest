package mezzari.torres.lucas.network.strategies

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Deferred
import mezzari.torres.lucas.core.model.Cache
import mezzari.torres.lucas.database.store.cache.CacheStore
import mezzari.torres.lucas.network.wrapper.Response
import java.lang.reflect.Type

/**
 * @author Lucas T. Mezzari
 * @since 02/09/2022
 */
class CacheStrategy<T> constructor(
    private val callId: String,
    private val repository: CacheStore,
    call: Deferred<Response<T>>,
    strict: Boolean = true,
    singleEmit: Boolean = false,
    private val type: Type,
) : OfflineStrategy<T>(call, strict, singleEmit) {
    override suspend fun onSaveData(data: T?) {
        val response = data ?: return
        repository.saveCache(transform(response))
    }

    override suspend fun onLoadData(): T? {
        return repository.getCache(callId)?.parse(type) as? T
    }

    private fun <T> Cache.parse(type: Type): T? {
        if (response.isBlank() || response.isEmpty())
            return null
        return Gson().fromJson(response, type) as? T
    }

    private fun transform(response: T): Cache {
        return Cache(
            callId,
            Gson().toJson(response)
        )
    }

    companion object {
        inline operator fun <reified T> invoke(
            callId: String,
            repository: CacheStore,
            call: Deferred<Response<T>>,
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