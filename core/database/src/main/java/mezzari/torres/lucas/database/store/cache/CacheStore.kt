package mezzari.torres.lucas.database.store.cache

import mezzari.torres.lucas.core.model.Cache

/**
 * @author Lucas T. Mezzari
 * @since 02/09/2022
 */
interface CacheStore {
    suspend fun getCache(cacheId: String): Cache?
    suspend fun saveCache(vararg caches: Cache)
}