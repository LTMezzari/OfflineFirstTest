package mezzari.torres.lucas.network.strategies

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mezzari.torres.lucas.core.resource.OutdatedResource
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.network.wrapper.OfflineResource
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * @author Lucas T. Mezzari
 * @since 01/06/2023
 */
class OfflineStrategyTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = StandardTestDispatcher()

    private lateinit var sub: OfflineStrategy<Any>
    private lateinit var collector: FlowCollector<Resource<Any>>
    private lateinit var onFetchFromNetwork: () -> MyResult<Any>
    private lateinit var onLoadFromDatabase: () -> Any?
    private lateinit var onSaveToDatabase: (Any?) -> Unit
    private var shouldSave: ((Any?, Any?) -> Boolean)? = null
    private var shouldFetch: ((Any?) -> Boolean)? = null
    private var onEmit: ((Resource<Any>) -> Unit)? = null
    private var isSingleEmit: Boolean = false
    private var isStrict: Boolean = true

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        collector = FlowCollector { value -> onEmit?.invoke(value) }
        isSingleEmit = false
        isStrict = true
    }

    private fun createSub(isSingleEmit: Boolean = false, isStrict: Boolean = true): OfflineStrategy<Any> {
        return object: OfflineStrategy<Any>(
            call = { CompletableDeferred(onFetchFromNetwork()) },
            strict = isStrict,
            singleEmit = isSingleEmit
        ) {
            override suspend fun onSaveData(data: Any?) {
                onSaveToDatabase(data)
            }

            override suspend fun onLoadData(): Any? {
                return onLoadFromDatabase()
            }

            override fun shouldSave(loadedData: Any?, receivedData: Any?): Boolean {
                return shouldSave?.invoke(loadedData, receivedData) ?: true
            }

            override fun shouldFetch(loadedData: Any?): Boolean {
                return shouldFetch?.invoke(loadedData) ?: true
            }
        }
    }

    // ---------------- Load, Fetch, Save

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Execute Strategy Successfully Should Load, Fetch, Save And Return A Resource With Successful Status`() =
        runTest {
            sub = createSub(isSingleEmit, isStrict)

            var counter = 0
            val loaded = 2
            val fetched = 3
            val finalReturn: MyResult<Any> = MyResult.create(Response.success(fetched))

            onLoadFromDatabase = {
                loaded
            }
            onFetchFromNetwork = {
                finalReturn
            }
            onSaveToDatabase = {
                assertEquals(fetched, it)
            }
            shouldFetch = {
                assertEquals(loaded, it)
                true
            }

            onEmit = {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loaded, it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetched, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Execute A Not Strict Strategy Successfully Should Load, Fetch, Save And Return A Outdated Resource With Successful Status`() =
        runTest {
            isStrict = false
            sub = createSub(isSingleEmit, isStrict)

            var counter = 0
            val loaded = 2
            val fetched = 3
            val finalReturn: MyResult<Any> = MyResult.create(Response.success(fetched))

            onLoadFromDatabase = {
                loaded
            }
            onFetchFromNetwork = {
                finalReturn
            }
            onSaveToDatabase = {
                assertEquals(fetched, it)
            }
            shouldFetch = {
                assertEquals(loaded, it)
                true
            }

            onEmit = {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loaded, it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loaded, it.data)
                        assertTrue(it is OutdatedResource)
                        assertEquals(fetched, (it as OutdatedResource).newData)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Execute A Single Emit Strategy Successfully Should Load, Fetch, Save And Return Only Once A Resource With Successful Status`() =
        runTest {
            isSingleEmit = true
            sub = createSub(isSingleEmit, isStrict)

            var counter = 0
            val loaded = 2
            val fetched = 3
            val finalReturn: MyResult<Any> = MyResult.create(Response.success(fetched))

            onLoadFromDatabase = {
                loaded
            }
            onFetchFromNetwork = {
                finalReturn
            }
            onSaveToDatabase = {
                assertEquals(fetched, it)
            }
            shouldFetch = {
                assertEquals(loaded, it)
                true
            }

            onEmit = {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetched, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(2, counter)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Execute A Non Strict Single Emit Strategy Successfully Should Load, Fetch, Save And Return Only Once A Resource With Successful Status`() =
        runTest {
            isSingleEmit = true
            isStrict = false
            sub = createSub(isSingleEmit, isStrict)

            var counter = 0
            val loaded = 2
            val fetched = 3
            val finalReturn: MyResult<Any> = MyResult.create(Response.success(fetched))

            onLoadFromDatabase = {
                loaded
            }
            onFetchFromNetwork = {
                finalReturn
            }
            onSaveToDatabase = {
                assertEquals(fetched, it)
            }
            shouldFetch = {
                assertEquals(loaded, it)
                true
            }

            onEmit = {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertTrue(it !is OutdatedResource)
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetched, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(2, counter)
        }

    // ---------------- Load, Fetch

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Execute Strategy Successfully But Not Save Should Load, Fetch And Return A Resource With Successful Status`() =
        runTest {
            sub = createSub(isSingleEmit, isStrict)

            var counter = 0
            val loaded = 2
            val fetched = 3
            val finalReturn: MyResult<Any> = MyResult.create(Response.success(fetched))

            onLoadFromDatabase = {
                loaded
            }
            onFetchFromNetwork = {
                finalReturn
            }
            onSaveToDatabase = {
                assertTrue(false)
            }
            shouldFetch = {
                assertEquals(loaded, it)
                true
            }

            shouldSave = { pLoaded, pFetched ->
                assertEquals(loaded, pLoaded)
                assertEquals(fetched, pFetched)
                false
            }

            onEmit = {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loaded, it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(fetched, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    // ---------------- Load

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Execute Strategy Successfully But Not Fetch Should Only Load And Return A Resource With Successful Status`() =
        runTest {
            sub = createSub(isSingleEmit, isStrict)

            var counter = 0
            val loaded = 2
            val fetched = 3
            val finalReturn: MyResult<Any> = MyResult.create(Response.success(fetched))

            onLoadFromDatabase = {
                loaded
            }
            onFetchFromNetwork = {
                assertTrue(false)
                finalReturn
            }
            onSaveToDatabase = {
                assertTrue(false)
            }

            shouldFetch = { pLoaded ->
                assertEquals(loaded, pLoaded)
                false
            }

            shouldSave = { _, _ ->
                assertTrue(false)
                false
            }

            onEmit = {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loaded, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(2, counter)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Execute Strict Single Emit Strategy Successfully But Not Fetch Should Only Load And Return A Resource With Successful Status`() =
        runTest {
            isSingleEmit = true
            isStrict = true
            sub = createSub(isSingleEmit, isStrict)

            var counter = 0
            val loaded = 2
            val fetched = 3
            val finalReturn: MyResult<Any> = MyResult.create(Response.success(fetched))

            onLoadFromDatabase = {
                loaded
            }
            onFetchFromNetwork = {
                assertTrue(false)
                finalReturn
            }
            onSaveToDatabase = {
                assertTrue(false)
            }

            shouldFetch = { pLoaded ->
                assertEquals(loaded, pLoaded)
                false
            }

            shouldSave = { _, _ ->
                assertTrue(false)
                false
            }

            onEmit = {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loaded, it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(2, counter)
        }

    // ---------------- Fetch Failed

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Execute Strategy Successfully With Network Error Should Load, Fetch And Return A Offline Resource With Successful Status`() =
        runTest {
            sub = createSub(isSingleEmit, isStrict)

            var counter = 0
            val loaded = 2
            val message = "Error here"
            val body = "{\"error\": \"$message\"}".toResponseBody(null)
            val finalReturn: MyResult<Any> = MyResult.create(Response.error(500, body))

            onLoadFromDatabase = {
                loaded
            }
            onFetchFromNetwork = {
                finalReturn
            }
            onSaveToDatabase = {
                assertTrue(false)
            }
            shouldFetch = {
                assertEquals(loaded, it)
                true
            }

            onEmit = {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loaded, it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loaded, it.data)
//                        assertEquals(message, it.message)
                        assertTrue(it is OfflineResource)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Execute Single Emit Strategy Successfully With Network Error Should Load, Fetch And Return A Offline Resource With Successful Status`() =
        runTest {
            isSingleEmit = true
            sub = createSub(isSingleEmit, isStrict)

            var counter = 0
            val loaded = 2
            val message = "Error here"
            val body = "{\"error\": \"$message\"}".toResponseBody(null)
            val finalReturn: MyResult<Any> = MyResult.create(Response.error(500, body))

            onLoadFromDatabase = {
                loaded
            }
            onFetchFromNetwork = {
                finalReturn
            }
            onSaveToDatabase = {
                assertTrue(false)
            }
            shouldFetch = {
                assertEquals(loaded, it)
                true
            }

            onEmit = {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loaded, it.data)
//                        assertEquals(message, it.message)
                        assertTrue(it is OfflineResource)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(2, counter)
        }

    // ---------------- Strategy Failed With Exception

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Execute Strategy Failed With Database Exception Should Return A Resource With Error Status`() =
        runTest {
            sub = createSub(isSingleEmit, isStrict)

            var counter = 0
            val message = "Error here"
            val body = "{\"error\": \"$message\"}".toResponseBody(null)
            val finalReturn: MyResult<Any> = MyResult.create(Response.error(500, body))

            onLoadFromDatabase = {
                throw Exception(message)
            }
            onFetchFromNetwork = {
                assertTrue(false)
                finalReturn
            }
            onSaveToDatabase = {
                assertTrue(false)
            }
            shouldFetch = {
                assertTrue(false)
                true
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
    fun `Execute Strategy Failed With Network Exception Should Load And Return A Resource With Error Status`() =
        runTest {
            sub = createSub(isSingleEmit, isStrict)

            var counter = 0
            val message = "Error here"
            val loaded = 2

            onLoadFromDatabase = {
                loaded
            }
            onFetchFromNetwork = {
                throw Exception(message)
            }
            onSaveToDatabase = {
                assertTrue(false)
            }
            shouldFetch = {
                assertEquals(loaded, it)
                true
            }

            onEmit = {
                counter++
                when (counter) {
                    1 -> assertEquals(Resource.Status.LOADING, it.status)
                    2 -> {
                        assertEquals(Resource.Status.SUCCESS, it.status)
                        assertEquals(loaded, it.data)
                    }
                    3 -> {
                        assertEquals(Resource.Status.FAILURE, it.status)
                        assertEquals(message, it.message)
                        assertNull(it.data)
                    }
                    else -> assertTrue(false)
                }
            }

            sub.execute(collector)
            assertEquals(3, counter)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Execute Strict Single Emit Strategy Failed With Network Exception Should Load And Return A Resource With Error Status`() =
        runTest {
            isSingleEmit = true
            sub = createSub(isSingleEmit, isStrict)

            var counter = 0
            val message = "Error here"
            val loaded = 2

            onLoadFromDatabase = {
                loaded
            }
            onFetchFromNetwork = {
                throw Exception(message)
            }
            onSaveToDatabase = {
                assertTrue(false)
            }
            shouldFetch = {
                assertEquals(loaded, it)
                true
            }

            onEmit = {
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Execute Not Strict, Single Emit Strategy Failed With Network Exception Should Load And Return A Resource With Error Status`() =
        runTest {
            isSingleEmit = true
            isStrict = false
            sub = createSub(isSingleEmit, isStrict)

            var counter = 0
            val message = "Error here"
            val loaded = 2

            onLoadFromDatabase = {
                loaded
            }
            onFetchFromNetwork = {
                throw Exception(message)
            }
            onSaveToDatabase = {
                assertTrue(false)
            }
            shouldFetch = {
                assertEquals(loaded, it)
                true
            }

            onEmit = {
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

}