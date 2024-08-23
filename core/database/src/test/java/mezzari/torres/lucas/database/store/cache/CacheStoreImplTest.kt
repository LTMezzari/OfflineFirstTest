package mezzari.torres.lucas.database.store.cache

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mezzari.torres.lucas.core.model.bo.Cache
import mezzari.torres.lucas.database.dao.CacheDao
import mezzari.torres.lucas.database.entities.CacheEntity
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

/**
 * @author Lucas T. Mezzari
 * @since 01/06/2023
 */
internal class CacheStoreImplTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var dao: CacheDao
    private lateinit var sub: CacheStoreImpl

    private var onCacheSaved: ((List<CacheEntity>) -> Unit)? = null
    private var onCacheDummiesRequested: ((String) -> Unit)? = null
    private var cacheDummies: List<CacheEntity>? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        dao = object : CacheDao {
            override suspend fun getCache(cacheId: String): List<CacheEntity>? {
                onCacheDummiesRequested?.invoke(cacheId)
                return cacheDummies
            }

            override suspend fun putCache(caches: List<CacheEntity>) {
                onCacheSaved?.invoke(caches)
            }

        }
        onCacheSaved = null
        onCacheDummiesRequested = null
        cacheDummies = null

        sub = CacheStoreImpl(dao)
    }

    // ----------------------------- getCache(cacheId: String): Cache?

    @Test
    fun `Request Cache With Valid String And With Valid Return Should Return First Cache`() =
        runTest {
            val cacheId = "Testing Id"
            val cacheId1 = "Testing Id 2"
            val cacheResponse = "{}"
            val cacheResponse1 = "[]"
            var requestCounter = 0
            cacheDummies =
                listOf(CacheEntity(cacheId, cacheResponse), CacheEntity(cacheId1, cacheResponse1))
            onCacheDummiesRequested = { id ->
                assertEquals(cacheId, id)
                requestCounter++
            }

            assertNotNull(cacheDummies)
            assertNotNull(onCacheDummiesRequested)

            val result: Cache? = sub.getCache(cacheId)

            assertEquals(requestCounter, 1)
            assertNotNull(result)
            assertNotEquals(result, cacheDummies!![0])
            assertEquals(cacheId, result?.id)
            assertEquals(cacheResponse, result?.response)
        }

    @Test
    fun `Request Cache With Empty String Should Return Null`() =
        runTest {
            val cacheId = ""

            val result: Cache? = sub.getCache(cacheId)

            assertNull(result)
        }

    @Test
    fun `Request Cache With Blank String Should Return Null`() =
        runTest {
            val cacheId = "                "

            val result: Cache? = sub.getCache(cacheId)

            assertNull(result)
        }

    @Test
    fun `Request Cache With Valid String And With Null Return Should Return Null`() =
        runTest {
            val cacheId = "Testing Id"
            var requestCounter = 0
            cacheDummies = null
            onCacheDummiesRequested = { id ->
                assertEquals(cacheId, id)
                requestCounter++
            }

            assertNull(cacheDummies)
            assertNotNull(onCacheDummiesRequested)

            val result: Cache? = sub.getCache(cacheId)

            assertEquals(requestCounter, 1)
            assertNull(result)
        }

    @Test
    fun `Request Cache With Valid String And With Empty Return Should Return Null`() =
        runTest {
            val cacheId = "Testing Id"
            var requestCounter = 0
            cacheDummies = listOf()
            onCacheDummiesRequested = { id ->
                assertEquals(cacheId, id)
                requestCounter++
            }

            assertNotNull(cacheDummies)
            assertNotNull(onCacheDummiesRequested)

            val result: Cache? = sub.getCache(cacheId)

            assertEquals(requestCounter, 1)
            assertNull(result)
        }

    // ----------------------------- putCaches(vararg caches: Cache?): Boolean

    @Test
    fun `Save One Cache Should Return True And Parse It To CacheEntity`() =
        runTest {
            val cacheId = "Testing Id"
            val cacheResponse = "{}"
            var requestCounter = 0
            val caches = arrayOf(Cache(cacheId, cacheResponse))
            onCacheSaved = { list ->
                assertEquals(caches.size, list.size)
                assertEquals(caches[0].id, list[0].id)
                assertEquals(caches[0].response, list[0].response)
                requestCounter++
            }

            val result: Boolean = sub.saveCache(*caches)

            assertEquals(requestCounter, 1)
            assertTrue(result)
        }

    @Test
    fun `Save More Than One Cache Should Return True And Parse All To CacheEntity`() =
        runTest {
            val cacheId = "Testing Id"
            val cacheId1 = "Testing Id 1"
            val cacheResponse = "{}"
            val cacheResponse1 = "[]"
            var requestCounter = 0
            val caches = arrayOf(Cache(cacheId, cacheResponse), Cache(cacheId1, cacheResponse1))
            onCacheSaved = { list ->
                assertEquals(caches.size, list.size)
                assertEquals(caches[0].id, list[0].id)
                assertEquals(caches[0].response, list[0].response)
                assertEquals(caches[1].id, list[1].id)
                assertEquals(caches[1].response, list[1].response)
                requestCounter++
            }

            val result: Boolean = sub.saveCache(*caches)

            assertEquals(requestCounter, 1)
            assertTrue(result)
        }

    @Test
    fun `Save More Than One Cache And One Null Should Return True And Parse All Not Null To CacheEntity`() =
        runTest {
            val cacheId = "Testing Id"
            val cacheId1 = "Testing Id 1"
            val cacheResponse = "{}"
            val cacheResponse1 = "[]"
            var requestCounter = 0
            val caches =
                arrayOf(Cache(cacheId, cacheResponse), null, Cache(cacheId1, cacheResponse1))
            onCacheSaved = { list ->
                assertEquals(list.size, 2)
                assertEquals(caches[0]?.id, list[0].id)
                assertEquals(caches[0]?.response, list[0].response)
                assertEquals(caches[2]?.id, list[1].id)
                assertEquals(caches[2]?.response, list[1].response)
                requestCounter++
            }

            val result: Boolean = sub.saveCache(*caches)

            assertEquals(requestCounter, 1)
            assertTrue(result)
        }

    @Test
    fun `Save A Null Object Should Return False And Don't Execute Dao`() =
        runTest {
            val caches: Array<Cache?> = arrayOf(null)
            onCacheSaved = {
                assertTrue(false)
            }

            val result: Boolean = sub.saveCache(*caches)

            assertFalse(result)
        }

    @Test
    fun `Save Multiple Null Objects Should Return False And Don't Execute Dao`() =
        runTest {
            val caches: Array<Cache?> = arrayOf(null, null, null)
            onCacheSaved = {
                assertTrue(false)
            }

            val result: Boolean = sub.saveCache(*caches)

            assertFalse(result)
        }
}