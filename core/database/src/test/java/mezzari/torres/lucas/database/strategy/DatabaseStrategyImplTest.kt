package mezzari.torres.lucas.database.strategy

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mezzari.torres.lucas.core.resource.Resource
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

/**
 * @author Lucas T. Mezzari
 * @since 01/06/2023
 */
internal class DatabaseStrategyImplTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var sub: DatabaseStrategy<Any>
    private lateinit var collector: FlowCollector<Resource<Any>>
    private var onEmit: ((Resource<Any>) -> Unit)? = null
    private var onLoad: (() -> Any)? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        onLoad = null
        collector = FlowCollector { value -> onEmit?.invoke(value) }
        sub = object: DatabaseStrategy<Any>() {
            override suspend fun onLoadData(): Any? {
                return onLoad?.invoke()
            }
        }
    }

    @Test
    fun `Execute Strategy Successfully Should Load And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val finalReturn = 2

            onLoad = {
                finalReturn
            }

            onEmit = {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(finalReturn, it.data)
                    }
                    else -> assertTrue(false)
                }

            }

            sub.execute(collector)
            assertEquals(2, counter)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Execute Strategy With Exception Should Load And Return A Resource With Error Status`() =
        runTest {
            var counter = 0
            val error = "Error here"

            onLoad = {
                throw Exception(error)
            }

            onEmit = {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.FAILURE, it.status)
                        assertEquals(error, it.message)
                    }
                    else -> assertTrue(false)
                }

            }

            sub.execute(collector)
            assertEquals(2, counter)
        }
}