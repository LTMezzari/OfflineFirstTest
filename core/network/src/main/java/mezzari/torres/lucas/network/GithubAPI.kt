package mezzari.torres.lucas.network

import kotlinx.coroutines.Deferred
import mezzari.torres.lucas.network.annotation.Route
import mezzari.torres.lucas.core.model.Repository
import mezzari.torres.lucas.core.model.User
import mezzari.torres.lucas.network.wrapper.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
@Route("https://api.github.com/")
interface GithubAPI {
    @GET("users/{userId}")
    fun getUser(
        @Path("userId") userId: String
    ): Deferred<Response<User>>

    @GET("users/{userId}/repos")
    fun getUserRepositories(
        @Path("userId") userId: String
    ): Deferred<Response<List<Repository>>>
}