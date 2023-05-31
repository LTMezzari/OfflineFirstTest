package mezzari.torres.lucas.viacep.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mezzari.torres.lucas.core.model.bo.Address
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.core.resource.bound.DataBoundResource
import mezzari.torres.lucas.network.strategies.OnlineStrategy
import mezzari.torres.lucas.viacep.ViacepAPI

/**
 * @author Lucas T. Mezzari
 * @since 30/05/2023
 */
class AddressRepositoryImpl(private val api: ViacepAPI) : AddressRepository {
    override fun getAddress(cep: String): Flow<Resource<Address>> {
        return flow {
            DataBoundResource(this, OnlineStrategy(api.getAddress(cep)))
        }
    }
}