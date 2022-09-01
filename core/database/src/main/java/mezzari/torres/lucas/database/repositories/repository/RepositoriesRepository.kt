package mezzari.torres.lucas.database.repositories.repository

import mezzari.torres.lucas.core.model.Repository
import mezzari.torres.lucas.database.dao.RepositoryDao
import mezzari.torres.lucas.database.entities.RepositoryEntity
import mezzari.torres.lucas.database.entities.asEntity
import mezzari.torres.lucas.database.entities.asEntry

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