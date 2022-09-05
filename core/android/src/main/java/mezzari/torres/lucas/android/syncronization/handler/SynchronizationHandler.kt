package mezzari.torres.lucas.android.syncronization.handler

/**
 * @author Lucas T. Mezzari
 * @since 05/09/2022
 */
interface SynchronizationHandler {
    suspend fun synchronize(): Boolean
}