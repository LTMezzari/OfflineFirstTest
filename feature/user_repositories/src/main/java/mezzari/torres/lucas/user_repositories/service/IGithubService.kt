package mezzari.torres.lucas.user_repositories.service

import kotlinx.coroutines.flow.Flow
import mezzari.torres.lucas.core.model.Repository
import mezzari.torres.lucas.core.model.User
import mezzari.torres.lucas.core.resource.Resource

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
interface IGithubService {
    fun getUser(userId: String): Flow<Resource<User>>
    
    fun getRepositories(userId: String): Flow<Resource<List<Repository>>>

    fun syncRepositories(userId: String): Flow<Resource<List<Repository>>>
}