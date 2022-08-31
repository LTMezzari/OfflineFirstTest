package mezzari.torres.lucas.offlinefirst.database.dao

import androidx.room.*
import mezzari.torres.lucas.offlinefirst.database.entities.UserEntity

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
@Dao
interface UserDao {
    @Transaction
    @Query("SELECT * FROM userentity WHERE username = :userId")
    suspend fun getUser(userId: String): List<UserEntity>?

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(vararg user: UserEntity)
}