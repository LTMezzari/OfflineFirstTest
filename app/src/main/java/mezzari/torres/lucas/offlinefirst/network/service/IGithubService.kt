package mezzari.torres.lucas.offlinefirst.network.service

import kotlinx.coroutines.flow.Flow
import mezzari.torres.lucas.offlinefirst.model.Repository
import mezzari.torres.lucas.offlinefirst.model.User
import mezzari.torres.lucas.offlinefirst.network.wrapper.Resource

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
interface IGithubService {
    fun getUser(userId: String): Flow<Resource<User>>
    
    fun getRepositories(userId: String): Flow<Resource<List<Repository>>>
}