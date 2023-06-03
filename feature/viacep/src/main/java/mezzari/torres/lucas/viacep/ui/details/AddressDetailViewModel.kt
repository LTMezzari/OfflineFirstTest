package mezzari.torres.lucas.viacep.ui.details

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import mezzari.torres.lucas.android.generic.BaseViewModel
import mezzari.torres.lucas.core.model.bo.Address

/**
 * @author Lucas T. Mezzari
 * @since 31/05/2023
 */
class AddressDetailViewModel: BaseViewModel() {
    val address: MutableLiveData<Address> = MutableLiveData()

    val zipCode: MediatorLiveData<String> = MediatorLiveData<String>().apply {
        addSource(address) {
            postValue(it?.cep)
        }
    }
    val street: MediatorLiveData<String> = MediatorLiveData<String>().apply {
        addSource(address) {
            postValue(it?.street)
        }
    }
    val neighborhood: MediatorLiveData<String> = MediatorLiveData<String>().apply {
        addSource(address) {
            postValue(it?.neighborhood)
        }
    }
    val city: MediatorLiveData<String> = MediatorLiveData<String>().apply {
        addSource(address) {
            postValue(it?.locality)
        }
    }
    val state: MediatorLiveData<String> = MediatorLiveData<String>().apply {
        addSource(address) {
            postValue(it?.state)
        }
    }
}