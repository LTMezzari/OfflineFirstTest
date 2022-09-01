package mezzari.torres.lucas.network.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mezzari.torres.lucas.core.model.Repository
import mezzari.torres.lucas.core.model.User
import mezzari.torres.lucas.core.resource.bound.DataBoundResource
import mezzari.torres.lucas.database.repositories.repository.IRepositoriesRepository
import mezzari.torres.lucas.database.repositories.user.IUserRepository
import mezzari.torres.lucas.network.IGithubAPI
import mezzari.torres.lucas.network.strategies.AutomaticStrategy
import mezzari.torres.lucas.network.strategies.OfflineStrategy

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class GithubService(
    private val api: IGithubAPI,
    private val userRepository: IUserRepository,
    private val repositoriesRepository: IRepositoriesRepository,
) : IGithubService {
    override fun getUser(userId: String): Flow<mezzari.torres.lucas.core.resource.Resource<User>> {
        return flow {
            DataBoundResource(
                this,
                object : AutomaticStrategy<User>(api.getUser(userId)) {
                    override suspend fun onSaveData(data: User?) {
                        data?.also { user ->
                            userRepository.saveUser(user)
                        }
                    }

                    override suspend fun onLoadData(): User? {
                        return userRepository.getUser(userId)
                    }
                }
            )
        }
    }

    override fun getRepositories(userId: String): Flow<mezzari.torres.lucas.core.resource.Resource<List<Repository>>> {
        return flow {
            DataBoundResource(
                this,
                object : OfflineStrategy<List<Repository>>(api.getUserRepositories(userId)) {
                    override suspend fun onSaveData(data: List<Repository>?) {
                        data?.also { repositories ->
                            repositories.forEach {
                                it.userId = userId
                            }
                            repositoriesRepository.saveRepositories(repositories)
                        }
                    }

                    override suspend fun onLoadData(): List<Repository> {
                        return repositoriesRepository.getRepositories(userId)
                    }
                }
            )
        }
    }
}