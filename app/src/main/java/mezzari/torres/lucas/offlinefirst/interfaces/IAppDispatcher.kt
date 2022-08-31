package mezzari.torres.lucas.offlinefirst.interfaces

import kotlin.coroutines.CoroutineContext

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
interface IAppDispatcher {
    var main: CoroutineContext
    var io: CoroutineContext
}