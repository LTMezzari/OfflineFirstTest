package mezzari.torres.lucas.network.wrapper

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
    mezzari.torres.lucas.core.resource.Resource<T>(status, data, message) {
    companion object {
        fun <T> create(loaded: mezzari.torres.lucas.core.resource.Resource<T>, networkError: String?, networkData: T?): mezzari.torres.lucas.core.resource.OutdatedResource<T> {
            return mezzari.torres.lucas.core.resource.OutdatedResource(
                loaded.status,
                loaded.data,
                networkError,
                networkData
            )
        }
    }
}