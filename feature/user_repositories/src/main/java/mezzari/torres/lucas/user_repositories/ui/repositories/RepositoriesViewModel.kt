package mezzari.torres.lucas.user_repositories.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mezzari.torres.lucas.android.persistence.session.SessionManager
import mezzari.torres.lucas.commons.generic.BaseViewModel
import mezzari.torres.lucas.core.archive.elvis
import mezzari.torres.lucas.core.archive.guard
import mezzari.torres.lucas.core.interfaces.AppDispatcher
import mezzari.torres.lucas.core.model.Repository
import mezzari.torres.lucas.core.model.User
import mezzari.torres.lucas.user_repositories.repository.GithubRepository
import mezzari.torres.lucas.core.resource.OutdatedResource
import mezzari.torres.lucas.core.resource.Resource

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
    val isLoading: LiveData<Boolean> = Transformations.map(repositoriesResource) {
        return@map it.status == Resource.Status.LOADING
    }
    val error: LiveData<String> = Transformations.map(repositoriesResource) {
        return@map if (it.status == Resource.Status.FAILURE) it.message else null
    }
    private val repositories: LiveData<List<Repository>> = Transformations.map(repositoriesResource) {
        return@map it.data
    }
    val isOutdated: LiveData<Boolean> = Transformations.map(repositoriesResource) {
        hasNewData = it is OutdatedResource
        return@map it is OutdatedResource
    }

    val paginatedList: MediatorLiveData<ObservableList<Repository>> = MediatorLiveData<ObservableList<Repository>>().apply {
        addSource(repositories) {
            if (it == null) {
                shouldLoadMore.value = false
                isLoadingMore.value = false
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