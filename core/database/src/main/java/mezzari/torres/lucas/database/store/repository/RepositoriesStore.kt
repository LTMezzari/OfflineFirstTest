package mezzari.torres.lucas.database.store.repository

import mezzari.torres.lucas.core.model.Repository

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
interface RepositoriesStore {
    suspend fun getRepositories(userId: String): List<Repository>
    suspend fun saveRepositories(repositories: List<Repository>)
}