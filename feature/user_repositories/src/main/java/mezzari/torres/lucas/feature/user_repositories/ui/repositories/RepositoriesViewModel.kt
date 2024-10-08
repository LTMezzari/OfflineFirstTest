package mezzari.torres.lucas.feature.user_repositories.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mezzari.torres.lucas.android.generic.BaseViewModel
import mezzari.torres.lucas.android.persistence.session.SessionManager
import mezzari.torres.lucas.core.interfaces.AppDispatcher
import mezzari.torres.lucas.core.model.ObservableList
import mezzari.torres.lucas.core.model.bo.Repository
import mezzari.torres.lucas.core.model.bo.User
import mezzari.torres.lucas.core.resource.OutdatedResource
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.data.user_repositories.repository.GithubRepository

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class RepositoriesViewModel(
    private val dispatcher: AppDispatcher,
    private val service: GithubRepository,
    private val session: SessionManager,
) : BaseViewModel() {
    private val user: User? get() = session.user
    private var hasNewData: Boolean = false

    val page: MutableLiveData<Int> = MutableLiveData(0)
    val isLoadingMore: MutableLiveData<Boolean> = MutableLiveData(false)
    val shouldLoadMore: MutableLiveData<Boolean> = MutableLiveData(false)

    private val repositoriesResource: MutableLiveData<Resource<List<Repository>>> =
        MutableLiveData()
    val isLoading: LiveData<Boolean> = repositoriesResource.map {
        return@map it.status == Resource.Status.LOADING
    }
    val error: LiveData<String?> = repositoriesResource.map {
        return@map if (it.status == Resource.Status.FAILURE) {
            isLoadingMore.postValue(false)
            it.message
        } else {
            null
        }
    }
    private val repositories: LiveData<List<Repository>> = repositoriesResource.map {
        return@map it.data ?: emptyList()
    }
    val isOutdated: LiveData<Boolean> = repositoriesResource.map {
        hasNewData = it is OutdatedResource
        return@map it is OutdatedResource
    }

    val paginatedList: MediatorLiveData<ObservableList<Repository>> = MediatorLiveData<ObservableList<Repository>>().apply {
        addSource(repositories) {
            if (it == null) {
                shouldLoadMore.value = false
                return@addSource
            }
            val list = value ?: return@addSource
            val isLoadingMore = isLoadingMore.value ?: false
            shouldLoadMore.value = it.size >= 10
            if (isLoadingMore) {
                list.addAll(it)
                this@RepositoriesViewModel.isLoadingMore.postValue(false)
                return@addSource
            }
            list.clear()
            list.addAll(it)
        }
        postValue(ObservableList())
    }

    fun getRepositories(page: Int) {
        this@RepositoriesViewModel.page.postValue(page)
        val userId = user?.username ?: return
        viewModelScope.launch(dispatcher.io) {
            service.getRepositories(userId, page).collect {
                repositoriesResource.postValue(it)
            }
        }
    }
}