package mezzari.torres.lucas.offlinefirst.database.dao

import androidx.room.*
import mezzari.torres.lucas.offlinefirst.database.entities.RepositoryEntity

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
@Dao
interface RepositoryDao {
    @Transaction
    @Query("SELECT * FROM repositoryentity WHERE userId = :userId")
    suspend fun getRepositories(userId: String): List<RepositoryEntity>?

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRepositories(repositories: List<RepositoryEntity>)
}