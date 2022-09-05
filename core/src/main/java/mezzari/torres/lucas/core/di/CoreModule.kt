package mezzari.torres.lucas.core.di

import kotlinx.coroutines.Dispatchers
import mezzari.torres.lucas.core.interfaces.IAppDispatcher
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

/**
 * @author Lucas T. Mezzari
 * @since 01/09/2022
 */
val coreModule = module {
    single<IAppDispatcher> {
        object : IAppDispatcher {
            override var main: CoroutineContext = Dispatchers.Main
            override var io: CoroutineContext = Dispatchers.IO
        }
    }
}