package mezzari.torres.lucas.core.interfaces

import kotlin.coroutines.CoroutineContext

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
interface AppDispatcher {
    var main: CoroutineContext
    var io: CoroutineContext
}