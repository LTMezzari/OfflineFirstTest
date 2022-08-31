package mezzari.torres.lucas.offlinefirst.network.wrapper

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
class OutdatedResource<T>(
    status: Status,
    data: T? = null,
    message: String? = null,
    val newData: T? = null
) :
    Resource<T>(status, data, message) {
    companion object {
        fun <T> success(oldResource: Resource<T>, newData: T?): OutdatedResource<T> {
            return OutdatedResource(
                Status.SUCCESS,
                oldResource.data,
                oldResource.message,
                newData
            )
        }
    }
}