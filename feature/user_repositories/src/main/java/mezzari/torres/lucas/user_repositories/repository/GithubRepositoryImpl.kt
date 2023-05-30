package mezzari.torres.lucas.user_repositories.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mezzari.torres.lucas.core.model.bo.Repository
import mezzari.torres.lucas.core.model.bo.User
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.core.resource.bound.DataBoundResource
import mezzari.torres.lucas.database.store.cache.CacheStore
import mezzari.torres.lucas.database.store.repository.RepositoriesStore
import mezzari.torres.lucas.database.store.user.UserStore
import mezzari.torres.lucas.user_repositories.GithubAPI
import mezzari.torres.lucas.network.strategies.CacheStrategy
import mezzari.torres.lucas.network.strategies.OnlineStrategy

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class GithubRepositoryImpl(
    private val api: GithubAPI,
    private val userRepository: UserStore,
    private val repositoriesRepository: RepositoriesStore,
    private val cacheRepository: CacheStore,
) : GithubRepository {
    override fun getUser(userId: String): Flow<Resource<User>> {
        return flow {
            DataBoundResource(
                this,
                CacheStrategy(
                    "${this@GithubRepositoryImpl::class.java.simpleName}:getUser:$userId",
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
                    "${this@GithubRepositoryImpl::class.java.simpleName}:getRepositories:$userId,$page",
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