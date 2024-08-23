package mezzari.torres.lucas.core.archive

import mezzari.torres.lucas.core.resource.Resource

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
fun <T> Resource<T>.isLoading(): Boolean {
    return this.status == Resource.Status.LOADING
}