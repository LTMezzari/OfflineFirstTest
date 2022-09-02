package mezzari.torres.lucas.database

import androidx.room.Database
import androidx.room.RoomDatabase
import mezzari.torres.lucas.database.dao.CacheDao
import mezzari.torres.lucas.database.dao.RepositoryDao
import mezzari.torres.lucas.database.dao.UserDao
import mezzari.torres.lucas.database.entities.CacheEntity
import mezzari.torres.lucas.database.entities.RepositoryEntity
import mezzari.torres.lucas.database.entities.UserEntity

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
@Database(version = 2, exportSchema = false, entities = [UserEntity::class, RepositoryEntity::class, CacheEntity::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun getUserDao(): UserDao
    abstract fun getRepositoryDao(): RepositoryDao
    abstract fun getCacheDao(): CacheDao
}