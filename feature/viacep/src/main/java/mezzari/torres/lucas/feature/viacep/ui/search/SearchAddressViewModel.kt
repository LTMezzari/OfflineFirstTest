package mezzari.torres.lucas.feature.viacep.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mezzari.torres.lucas.android.generic.BaseViewModel
import mezzari.torres.lucas.core.interfaces.AppDispatcher
import mezzari.torres.lucas.core.model.bo.Address
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.data.viacep.repository.AddressRepository

/**
 * @author Lucas T. Mezzari
 * @since 31/05/2023
 */
class SearchAddressViewModel(
    private val dispatcher: AppDispatcher,
    private val repository: AddressRepository
): BaseViewModel() {

    val cep: MutableLiveData<String> = MutableLiveData()

    private val addressResource: MutableLiveData<Resource<Address>> = MutableLiveData()
    val isLoading: LiveData<Boolean> = addressResource.map {
        return@map it?.status == Resource.Status.LOADING
    }
    val error: LiveData<String?> = addressResource.map {
        return@map it?.message
    }

    fun searchAddress(callback: ((Address?) -> Unit)? = null) {
        val cep = cep.value ?: return
        viewModelScope.launch(dispatcher.io) {
            repository.getAddress(cep).collect {
                addressResource.postValue(it)
                launch(dispatcher.main) {
                    if (it.status == Resource.Status.SUCCESS) {
                        callback?.invoke(it.data)
                    }
                }
            }
        }
    }
}