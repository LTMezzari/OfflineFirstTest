package mezzari.torres.lucas.user_repositories

import kotlinx.coroutines.Deferred
import mezzari.torres.lucas.network.annotation.Route
import mezzari.torres.lucas.core.model.bo.Repository
import mezzari.torres.lucas.core.model.bo.User
import mezzari.torres.lucas.network.wrapper.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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
        @Path("userId") userId: String,
        @Query("page") page: Int,
        @Query("per_page") size: Int
    ): Deferred<Response<List<Repository>>>

    @GET("users/{userId}/repos")
    fun getUserRepositories(
        @Path("userId") userId: String
    ): Deferred<Response<List<Repository>>>
}