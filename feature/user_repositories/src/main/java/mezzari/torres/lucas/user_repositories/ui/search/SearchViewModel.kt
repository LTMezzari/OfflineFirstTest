package mezzari.torres.lucas.user_repositories.ui.search

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import mezzari.torres.lucas.android.persistence.preferences.IPreferencesManager
import mezzari.torres.lucas.android.persistence.session.ISessionManager
import mezzari.torres.lucas.commons.generic.BaseViewModel
import mezzari.torres.lucas.core.archive.elvis
import mezzari.torres.lucas.core.archive.guard
import mezzari.torres.lucas.core.interfaces.IAppDispatcher
import mezzari.torres.lucas.core.model.User
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.user_repositories.service.IGithubService

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class SearchViewModel(
    private val dispatcher: IAppDispatcher,
    private val service: IGithubService,
    private val session: ISessionManager,
    private val preferences: IPreferencesManager,
) : BaseViewModel() {
    val search: MutableLiveData<String> = MutableLiveData()

    private val searchResource: MutableLiveData<Resource<User>> = MutableLiveData()
    val isLoading: LiveData<Boolean> = Transformations.map(searchResource) {
        return@map it.status == Resource.Status.LOADING
    }
    val error: LiveData<String> = Transformations.map(searchResource) {
        return@map if (it.status != Resource.Status.FAILURE) null else it.message
    }

    private val _isValid: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        postValue(false)
        addSource(search) {
            postValue(
                isLoading.value != true &&
                        it != null &&
                        !it.isNullOrBlank() &&
                        !it.isNullOrEmpty()
            )
        }

        addSource(isLoading) {
            postValue(
                it != true &&
                        search.value != null &&
                        !search.value.isNullOrBlank() &&
                        !search.value.isNullOrEmpty()
            )
        }
    }
    val isValid: LiveData<Boolean> get() = _isValid

    fun getUser(callback: (User?) -> Unit) {
        val (userId) = guard(search.value) elvis {
            return
        }
        viewModelScope.launch(dispatcher.io) {
            service.getUser(userId.toString()).collect {
                searchResource.postValue(it)
                if (it.status != Resource.Status.LOADING) {
                    launch(dispatcher.main) {
                        callback(it.data)
                        session.user = it.data
                        preferences.user = it.data
                    }
                }
            }
        }
    }
}