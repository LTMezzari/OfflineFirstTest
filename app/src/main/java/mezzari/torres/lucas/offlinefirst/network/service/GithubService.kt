package mezzari.torres.lucas.offlinefirst.network.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mezzari.torres.lucas.offlinefirst.database.repository.repositories.IRepositoriesRepository
import mezzari.torres.lucas.offlinefirst.database.repository.user.IUserRepository
import mezzari.torres.lucas.offlinefirst.model.Repository
import mezzari.torres.lucas.offlinefirst.model.User
import mezzari.torres.lucas.offlinefirst.network.IGithubAPI
import mezzari.torres.lucas.offlinefirst.network.bound.NetworkBoundResource
import mezzari.torres.lucas.offlinefirst.network.bound.strategies.AutomaticStrategy
import mezzari.torres.lucas.offlinefirst.network.bound.strategies.OfflineStrategy
import mezzari.torres.lucas.offlinefirst.network.bound.strategies.OnlineStrategy
import mezzari.torres.lucas.offlinefirst.network.wrapper.Resource

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class GithubService(
    private val api: IGithubAPI,
    private val userRepository: IUserRepository,
    private val repositoriesRepository: IRepositoriesRepository,
) : IGithubService {
    override fun getUser(userId: String): Flow<Resource<User>> {
        return flow {
            NetworkBoundResource(
                this,
                api.getUser(userId),
                object: AutomaticStrategy<User>() {
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

    override fun getRepositories(userId: String): Flow<Resource<List<Repository>>> {
        return flow {
            NetworkBoundResource(
                this,
                api.getUserRepositories(userId),
                object: OfflineStrategy<List<Repository>>() {
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