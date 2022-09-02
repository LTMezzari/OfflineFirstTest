package mezzari.torres.lucas.network.wrapper

import mezzari.torres.lucas.core.resource.Resource

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
class OfflineResource<T>(
    status: Status,
    data: T? = null,
    message: String? = null,
    val networkData: T? = null
) :
    Resource<T>(status, data, message) {
    companion object {
        fun <T> create(
            loaded: Resource<T>,
            networkError: String?,
            networkData: T?
        ): OfflineResource<T> {
            return OfflineResource(
                loaded.status,
                loaded.data,
                networkError,
                networkData
            )
        }
    }
}