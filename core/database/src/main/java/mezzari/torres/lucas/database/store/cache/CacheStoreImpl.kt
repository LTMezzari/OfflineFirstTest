package mezzari.torres.lucas.database.store.cache

import mezzari.torres.lucas.core.model.bo.Cache
import mezzari.torres.lucas.database.dao.CacheDao
import mezzari.torres.lucas.database.entities.asEntity
import mezzari.torres.lucas.database.entities.asEntry

/**
 * @author Lucas T. Mezzari
 * @since 02/09/2022
 */
class CacheStoreImpl(private val dao: CacheDao): CacheStore {
    override suspend fun getCache(cacheId: String): Cache? {
        val cache = dao.getCache(cacheId) ?: return null
        if (cache.isEmpty())
            return null
        return cache.first().asEntry()
    }

    override suspend fun saveCache(vararg caches: Cache?): Boolean {
        val list = caches.mapNotNull { it?.asEntity() }
        if (list.isEmpty())
            return false
        dao.putCache(list)
        return true
    }
}