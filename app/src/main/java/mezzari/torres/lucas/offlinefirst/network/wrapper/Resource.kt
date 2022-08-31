package mezzari.torres.lucas.offlinefirst.network.wrapper

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
open class Resource<T>(val status: Status, val data: T? = null, val message: String? = null) {

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data)
        }

        fun <T> error(message: String?, data: T?): Resource<T> {
            return Resource(Status.FAILURE, data, message)
        }

        fun <T> loading(): Resource<T> {
            return Resource(Status.LOADING)
        }
    }

    enum class Status {
        LOADING,
        SUCCESS,
        FAILURE
    }
}