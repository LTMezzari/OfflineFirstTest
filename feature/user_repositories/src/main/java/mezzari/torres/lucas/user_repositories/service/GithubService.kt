package mezzari.torres.lucas.user_repositories.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mezzari.torres.lucas.core.model.bo.Repository
import mezzari.torres.lucas.core.model.bo.User
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.core.resource.bound.DataBoundResource
import mezzari.torres.lucas.database.repositories.cache.ICacheRepository
import mezzari.torres.lucas.database.repositories.repository.IRepositoriesRepository
import mezzari.torres.lucas.database.repositories.user.IUserRepository
import mezzari.torres.lucas.network.IGithubAPI
import mezzari.torres.lucas.network.strategies.CacheStrategy
import mezzari.torres.lucas.network.strategies.OnlineStrategy

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class GithubService(
    private val api: IGithubAPI,
    private val userRepository: IUserRepository,
    private val repositoriesRepository: IRepositoriesRepository,
    private val cacheRepository: ICacheRepository,
) : IGithubService {
    override fun getUser(userId: String): Flow<Resource<User>> {
        return flow {
            DataBoundResource(
                this,
                CacheStrategy(
                    "${this@GithubService::class.java.simpleName}:getUser:$userId",
                    cacheRepository,
                    api.getUser(userId),
                    singleEmit = true
                )
            )
        }
    }

    override fun getRepositories(userId: String, page: Int): Flow<Resource<List<Repository>>> {
        return flow {
            DataBoundResource(
                this,
                CacheStrategy<List<Repository>>(
                    "${this@GithubService::class.java.simpleName}:getRepositories:$userId,$page",
                    cacheRepository,
                    api.getUserRepositories(userId, page, 10),
                    strict = true,
                    singleEmit = true
                )
            )
        }
    }

    override fun syncRepositories(userId: String): Flow<Resource<List<Repository>>> {
        return flow {
            DataBoundResource(
                this,
                OnlineStrategy(api.getUserRepositories(userId)) {
                    val repositories = it ?: return@OnlineStrategy
                    repositoriesRepository.saveRepositories(repositories)
                }
            )
        }
    }
}