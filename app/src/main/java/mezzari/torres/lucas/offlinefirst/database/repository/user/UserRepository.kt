package mezzari.torres.lucas.offlinefirst.database.repository.user

import mezzari.torres.lucas.offlinefirst.database.dao.UserDao
import mezzari.torres.lucas.offlinefirst.database.entities.asEntity
import mezzari.torres.lucas.offlinefirst.database.entities.asEntry
import mezzari.torres.lucas.offlinefirst.model.User

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