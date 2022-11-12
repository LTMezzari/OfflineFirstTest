package mezzari.torres.lucas.user_repositories.synchronization

import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import mezzari.torres.lucas.android.persistence.preferences.IPreferencesManager
import mezzari.torres.lucas.android.synchronization.handler.SynchronizationHandler
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.user_repositories.service.IGithubService

/**
 * @author Lucas T. Mezzari
 * @since 05/09/2022
 */
class RepositoryHandler(
    private val service: IGithubService,
    private val preferences: IPreferencesManager
) : SynchronizationHandler {
    override suspend fun synchronize(): Boolean {
        val userId = preferences.user?.username ?: return true
        val deferred: CompletableDeferred<Boolean> = CompletableDeferred()
        Log.d(javaClass.simpleName, "Calling service")
        service.syncRepositories(userId).collect {
            Log.d(javaClass.simpleName, it.status.name)
            if (it.status == Resource.Status.LOADING)
                return@collect
            deferred.complete(it.status == Resource.Status.SUCCESS)
        }
        return deferred.await()
    }
}