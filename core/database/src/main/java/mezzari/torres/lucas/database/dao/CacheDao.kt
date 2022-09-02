package mezzari.torres.lucas.database.dao

import androidx.room.*
import mezzari.torres.lucas.database.entities.CacheEntity

/**
 * @author Lucas T. Mezzari
 * @since 02/09/2022
 */
@Dao
interface CacheDao {
    @Transaction
    @Query("SELECT * FROM cacheentity WHERE id = :cacheId")
    suspend fun getCache(cacheId: String): List<CacheEntity>?

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putCache(caches: List<CacheEntity>)
}