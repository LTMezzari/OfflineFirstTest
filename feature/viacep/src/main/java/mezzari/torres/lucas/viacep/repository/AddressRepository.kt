package mezzari.torres.lucas.viacep.repository

import kotlinx.coroutines.flow.Flow
import mezzari.torres.lucas.core.model.bo.Address
import mezzari.torres.lucas.core.resource.Resource

/**
 * @author Lucas T. Mezzari
 * @since 30/05/2023
 */
interface AddressRepository {
    fun getAddress(cep: String): Flow<Resource<Address>>
}