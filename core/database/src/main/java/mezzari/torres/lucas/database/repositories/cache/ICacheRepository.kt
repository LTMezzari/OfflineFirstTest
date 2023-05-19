package mezzari.torres.lucas.database.repositories.cache

import mezzari.torres.lucas.core.model.bo.Cache

/**
 * @author Lucas T. Mezzari
 * @since 02/09/2022
 */
interface ICacheRepository {
    suspend fun getCache(cacheId: String): Cache?
    suspend fun saveCache(vararg caches: Cache)
}