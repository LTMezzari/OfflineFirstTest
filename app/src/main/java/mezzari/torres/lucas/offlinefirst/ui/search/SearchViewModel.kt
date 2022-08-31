package mezzari.torres.lucas.offlinefirst.ui.search

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import mezzari.torres.lucas.offlinefirst.generic.BaseViewModel
import mezzari.torres.lucas.offlinefirst.interfaces.IAppDispatcher
import mezzari.torres.lucas.offlinefirst.model.User
import mezzari.torres.lucas.offlinefirst.network.service.IGithubService
import mezzari.torres.lucas.offlinefirst.network.wrapper.Resource
import mezzari.torres.lucas.offlinefirst.persistence.ISessionManager

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class SearchViewModel(
    private val dispatcher: IAppDispatcher,
    private val service: IGithubService,
    private val session: ISessionManager,
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
        val userId = search.value ?: return
        viewModelScope.launch(dispatcher.io) {
            service.getUser(userId).collect {
                searchResource.postValue(it)
                if (it.status != Resource.Status.LOADING) {
                    launch(dispatcher.main) {
                        callback(it.data)
                        session.user = it.data
                    }
                }
            }
        }
    }
}