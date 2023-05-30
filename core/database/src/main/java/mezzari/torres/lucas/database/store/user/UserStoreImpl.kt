package mezzari.torres.lucas.database.store.user

import mezzari.torres.lucas.core.model.bo.User
import mezzari.torres.lucas.database.dao.UserDao
import mezzari.torres.lucas.database.entities.asEntity
import mezzari.torres.lucas.database.entities.asEntry

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
class UserStoreImpl(
    private val dao: UserDao
) : UserStore {
    override suspend fun getUser(userId: String): User? {
        val users = dao.getUser(userId) ?: return null
        if (users.isEmpty())
            return null
        return users.first().asEntry()
    }

    override suspend fun saveUser(user: User) {
        val entity = user.asEntity() ?: return
        dao.addUser(entity)
    }
}