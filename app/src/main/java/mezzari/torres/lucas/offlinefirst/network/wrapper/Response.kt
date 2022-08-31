package mezzari.torres.lucas.offlinefirst.network.wrapper

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
sealed class Response<T> {
    companion object {
        fun <T> create(throwable: Throwable): Response<T> {
            return Failure(throwable.message)
        }

        fun <T> create(response: retrofit2.Response<T>): Response<T> {
            val body = response.body()
            if (response.isSuccessful) {
                return Success(body)
            }
            val message = response.message()
            return Failure(message, body)
        }
    }

    data class Success<T>(val data: T?) : Response<T>()
    data class Failure<T>(val error: String?, val data: T? = null) : Response<T>()
}