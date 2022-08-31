package mezzari.torres.lucas.offlinefirst.database

import androidx.room.Database
import androidx.room.RoomDatabase
import mezzari.torres.lucas.offlinefirst.database.dao.RepositoryDao
import mezzari.torres.lucas.offlinefirst.database.dao.UserDao
import mezzari.torres.lucas.offlinefirst.database.entities.RepositoryEntity
import mezzari.torres.lucas.offlinefirst.database.entities.UserEntity

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
@Database(version = 1, exportSchema = false, entities = [UserEntity::class, RepositoryEntity::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun getUserDao(): UserDao
    abstract fun getRepositoryDao(): RepositoryDao
}