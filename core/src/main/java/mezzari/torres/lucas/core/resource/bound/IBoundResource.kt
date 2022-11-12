package mezzari.torres.lucas.core.resource.bound

/**
 * @author Lucas T. Mezzari
 * @since 11/11/2022
 */
interface IBoundResource<T> {
    suspend fun execute()
}