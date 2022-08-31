package mezzari.torres.lucas.offlinefirst.database.repository.repositories

import mezzari.torres.lucas.offlinefirst.database.dao.RepositoryDao
import mezzari.torres.lucas.offlinefirst.database.entities.RepositoryEntity
import mezzari.torres.lucas.offlinefirst.database.entities.asEntity
import mezzari.torres.lucas.offlinefirst.database.entities.asEntry
import mezzari.torres.lucas.offlinefirst.model.Repository

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
class RepositoriesRepository(
    private val dao: RepositoryDao
): IRepositoriesRepository {
    override suspend fun getRepositories(userId: String): List<Repository> {
        return dao.getRepositories(userId)?.map {
            it.asEntry()
        } ?: arrayListOf()
    }

    override suspend fun saveRepositories(repositories: List<Repository>) {
        val entities = arrayListOf<RepositoryEntity>()
        for (repository in repositories) {
            val entity = repository.asEntity() ?: continue
            entities.add(entity)
        }
        dao.addRepositories(entities)
    }
}