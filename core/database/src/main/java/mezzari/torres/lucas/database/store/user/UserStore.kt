package mezzari.torres.lucas.database.store.user

import mezzari.torres.lucas.core.model.User

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
interface UserStore {
    suspend fun getUser(userId: String): User?
    suspend fun saveUser(user: User)
}