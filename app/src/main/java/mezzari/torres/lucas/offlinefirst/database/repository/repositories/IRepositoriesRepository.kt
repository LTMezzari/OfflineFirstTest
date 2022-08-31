package mezzari.torres.lucas.offlinefirst.database.repository.repositories

import mezzari.torres.lucas.offlinefirst.model.Repository

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
interface IRepositoriesRepository {
    suspend fun getRepositories(userId: String): List<Repository>
    suspend fun saveRepositories(repositories: List<Repository>)
}