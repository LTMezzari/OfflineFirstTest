package mezzari.torres.lucas.network.strategies

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mezzari.torres.lucas.core.resource.Resource
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import retrofit2.Response

internal typealias MyResult<T> = mezzari.torres.lucas.network.wrapper.Response<T>

/**
 * @author Lucas T. Mezzari
 * @since 01/06/2023
 */
class OnlineStrategyTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = StandardTestDispatcher()

    private lateinit var sub: OnlineStrategy<Any>
    private lateinit var collector: FlowCollector<Resource<Any>>
    private lateinit var onLoad: () -> MyResult<Any>
    private var onEmit: ((Resource<Any>) -> Unit)? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        collector = FlowCollector { value -> onEmit?.invoke(value) }
        sub = OnlineStrategy({ CompletableDeferred(onLoad()) })
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Execute Strategy Successfully Should Load And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val response = 2
            val finalReturn: MyResult<Any> = MyResult.create(Response.success(response))

            onLoad = {
                finalReturn
            }

            onEmit = {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(response, it.data)
                    }
                    else -> assertTrue(false)
                }

            }

            sub.execute(collector)
            assertEquals(2, counter)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Execute Strategy With Response Error Should Load And Return A Resource With Error Status`() =
        runTest {
            var counter = 0
            val message = "Error here"
            val body = "{\"error\": \"$message\"}".toResponseBody(null)
            val finalReturn: MyResult<Any> = MyResult.create(Response.error(500, body))

            onLoad = {
                finalReturn
            }

            onEmit = {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.FAILURE, it.status)
//                        assertEquals(message, it.message)
                        assertNull(it.data)
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
            val message = "Error here"

            onLoad = {
                throw Exception(message)
            }

            onEmit = {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.FAILURE, it.status)
                        assertEquals(message, it.message)
                    }
                    else -> assertTrue(false)
                }

            }

            sub.execute(collector)
            assertEquals(2, counter)
        }
}