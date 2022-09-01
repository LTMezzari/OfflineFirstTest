package mezzari.torres.lucas.offlinefirst.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mezzari.torres.lucas.offlinefirst.generic.BaseViewModel
import mezzari.torres.lucas.core.interfaces.IAppDispatcher
import mezzari.torres.lucas.core.model.Repository
import mezzari.torres.lucas.core.model.User
import mezzari.torres.lucas.network.service.IGithubService
import mezzari.torres.lucas.core.persistence.ISessionManager
import mezzari.torres.lucas.core.resource.Resource

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class RepositoriesViewModel(
    private val dispatcher: IAppDispatcher,
    private val service: IGithubService,
    private val session: ISessionManager,
) : BaseViewModel() {
    private val repositoriesResource: MutableLiveData<Resource<List<Repository>>> =
        MutableLiveData()
    val isLoading: LiveData<Boolean> = Transformations.map(repositoriesResource) {
        return@map it.status == Resource.Status.LOADING
    }
    val error: LiveData<String> = Transformations.map(repositoriesResource) {
        return@map if (it.status == Resource.Status.FAILURE) it.message else null
    }
    val repositories: LiveData<List<Repository>> = Transformations.map(repositoriesResource) {
        return@map it.data
    }

    private val user: User? get() = session.user

    fun getRepositories() {
        val userId = user?.username ?: return
        viewModelScope.launch(dispatcher.io) {
            service.getRepositories(userId).collect {
                repositoriesResource.postValue(it)
            }
        }
    }
}