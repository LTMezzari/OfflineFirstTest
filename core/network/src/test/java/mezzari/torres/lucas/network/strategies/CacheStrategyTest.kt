package mezzari.torres.lucas.network.strategies

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mezzari.torres.lucas.core.model.bo.Cache
import mezzari.torres.lucas.core.resource.OutdatedResource
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.database.store.cache.CacheStore
import mezzari.torres.lucas.network.wrapper.OfflineResource
import okhttp3.ResponseBody.Companion.toResponseBody
import java.lang.reflect.Type
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * @author Lucas T. Mezzari
 * @since 01/06/2023
 */
internal class CacheStrategyTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var store: CacheStore
    private var onGetCache: ((String) -> Cache?)? = null
    private var onPutCache: ((List<Cache?>) -> Boolean)? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        onGetCache = null
        onPutCache = null
        store = object : CacheStore {
            override suspend fun getCache(cacheId: String): Cache? {
                return onGetCache?.invoke(cacheId)
            }

            override suspend fun saveCache(vararg caches: Cache?): Boolean {
                return onPutCache?.invoke(caches.asList()) ?: true
            }
        }
    }

    private fun <T : Any> createCollector(onEmit: (Resource<T>) -> Unit): FlowCollector<Resource<T>> {
        return FlowCollector { value -> onEmit.invoke(value) }
    }

    private fun <R: Any, T : Any> createSub(
        callId: String,
        onLoad: () -> MyResult<R>,
        type: Type,
        onTransform: ((R?) -> T?)? = null,
        isStrict: Boolean = true,
        isSingleEmit: Boolean = false
    ): CacheStrategy<R, T> {
        return CacheStrategy(
            callId = callId,
            store = store,
            call = { CompletableDeferred(onLoad()) },
            onTransform = onTransform,
            strict = isStrict,
            singleEmit = isSingleEmit,
            type = type
        )
    }

    private inline fun <reified R: Any, reified T : Any> createSub(
        callId: String,
        noinline onLoad: () -> MyResult<R>,
        noinline onTransform: ((R?) -> T?)? = null,
        isStrict: Boolean = true,
        isSingleEmit: Boolean = false
    ): CacheStrategy<R, T> {
        return CacheStrategy(
            callId = callId,
            store = store,
            call = { CompletableDeferred(onLoad()) },
            onTransform = onTransform,
            strict = isStrict,
            singleEmit = isSingleEmit,
            type = object : TypeToken<R>() {}.type
        )
    }

    internal class WrapperSubject<T: ParsableObject>(val data: T): ParsableObject {
        override fun toJson(): String {
            return "{\"data\":${data.toJson()}}"
        }
    }

    internal class TestSubject(val intValue: Int, val stringValue: String = ""): ParsableObject {
        override fun toJson(): String {
            return "{\"intValue\":$intValue,\"stringValue\":\"$stringValue\"}"
        }
    }

    internal data class OtherTestSubject(val intValue: Int, val stringValue: String = ""): ParsableObject {
        override fun toJson(): String {
            return "{\"intValue\":$intValue,\"stringValue\":\"$stringValue\"}"
        }
    }

    internal interface ParsableObject {
        fun toJson(): String
    }

    // ---------------- Load, Parse, Fetch, Transform, Save

    @Test
    fun `Execute Strategy For TestSubject Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = TestSubject(1, "Test")
            val loadedCache = Cache(callId, loadedBody.toJson())

            val fetchedBody = TestSubject(2)
            val response: MyResult<TestSubject> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<TestSubject, TestSubject> = createSub(callId, {
                response
            })

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedBody.toJson())
                true
            }

            val collector: FlowCollector<Resource<TestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertNotEquals(loadedBody, it.data)
                        assertEquals(loadedBody.intValue, it.data?.intValue)
                        assertEquals(loadedBody.stringValue, it.data?.stringValue)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute Strategy For Wrapped TestSubject Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = TestSubject(1, "Test")
            val wrappedBody = WrapperSubject(loadedBody)
            val loadedCache = Cache(callId, wrappedBody.toJson())

            val fetchedResult = TestSubject(2)
            val fetchedBody = WrapperSubject(fetchedResult)
            val response: MyResult<WrapperSubject<TestSubject>> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<WrapperSubject<TestSubject>, TestSubject> = createSub(callId, {
                response
            }, onTransform = {
                it?.data
            })

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedBody.toJson())
                true
            }

            val collector: FlowCollector<Resource<TestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertNotEquals(loadedBody, it.data)
                        assertEquals(loadedBody.intValue, it.data?.intValue)
                        assertEquals(loadedBody.stringValue, it.data?.stringValue)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedResult, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute Strategy Passing Type For TestSubject Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = TestSubject(1, "Test")
            val loadedCache = Cache(callId, loadedBody.toJson())

            val fetchedBody = TestSubject(2)
            val response: MyResult<TestSubject> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<TestSubject, TestSubject> = createSub(callId, {
                response
            }, type = TestSubject::class.java)

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedBody.toJson())
                true
            }

            val collector: FlowCollector<Resource<TestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertNotEquals(loadedBody, it.data)
                        assertEquals(loadedBody.intValue, it.data?.intValue)
                        assertEquals(loadedBody.stringValue, it.data?.stringValue)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute Strategy Passing Type For Wrapped TestSubject Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = TestSubject(1, "Test")
            val wrappedBody = WrapperSubject(loadedBody)
            val loadedCache = Cache(callId, wrappedBody.toJson())

            val fetchedResult = TestSubject(2)
            val fetchedBody = WrapperSubject(fetchedResult)
            val response: MyResult<WrapperSubject<TestSubject>> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<WrapperSubject<TestSubject>, TestSubject> = createSub(callId, {
                response
            }, onTransform = {
                it?.data
            }, type = object : TypeToken<WrapperSubject<TestSubject>>() {}.type)

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedBody.toJson())
                true
            }

            val collector: FlowCollector<Resource<TestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertNotEquals(loadedBody, it.data)
                        assertEquals(loadedBody.intValue, it.data?.intValue)
                        assertEquals(loadedBody.stringValue, it.data?.stringValue)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedResult, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute Strategy For OtherTestSubject Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = OtherTestSubject(1, "Test")
            val loadedCache = Cache(callId, loadedBody.toJson())

            val fetchedBody = OtherTestSubject(2)
            val response: MyResult<OtherTestSubject> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<OtherTestSubject, OtherTestSubject> = createSub(callId, {
                response
            })

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedBody.toJson())
                true
            }

            val collector: FlowCollector<Resource<OtherTestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loadedBody, it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute Strategy Passing Type For OtherTestSubject Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = OtherTestSubject(1, "Test")
            val loadedCache = Cache(callId, loadedBody.toJson())

            val fetchedBody = OtherTestSubject(2)
            val response: MyResult<OtherTestSubject> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<OtherTestSubject, OtherTestSubject> = createSub(callId, {
                response
            }, OtherTestSubject::class.java)

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedBody.toJson())
                true
            }

            val collector: FlowCollector<Resource<OtherTestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loadedBody, it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute Strategy For HashMap Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = hashMapOf<String, Int>("one" to 1, "two" to 2)
            val loadedCache = Cache(callId, "{\"one\":1,\"two\":2}")

            val fetchedBody = hashMapOf<String, Int>("three" to 3)
            val response: MyResult<HashMap<String, Int>> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<HashMap<String, Int>, HashMap<String, Int>> = createSub(callId, {
                response
            })

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals("{\"three\":3}", it[0]?.response)
                true
            }

            val collector: FlowCollector<Resource<HashMap<String, Int>>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loadedBody, it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute Strategy For OtherTestSubject Successfully With No Cache Should Load, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"

            val fetchedBody = OtherTestSubject(2)
            val response: MyResult<OtherTestSubject> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<OtherTestSubject, OtherTestSubject> = createSub(callId, {
                response
            })

            onGetCache = {
                assertEquals(callId, it)
                null
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedBody.toJson())
                true
            }

            val collector: FlowCollector<Resource<OtherTestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertNull(it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute Strategy For OtherTestSubject Successfully With Empty Cache Response Should Load, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedCache = Cache(callId, "")

            val fetchedBody = OtherTestSubject(2)
            val response: MyResult<OtherTestSubject> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<OtherTestSubject, OtherTestSubject> = createSub(callId, {
                response
            })

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedBody.toJson())
                true
            }

            val collector: FlowCollector<Resource<OtherTestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertNull(it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute Strategy For OtherTestSubject Successfully With Blank Cache Response Should Load, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedCache = Cache(callId, "         ")

            val fetchedBody = OtherTestSubject(2)
            val response: MyResult<OtherTestSubject> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<OtherTestSubject, OtherTestSubject> = createSub(callId, {
                response
            })

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedBody.toJson())
                true
            }

            val collector: FlowCollector<Resource<OtherTestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertNull(it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    // ---------------- Parse, Transform Primitives

    @Test
    fun `Execute Strategy Passing Type For String Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = "String"
            val loadedCache = Cache(callId, loadedBody)

            val fetchedBody = "Another String"
            val response: MyResult<String> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<String, String> = createSub(callId, {
                response
            }, String::class.java)

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, Gson().toJson(fetchedBody))
                true
            }

            val collector: FlowCollector<Resource<String>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loadedBody, it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute Strategy For String Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = "String"
            val loadedCache = Cache(callId, loadedBody)

            val fetchedBody = "Another String"
            val response: MyResult<String> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<String, String> = createSub(callId, {
                response
            })

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, Gson().toJson(fetchedBody))
                true
            }

            val collector: FlowCollector<Resource<String>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loadedBody, it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute Strategy For Int Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = 2
            val loadedString = "$loadedBody"
            val loadedCache = Cache(callId, loadedString)

            val fetchedBody = 4
            val fetchedString = "$fetchedBody"
            val response: MyResult<Int> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<Int, Int> = createSub(callId, {
                response
            })

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedString)
                true
            }

            val collector: FlowCollector<Resource<Int>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loadedBody, it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute Strategy For Double Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = 2.2
            val loadedString = "$loadedBody"
            val loadedCache = Cache(callId, loadedString)

            val fetchedBody = 3.1
            val fetchedString = "$fetchedBody"
            val response: MyResult<Double> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<Double, Double> = createSub(callId, {
                response
            })

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedString)
                true
            }

            val collector: FlowCollector<Resource<Double>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loadedBody, it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute Strategy For Boolean Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = false
            val loadedString = "$loadedBody"
            val loadedCache = Cache(callId, loadedString)

            val fetchedBody = true
            val fetchedString = "$fetchedBody"
            val response: MyResult<Boolean> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<Boolean, Boolean> = createSub(callId, {
                response
            })

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedString)
                true
            }

            val collector: FlowCollector<Resource<Boolean>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loadedBody, it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute Strategy For Float Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = 1f
            val loadedString = "$loadedBody"
            val loadedCache = Cache(callId, loadedString)

            val fetchedBody = 2f
            val fetchedString = "$fetchedBody"
            val response: MyResult<Float> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<Float, Float> = createSub(callId, {
                response
            })

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedString)
                true
            }

            val collector: FlowCollector<Resource<Float>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loadedBody, it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute Strategy For Long Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = 5L
            val loadedString = "$loadedBody"
            val loadedCache = Cache(callId, loadedString)

            val fetchedBody = 1L
            val fetchedString = "$fetchedBody"
            val response: MyResult<Long> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<Long, Long> = createSub(callId, {
                response
            })

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedString)
                true
            }

            val collector: FlowCollector<Resource<Long>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loadedBody, it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    // ---------------- Load With Single Emit Or Strict

    @Test
    fun `Execute A Non Strict Strategy For TestSubject Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = TestSubject(1, "Test")
            val loadedCache = Cache(callId, loadedBody.toJson())

            val fetchedBody = TestSubject(2)
            val response: MyResult<TestSubject> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<TestSubject, TestSubject> = createSub(callId, {
                response
            }, isStrict = false)

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedBody.toJson())
                true
            }

            val collector: FlowCollector<Resource<TestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertTrue(it !is OutdatedResource)
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertNotEquals(loadedBody, it.data)
                        assertEquals(loadedBody.intValue, it.data?.intValue)
                        assertEquals(loadedBody.stringValue, it.data?.stringValue)
                    }
                    3 -> {
                        assertTrue(it is OutdatedResource)
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertNotEquals(loadedBody, it.data)
                        assertEquals(loadedBody.intValue, it.data?.intValue)
                        assertEquals(loadedBody.stringValue, it.data?.stringValue)
                        assertEquals(fetchedBody, (it as OutdatedResource).newData)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute A Non Strict Strategy For OtherTestSubject Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = OtherTestSubject(1, "Test")
            val loadedCache = Cache(callId, loadedBody.toJson())

            val fetchedBody = OtherTestSubject(2)
            val response: MyResult<OtherTestSubject> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<OtherTestSubject, OtherTestSubject> = createSub(callId, {
                response
            }, isStrict = false)

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedBody.toJson())
                true
            }

            val collector: FlowCollector<Resource<OtherTestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertTrue(it !is OutdatedResource)
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loadedBody, it.data)
                    }
                    3 -> {
                        assertTrue(it is OutdatedResource)
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loadedBody, it.data)
                        assertEquals(fetchedBody, (it as OutdatedResource).newData)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute A Single Emit Strict Strategy For OtherTestSubject Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = OtherTestSubject(1, "Test")
            val loadedCache = Cache(callId, loadedBody.toJson())

            val fetchedBody = OtherTestSubject(2)
            val response: MyResult<OtherTestSubject> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<OtherTestSubject, OtherTestSubject> = createSub(callId, {
                response
            }, isStrict = true, isSingleEmit = true)

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedBody.toJson())
                true
            }

            val collector: FlowCollector<Resource<OtherTestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(2, counter)
        }

    @Test
    fun `Execute A Non Strict Single Emit Strategy For OtherTestSubject Successfully Should Load, Parse, Fetch, Transform, Save And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = OtherTestSubject(1, "Test")
            val loadedCache = Cache(callId, loadedBody.toJson())

            val fetchedBody = OtherTestSubject(2)
            val response: MyResult<OtherTestSubject> = MyResult.create(Response.success(fetchedBody))
            val sub: CacheStrategy<OtherTestSubject, OtherTestSubject> = createSub(callId, {
                response
            }, isStrict = false, isSingleEmit = true)

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertEquals(1, it.size)
                assertEquals(it[0]?.response, fetchedBody.toJson())
                true
            }

            val collector: FlowCollector<Resource<OtherTestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertTrue(it !is OutdatedResource)
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetchedBody, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(2, counter)
        }

    // ---------------- Fetch Failed

    @Test
    fun `Execute Strategy Successfully With Network Error Should Load, Parse And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = OtherTestSubject(1, "Test")
            val loadedCache = Cache(callId, loadedBody.toJson())

            val message = "Error here"
            val body = "{\"error\": \"$message\"}".toResponseBody(null)
            val response: MyResult<OtherTestSubject> = MyResult.create(Response.error(500, body))
            val sub: CacheStrategy<OtherTestSubject, OtherTestSubject> = createSub(callId, {
                response
            })

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertTrue(false)
                true
            }

            val collector: FlowCollector<Resource<OtherTestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loadedBody, it.data)
                    }
                    3 -> {
                        assertTrue(it is OfflineResource)
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loadedBody, it.data)
//                        assertEquals(message, it.message)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute A Single Emit Strategy Successfully With Network Error Should Load, Parse And Return A Resource With Successful Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = OtherTestSubject(1, "Test")
            val loadedCache = Cache(callId, loadedBody.toJson())

            val message = "Error here"
            val body = "{\"error\": \"$message\"}".toResponseBody(null)
            val response: MyResult<OtherTestSubject> = MyResult.create(Response.error(500, body))
            val sub: CacheStrategy<OtherTestSubject, OtherTestSubject> = createSub(callId, {
                response
            }, isStrict = true, isSingleEmit = true)

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertTrue(false)
                true
            }

            val collector: FlowCollector<Resource<OtherTestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertTrue(it is OfflineResource)
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loadedBody, it.data)
//                        assertEquals(message, it.message)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(2, counter)
        }

    // ---------------- Strategy Failed With Exception

    @Test
    fun `Execute Strategy Failed With Network Exception Should Load, Parse, And Return A Resource With Error Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = OtherTestSubject(1, "Test")
            val loadedCache = Cache(callId, loadedBody.toJson())

            val message = "Error here"
            val sub: CacheStrategy<OtherTestSubject, OtherTestSubject> = createSub(callId, {
                throw Exception(message)
            })

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertTrue(false)
                true
            }

            val collector: FlowCollector<Resource<OtherTestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loadedBody, it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.FAILURE, it.status)
//                        assertEquals(message, it.message)
                        assertNull(it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @Test
    fun `Execute A Single Emit Strategy Failed With Network Exception Should Load, Parse, And Return A Resource With Error Status`() =
        runTest {
            var counter = 0
            val callId = "Test"
            val loadedBody = OtherTestSubject(1, "Test")
            val loadedCache = Cache(callId, loadedBody.toJson())

            val message = "Error here"
            val sub: CacheStrategy<OtherTestSubject, OtherTestSubject> = createSub(callId, {
                throw Exception(message)
            }, isStrict = true, isSingleEmit = true)

            onGetCache = {
                assertEquals(callId, it)
                loadedCache
            }

            onPutCache = {
                assertTrue(false)
                true
            }

            val collector: FlowCollector<Resource<OtherTestSubject>> = createCollector {
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

    @Test
    fun `Execute Strategy Failed With Database Exception Should Return A Resource With Error Status`() =
        runTest {
            var counter = 0
            val callId = "Test"

            val message = "Error here"
            val sub: CacheStrategy<OtherTestSubject, OtherTestSubject> = createSub(callId, {
                assertTrue(false)
                throw Exception("Not allowed")
            })

            onGetCache = {
                assertEquals(callId, it)
                throw Exception(message)
            }

            onPutCache = {
                assertTrue(false)
                true
            }

            val collector: FlowCollector<Resource<OtherTestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.FAILURE, it.status)
                        assertEquals(message, it.message)
                        assertNull(it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(2, counter)
        }

    @Test
    fun `Execute Strategy Failed With Empty Id Should Return A Resource With Error Status`() =
        runTest {
            var counter = 0
            val callId = ""

            val sub: CacheStrategy<OtherTestSubject, OtherTestSubject> = createSub(callId, {
                assertTrue(false)
                throw Exception("Not allowed")
            })

            onGetCache = {
                assertTrue(false)
                throw Exception("Not allowed")
            }

            onPutCache = {
                assertTrue(false)
                throw Exception("Not allowed")
            }

            val collector: FlowCollector<Resource<OtherTestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.FAILURE, it.status)
                        assertNull(it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(2, counter)
        }

    @Test
    fun `Execute Strategy Failed With Blank Id Should Return A Resource With Error Status`() =
        runTest {
            var counter = 0
            val callId = "       "

            val sub: CacheStrategy<OtherTestSubject, OtherTestSubject> = createSub(callId, {
                assertTrue(false)
                throw Exception("Not allowed")
            })

            onGetCache = {
                assertTrue(false)
                throw Exception("Not allowed")
            }

            onPutCache = {
                assertTrue(false)
                throw Exception("Not allowed")
            }

            val collector: FlowCollector<Resource<OtherTestSubject>> = createCollector {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.FAILURE, it.status)
                        assertNull(it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(2, counter)
        }
}