package mezzari.torres.lucas.user_repositories.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mezzari.torres.lucas.android.persistence.preferences.IPreferencesManager
import mezzari.torres.lucas.android.persistence.session.ISessionManager
import mezzari.torres.lucas.core.interfaces.IAppDispatcher
import mezzari.torres.lucas.core.model.bo.Repository
import mezzari.torres.lucas.core.model.bo.User
import mezzari.torres.lucas.core.resource.Resource
import mezzari.torres.lucas.user_repositories.service.IGithubService
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.coroutines.CoroutineContext

/**
 * @author Lucas T. Mezzari
 * @since 12/11/2022
 */
class SearchViewModelTest {
    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var sub: SearchViewModel
    private lateinit var dispatcher: IAppDispatcher
    private lateinit var service: IGithubService
    private lateinit var preferences: IPreferencesManager
    private lateinit var session: ISessionManager

    private var sessionUser: User? = null
    private var preferencesUser: User? = null

    private var dummy: User? = User()
    private var dummyResource: Resource<User> = Resource.success(dummy)

    private var requestGetUserListener: ((userId: String) -> Unit)? = null

    @Before
    fun setUp() {
        sessionUser = null
        preferencesUser = null
        dummy = User()
        dummyResource = Resource.success(dummy)
        requestGetUserListener = null

        dispatcher =
            object : IAppDispatcher {
                override var main: CoroutineContext = Dispatchers.Unconfined
                override var io: CoroutineContext = Dispatchers.Unconfined
            }
        service = object : IGithubService {
            override fun getUser(userId: String): Flow<Resource<User>> {
                return flow {
                    requestGetUserListener?.invoke(userId)
                    emit(dummyResource)
                }
            }

            override fun getRepositories(userId: String, page: Int): Flow<Resource<List<Repository>>> {
                throw Exception("Unimplemented")
            }

            override fun syncRepositories(userId: String): Flow<Resource<List<Repository>>> {
                throw Exception("Unimplemented")
            }
        }
        preferences = object : IPreferencesManager {
            override var user: User? by this@SearchViewModelTest::preferencesUser
        }
        session = object : ISessionManager {
            override var user: User? by this@SearchViewModelTest::sessionUser
        }

        sub = SearchViewModel(dispatcher, service, session, preferences)
    }

    // -------------------------- ViewModel Search

    @Test
    fun `Changing Search Value Should Dispatch An Update`() {
        val newValue = "Hello"
        var dispatchedUpdates = 0
        sub.search.observeForever {
            dispatchedUpdates++
            assertEquals(it, newValue)
        }
        assertNull(sub.search.value)
        sub.search.value = newValue
        assertEquals(sub.search.value, newValue)
        assertEquals(dispatchedUpdates, 1)
    }

    @Test
    fun `Posting Search Value Changes Should Dispatch An Update`() {
        val newValue = null
        var dispatchedUpdates = 0
        sub.search.observeForever {
            dispatchedUpdates++
            assertEquals(it, newValue)
        }
        assertNull(sub.search.value)
        sub.search.postValue(newValue)
        assertEquals(sub.search.value, newValue)
        assertEquals(dispatchedUpdates, 1)
    }

    // -------------------------- ViewModel Is Search Valid

    @Test
    fun `Changing Search To A Valid String Should Change Is Search Valid To True`() {
        val newValue = "Hello"
        var dispatchedUpdates = 0
        sub.isSearchValid.observeForever {
            dispatchedUpdates++
        }
        assertNull(sub.search.value)
        assertNull(sub.isSearchValid.value)

        sub.search.postValue(newValue)

        assertNotNull(sub.isSearchValid.value)
        assertTrue(sub.isSearchValid.value!!)
        assertEquals(dispatchedUpdates, 1)
    }

    @Test
    fun `Changing Search To Null Should Change Is Search Valid To False`() {
        val newValue: String? = null
        var dispatchedUpdates = 0
        sub.isSearchValid.observeForever {
            dispatchedUpdates++
        }
        assertNull(sub.search.value)
        assertNull(sub.isSearchValid.value)

        sub.search.postValue(newValue)

        assertNotNull(sub.isSearchValid.value)
        assertFalse(sub.isSearchValid.value!!)
        assertEquals(dispatchedUpdates, 1)
    }

    @Test
    fun `Changing Search To A Blank String Should Change Is Search Valid To False`() {
        val newValue = "                "
        var dispatchedUpdates = 0
        sub.isSearchValid.observeForever {
            dispatchedUpdates++
        }
        assertNull(sub.search.value)
        assertNull(sub.isSearchValid.value)

        sub.search.postValue(newValue)

        assertNotNull(sub.isSearchValid.value)
        assertFalse(sub.isSearchValid.value!!)
        assertEquals(dispatchedUpdates, 1)
    }

    @Test
    fun `Changing Search To A Empty String Should Change Is Search Valid To False`() {
        val newValue = ""
        var dispatchedUpdates = 0
        sub.isSearchValid.observeForever {
            dispatchedUpdates++
        }
        assertNull(sub.search.value)
        assertNull(sub.isSearchValid.value)

        sub.search.postValue(newValue)

        assertNotNull(sub.isSearchValid.value)
        assertFalse(sub.isSearchValid.value!!)
        assertEquals(dispatchedUpdates, 1)
    }

    // -------------------------- ViewModel Is Loading

    @Test
    fun `Request Loading With Valid Search Should Change Is Loading To True`() {
        val newValue = "Hello"
        var dispatchedUpdates = 0
        sub.isValid.observeForever {
            dispatchedUpdates++
        }
        sub.isLoading.observeForever {
            dispatchedUpdates++
        }
        assertNull(sub.search.value)
        assertNull(sub.isLoading.value)

        sub.search.postValue(newValue)

        dummyResource = Resource.loading()
        sub.getUser {
            assertTrue(false)
        }

        assertNotNull(sub.isLoading.value)
        assertTrue(sub.isLoading.value!!)
        assertEquals(dispatchedUpdates, 4)
    }

    @Test
    fun `Request Was Successful With Valid Search Should Change Is Loading To False`() {
        val newValue = "Hello"
        var dispatchedUpdates = 0
        sub.isValid.observeForever {
            dispatchedUpdates++
        }
        sub.isLoading.observeForever {
            dispatchedUpdates++
        }
        assertNull(sub.search.value)
        assertNull(sub.isLoading.value)

        sub.search.postValue(newValue)

        sub.getUser {
            assertNotNull(it)
            assertEquals(it, dummy)
        }

        assertNotNull(sub.isLoading.value)
        assertFalse(sub.isLoading.value!!)
        assertEquals(dispatchedUpdates, 4)
    }

    @Test
    fun `Request Was Not Successful With Valid Search Should Change Is Loading To False`() {
        val newValue = "Hello"
        val error = "FAILURE"
        var dispatchedUpdates = 0
        sub.isValid.observeForever {
            dispatchedUpdates++
        }
        sub.isLoading.observeForever {
            dispatchedUpdates++
        }
        assertNull(sub.search.value)
        assertNull(sub.isLoading.value)

        sub.search.postValue(newValue)

        dummyResource = Resource.error(error)
        sub.getUser {
            assertNotEquals(it, dummy)
            assertNull(it)
        }

        assertNotNull(sub.isLoading.value)
        assertFalse(sub.isLoading.value!!)
        assertEquals(dispatchedUpdates, 4)
    }

    @Test
    fun `Request Loading With Invalid Search Should Keep It's Value To Null`() {
        val newValue = "               "
        var dispatchedUpdates = 0
        sub.isLoading.observeForever {
            dispatchedUpdates++
        }
        assertNull(sub.search.value)
        assertNull(sub.isLoading.value)

        sub.search.postValue(newValue)

        sub.getUser {
            assertTrue(false)
        }

        assertNull(sub.isLoading.value)
        assertEquals(dispatchedUpdates, 0)
    }

    // -------------------------- ViewModel Is Valid

    @Test
    fun `Changing Search To A Valid Value Should Change Is Valid To True`() {
        val newValue = "Hello"
        var dispatchedUpdates = 0
        sub.isValid.observeForever {
            dispatchedUpdates++
        }
        assertNull(sub.search.value)
        assertNotNull(sub.isValid.value)
        assertFalse(sub.isValid.value!!)

        sub.search.postValue(newValue)

        assertNotNull(sub.isValid.value)
        assertTrue(sub.isValid.value!!)
        assertEquals(dispatchedUpdates, 2)
    }

    @Test
    fun `Changing Search To A Invalid Value Should Change Is Valid To False`() {
        val newValue = ""
        var dispatchedUpdates = 0
        sub.isValid.observeForever {
            dispatchedUpdates++
        }
        assertNull(sub.search.value)
        assertNotNull(sub.isValid.value)
        assertFalse(sub.isValid.value!!)

        sub.search.postValue(newValue)

        assertNotNull(sub.isValid.value)
        assertFalse(sub.isValid.value!!)
        assertEquals(dispatchedUpdates, 2)
    }

    @Test
    fun `Changing Search To A Valid Value While Loading Should Change Is Valid To False`() {
        val newValue = "Hello"
        var dispatchedUpdates = 0
        sub.isValid.observeForever {
            dispatchedUpdates++
        }
        assertNull(sub.search.value)
        assertNotNull(sub.isValid.value)
        assertFalse(sub.isValid.value!!)

        sub.search.postValue(newValue)

        assertNotNull(sub.isValid.value)
        assertTrue(sub.isValid.value!!)
        assertEquals(dispatchedUpdates, 2)

        dummyResource = Resource.loading()
        sub.getUser {
            assertTrue(false)
        }

        assertNotNull(sub.isLoading.value)
        assertTrue(sub.isLoading.value!!)

        assertNotNull(sub.isValid.value)
        assertFalse(sub.isValid.value!!)
        assertEquals(dispatchedUpdates, 3)
    }

    @Test
    fun `Changing Search To A Valid Value When Request is Successful Should Change Is Valid To True`() {
        val newValue = "Hello"
        var dispatchedUpdates = 0
        sub.isValid.observeForever {
            dispatchedUpdates++
        }
        assertNull(sub.search.value)
        assertNotNull(sub.isValid.value)
        assertFalse(sub.isValid.value!!)

        sub.search.postValue(newValue)

        assertNotNull(sub.isValid.value)
        assertTrue(sub.isValid.value!!)
        assertEquals(dispatchedUpdates, 2)

        sub.getUser {
            assertNotNull(it)
            assertEquals(it, dummy)
        }

        assertNotNull(sub.isLoading.value)
        assertFalse(sub.isLoading.value!!)

        assertNotNull(sub.isValid.value)
        assertTrue(sub.isValid.value!!)
        assertEquals(dispatchedUpdates, 3)
    }

    @Test
    fun `Changing Search To A Valid Value When Request is Not Successful Should Change Is Valid To True`() {
        val newValue = "Hello"
        val error = "FAILURE"
        var dispatchedUpdates = 0
        sub.isValid.observeForever {
            dispatchedUpdates++
        }
        assertNull(sub.search.value)
        assertNotNull(sub.isValid.value)
        assertFalse(sub.isValid.value!!)

        sub.search.postValue(newValue)

        assertNotNull(sub.isValid.value)
        assertTrue(sub.isValid.value!!)
        assertEquals(dispatchedUpdates, 2)

        dummyResource = Resource.error(error, dummy)
        sub.getUser {
            assertNotNull(it)
            assertEquals(it, dummy)
        }

        assertNotNull(sub.isLoading.value)
        assertFalse(sub.isLoading.value!!)

        assertNotNull(sub.isValid.value)
        assertTrue(sub.isValid.value!!)
        assertEquals(dispatchedUpdates, 3)
    }

    // -------------------------- ViewModel Error

    @Test
    fun `Request is Not Successful Should Make Error Not Null`() {
        val newValue = "Hello"
        val error = "FAILURE"
        var dispatchedUpdates = 0
        sub.isValid.observeForever {
            dispatchedUpdates++
        }
        sub.error.observeForever {
            dispatchedUpdates++
        }

        sub.search.postValue(newValue)

        assertNotNull(sub.isValid.value)
        assertTrue(sub.isValid.value!!)

        assertNull(sub.error.value)

        dummyResource = Resource.error(error, dummy)
        sub.getUser {
            assertNotNull(it)
            assertEquals(it, dummy)
        }

        assertNotNull(sub.error.value)
        assertEquals(sub.error.value, error)
        assertEquals(dispatchedUpdates, 4)
    }

    @Test
    fun `Request is Successful Should Keep Error As Null`() {
        val newValue = "Hello"
        var dispatchedUpdates = 0
        sub.isValid.observeForever {
            dispatchedUpdates++
        }
        sub.error.observeForever {
            dispatchedUpdates++
        }

        sub.search.postValue(newValue)

        assertNotNull(sub.isValid.value)
        assertTrue(sub.isValid.value!!)

        assertNull(sub.error.value)

        sub.getUser {
            assertNotNull(it)
            assertEquals(it, dummy)
        }

        assertNull(sub.error.value)
        assertEquals(dispatchedUpdates, 4)
    }

    @Test
    fun `Request is Loading Should Keep Error As Null`() {
        val newValue = "Hello"
        var dispatchedUpdates = 0
        sub.isValid.observeForever {
            dispatchedUpdates++
        }
        sub.error.observeForever {
            dispatchedUpdates++
        }

        sub.search.postValue(newValue)

        assertNotNull(sub.isValid.value)
        assertTrue(sub.isValid.value!!)

        assertNull(sub.error.value)

        dummyResource = Resource.loading()
        sub.getUser {
            assertNotNull(it)
            assertEquals(it, dummy)
        }

        assertNull(sub.error.value)
        assertEquals(dispatchedUpdates, 4)
    }

    // -------------------------- ViewModel Get User

    @Test
    fun `Requesting Get User Successfully With Valid Search Should Keep Error As Null, Loading As False, Callback And Update Session And Preferences`() {
        val newValue = "Hello"
        var dispatchedUpdates = 0
        sub.isValid.observeForever {
            dispatchedUpdates++
        }

        sub.search.postValue(newValue)

        assertTrue(sub.isValid.value!!)

        assertNull(sub.error.value)
        assertNull(sub.isLoading.value)

        requestGetUserListener = {
            assertEquals(it, newValue)
        }

        sub.getUser {
            assertNotNull(it)
            assertEquals(it, dummy)
        }

        assertNull(sub.error.value)
        assertFalse(sub.isLoading.value!!)
        assertEquals(dispatchedUpdates, 3)

        assertEquals(preferences.user, dummy)
        assertEquals(session.user, dummy)
    }

    @Test
    fun `Failure In Requesting Get User With Valid Search Should Change Error, Have Loading As False, Callback And Keep Session And Preferences`() {
        val newValue = "Hello"
        val error = "Error"
        var dispatchedUpdates = 0
        sub.isValid.observeForever {
            dispatchedUpdates++
        }
        sub.error.observeForever {
            dispatchedUpdates++
        }

        sub.search.postValue(newValue)

        assertTrue(sub.isValid.value!!)

        assertNull(sub.error.value)
        assertNull(sub.isLoading.value)

        requestGetUserListener = {
            assertEquals(it, newValue)
        }

        dummyResource = Resource.error(error)
        sub.getUser {
            assertNull(it)
            assertNotEquals(it, dummy)
        }

        assertFalse(sub.isLoading.value!!)
        assertNotNull(sub.error.value)
        assertEquals(sub.error.value, error)
        assertEquals(dispatchedUpdates, 4)

        assertNull(preferences.user)
        assertNull(session.user)
    }

    @Test
    fun `Failure In Requesting Get User With Valid Search Should Change Error With Dummy, Have Loading As False, Callback And Update Session And Preferences`() {
        val newValue = "Hello"
        val error = "Error"
        var dispatchedUpdates = 0
        sub.isValid.observeForever {
            dispatchedUpdates++
        }
        sub.error.observeForever {
            dispatchedUpdates++
        }

        sub.search.postValue(newValue)

        assertTrue(sub.isValid.value!!)

        assertNull(sub.error.value)
        assertNull(sub.isLoading.value)

        requestGetUserListener = {
            assertEquals(it, newValue)
        }

        dummyResource = Resource.error(error, dummy)
        sub.getUser {
            assertNotNull(it)
            assertEquals(it, dummy)
        }

        assertFalse(sub.isLoading.value!!)
        assertNotNull(sub.error.value)
        assertEquals(sub.error.value, error)
        assertEquals(dispatchedUpdates, 4)

        assertNotNull(preferences.user)
        assertEquals(preferences.user, dummy)
        assertNotNull(session.user)
        assertEquals(preferences.user, dummy)
    }

    @Test
    fun `Loading Request In Get User With Valid Search Should Keep Error As Null, Have Loading As True, Never Callback And Keep Session And Preferences`() {
        val newValue = "Hello"
        var dispatchedUpdates = 0
        sub.isValid.observeForever {
            dispatchedUpdates++
        }
        sub.error.observeForever {
            dispatchedUpdates++
        }
        sub.isLoading.observeForever {
            dispatchedUpdates++
        }

        sub.search.postValue(newValue)

        assertTrue(sub.isValid.value!!)

        assertNull(sub.error.value)
        assertNull(sub.isLoading.value)

        requestGetUserListener = {
            assertEquals(it, newValue)
        }

        dummyResource = Resource.loading()
        sub.getUser {
            assertTrue(false)
        }

        assertTrue(sub.isLoading.value!!)
        assertNull(sub.error.value)
        assertEquals(dispatchedUpdates, 5)

        assertNull(preferences.user)
        assertNull(session.user)
    }

    @Test
    fun `Request Get User With Invalid Search Should Keep Error As Null, Have Loading As Null, Never Callback And Keep Session And Preferences`() {
        val newValue = "         "
        var dispatchedUpdates = 0
        sub.isValid.observeForever {
            dispatchedUpdates++
        }
        sub.error.observeForever {
            dispatchedUpdates++
        }
        sub.isLoading.observeForever {
            dispatchedUpdates++
        }

        sub.search.postValue(newValue)

        assertFalse(sub.isValid.value!!)

        assertNull(sub.error.value)
        assertNull(sub.isLoading.value)

        requestGetUserListener = {
            assertTrue(false)
        }

        sub.getUser {
            assertTrue(false)
        }

        assertNull(sub.isLoading.value)
        assertNull(sub.error.value)
        assertEquals(dispatchedUpdates, 2)

        assertNull(preferences.user)
        assertNull(session.user)
    }
}