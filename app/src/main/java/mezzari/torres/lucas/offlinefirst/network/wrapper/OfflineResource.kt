package mezzari.torres.lucas.offlinefirst.network.wrapper

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
class OfflineResource<T>(
    status: Status,
    data: T? = null,
    message: String? = null,
    val newData: T? = null
) :
    Resource<T>(status, data, message) {
    companion object {
        fun <T> create(loaded: Resource<T>, networkError: String?, networkData: T?): OutdatedResource<T> {
            return OutdatedResource(
                loaded.status,
                loaded.data,
                networkError,
                networkData
            )
        }
    }
}