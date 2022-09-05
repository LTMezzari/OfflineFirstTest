package mezzari.torres.lucas.android.syncronization.adapter

import mezzari.torres.lucas.android.persistence.preferences.IPreferencesManager
import mezzari.torres.lucas.network.service.IGithubService
import mezzari.torres.lucas.android.syncronization.handler.SynchronizationHandler
import mezzari.torres.lucas.android.syncronization.handler.repository.RepositoryHandler

/**
 * @author Lucas T. Mezzari
 * @since 05/09/2022
 */
class DataSynchronizationAdapter(private val service: IGithubService, private val preferences: IPreferencesManager): SynchronizationAdapter {
    override fun handlersCount(): Int {
        return 1
    }

    override fun buildHandler(index: Int): SynchronizationHandler {
        return when (index) {
            0 -> {
                RepositoryHandler(service, preferences)
            }
            else -> RepositoryHandler(service, preferences)
        }
    }
}