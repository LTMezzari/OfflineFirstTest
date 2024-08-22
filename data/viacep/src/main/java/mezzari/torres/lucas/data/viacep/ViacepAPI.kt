package mezzari.torres.lucas.data.viacep

import kotlinx.coroutines.Deferred
import mezzari.torres.lucas.core.model.bo.Address
import mezzari.torres.lucas.network.annotation.Route
import mezzari.torres.lucas.network.wrapper.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author Lucas T. Mezzari
 * @since 30/05/2023
 */
@Route("https://viacep.com.br/ws/")
interface ViacepAPI {
    @GET("{cep}/json")
    fun getAddress(
        @Path("cep") cep: String
    ): Deferred<Response<Address>>
}