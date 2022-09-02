package mezzari.torres.lucas.database.repositories.user

import mezzari.torres.lucas.core.model.User
import mezzari.torres.lucas.database.dao.UserDao
import mezzari.torres.lucas.database.entities.asEntity
import mezzari.torres.lucas.database.entities.asEntry

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
class UserRepository(
    private val dao: UserDao
) : IUserRepository {
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