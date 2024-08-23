package mezzari.torres.lucas.core.archive

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
fun CoroutineContext.launch(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return CoroutineScope(this).launch(this, start, block)
}

fun <T> Flow<T>.asDeferred(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    checkCompletion: ((T) -> Boolean)? = null
): Deferred<T?> {
    val completable = CompletableDeferred<T?>()
    var data: T? = null
    onCompletion {
        if (completable.isCompleted) {
            return@onCompletion
        }
        completable.complete(data)
    }

    CoroutineScope(context).launch(
        context,
        start
    ) {
        collect {
            data = it
            if (checkCompletion?.invoke(it) == true) {
                completable.complete(data)
            }
        }
    }

    return completable
}

suspend fun <T> Flow<T>.await(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
): T? {
    val completable = CompletableDeferred<T?>()
    var data: T? = null
    onCompletion {
        completable.complete(data)
    }
    collectLatest {
        data = it
    }
    return completable.await()
}