package mezzari.torres.lucas.android.syncronization.adapter

import mezzari.torres.lucas.android.syncronization.handler.SynchronizationHandler

/**
 * @author Lucas T. Mezzari
 * @since 05/09/2022
 */
interface SynchronizationAdapter {
    fun handlersCount(): Int

    fun buildHandler(index: Int): SynchronizationHandler
}