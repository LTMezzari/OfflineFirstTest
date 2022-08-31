package mezzari.torres.lucas.offlinefirst.database.repository.user

import mezzari.torres.lucas.offlinefirst.model.User

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
interface IUserRepository {
    suspend fun getUser(userId: String): User?
    suspend fun saveUser(user: User)
}