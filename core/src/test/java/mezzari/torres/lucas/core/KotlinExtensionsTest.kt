package mezzari.torres.lucas.core

import mezzari.torres.lucas.core.archive.elvis
import mezzari.torres.lucas.core.archive.guard
import mezzari.torres.lucas.core.archive.then
import mezzari.torres.lucas.core.archive.unwrap
import mezzari.torres.lucas.core.archive.lazy
import org.junit.Test

import org.junit.Assert.*
import java.lang.Exception
import java.lang.reflect.Field

/**
 * @author Lucas T. Mezzari
 * @since 12/11/2022
 */
internal class KotlinExtensionsTest {
    // ----------------- Guard

    @Test
    fun `Execute Guard With No Null Values Returns Valid List With Same Size`() {
        val params = arrayOf("Lucas", 22, 10.0, true)
        val list = guard(*params)
        assertNotNull(list)
        assertEquals(list?.size, params.size)
    }

    @Test
    fun `Execute Guard With No Null Values Should Deconstruct List And Spread Values`() {
        try {
            val params = arrayOf("Lucas", 22, 10.0, true)
            val (name, age, balance, isValid) = guard(*params) ?: throw Exception("Invalid")
            assertNotNull(name)
            assertEquals(name, params[0])
            assertNotNull(age)
            assertEquals(age, params[1])
            assertNotNull(balance)
            assertEquals(balance, params[2])
            assertNotNull(isValid)
            assertEquals(isValid, params[3])
        } catch (e: Exception) {
            assertTrue(false)
        }
    }

    @Test
    fun `Execute Guard With A Null Value Returns Null`() {
        val params = arrayOf("Lucas", null, 10.0)
        val list = guard(*params)
        assertNotEquals(list?.size, params.size)
        assertNull(list)
    }

    @Test
    fun `Execute Guard With A Null Value Should Not Deconstruct List And Spread Values`() {
        try {
            val params = arrayOf("Lucas", 22, 10.0, null)
            val (_, _, _, _) = guard(*params) ?: throw Exception("Invalid")
            assertTrue(false)
        } catch (e: Exception) {
            assertTrue(true)
        }
    }

    // ----------------- Elvis

    @Test
    fun `Execute Elvis With A Not Null Instance Continues Execution`() {
        val sub = "Test"
        sub elvis {
            assertTrue(false)
            return
        }
        assertTrue(true)
    }

    @Test
    fun `Execute Elvis With A Null Instance Should Stop Execution`() {
        val sub: String? = null
        sub elvis {
            assertTrue(true)
            return
        }
        assertTrue(false)
    }

    // ----------------- Guard Elvis

    @Test
    fun `Execute Guard Elvis With No Null Values Should Avoid Elvis And Continue Execution With The Same List Size`() {
        val params = arrayOf("Lucas", 22, 10.0, true)
        val list = guard(*params) elvis {
            assertTrue(false)
            return
        }
        assertNotNull(list)
        assertEquals(list.size, params.size)
    }

    @Test
    fun `Execute Guard Elvis With No Null Values Should Avoid Elvis, Deconstruct List And Spread`() {
        val params = arrayOf("Lucas", 22, 10.0, true)
        val (name, age, balance, valid) = guard(*params) elvis {
            assertTrue(false)
            return
        }
        assertNotNull(name)
        assertEquals(name, params[0])
        assertNotNull(age)
        assertEquals(age, params[1])
        assertNotNull(balance)
        assertEquals(balance, params[2])
        assertNotNull(valid)
        assertEquals(valid, params[3])
    }

    @Test
    fun `Execute Guard Elvis With A Null Values Should Run Elvis`() {
        val params = arrayOf(null, 22, 10.0, true)
        val list = guard(*params) elvis {
            assertTrue(true)
            return
        }
        assertTrue(false)
        assertNull(list)
    }

    @Test
    fun `Execute Guard Elvis With A Null Values Should Run Elvis And Not Spread Deconstructed Values`() {
        val params = arrayOf(null, 22, 10.0, true)
        val (name, _, _, _) = guard(*params) elvis {
            assertTrue(true)
            return
        }
        assertTrue(false)
        assertNull(name)
    }

    // ----------------- Unwrap

    @Test
    fun `Execute Unwrap With No Null Values Returns Valid List With All Values`() {
        val name = "Lucas"
        val age = 22
        val balance = 10.0
        val isValid = true
        val params = arrayOf(name, age, balance, isValid)
        val list = unwrap(*params)
        assertNotNull(list)
        assertEquals(list?.size, params.size)
        assertEquals(name, list!![0])
        assertEquals(age, list[1])
        assertEquals(balance, list[2])
        assertEquals(isValid, list[3])
    }

    @Test
    fun `Execute Unwrap With A Null Value Returns A Null Object`() {
        val name = "Lucas"
        val age: Int? = null
        val balance = 10.0
        val isValid = true
        val params = arrayOf(name, age, balance, isValid)
        val list = unwrap(*params)
        assertNull(list)
    }

    // ----------------- Then

    @Test
    fun `Execute Then With A Not Null Instance Should Execute It's Function With A Not Null Param`() {
        val name = "Lucas"
        var hasExecuted = false
        assertNotNull(name)
        name then {
            assertNotNull(it)
            hasExecuted = true
        }
        assertTrue(hasExecuted)
    }

    @Test
    fun `Execute Then With A Not Null Instance Should Execute It's Function And Deconstruct The Param`() {
        val values = listOf("Lucas", 22)
        var hasExecuted = false
        assertNotNull(values)
        values then { (name, age) ->
            assertNotNull(name)
            assertEquals(name, values[0])
            assertNotNull(age)
            assertEquals(age, values[1])
            hasExecuted = true
        }
        assertTrue(hasExecuted)
    }

    @Test
    fun `Execute Then With A Null Instance Should Not Execute It's Function`() {
        val name: String? = null
        var hasExecuted = false
        assertNull(name)
        name then {
            assertNull(it)
            hasExecuted = true
        }
        assertFalse(hasExecuted)
    }

    // ----------------- Unwrap Then

    @Test
    fun `Execute Unwrap Then With No Null Values Should Execute It's Function And The Param Should Not Be Null`() {
        val name = "Lucas"
        val age = 22
        val balance = 10.0
        val isValid = true
        val params = arrayOf(name, age, balance, isValid)
        var hasExecuted = false
        unwrap(*params) then { list ->
            hasExecuted = true
            assertNotNull(list)
            assertEquals(list.size, params.size)
            assertEquals(name, list[0])
            assertEquals(age, list[1])
            assertEquals(balance, list[2])
            assertEquals(isValid, list[3])
        }
        assertTrue(hasExecuted)
    }

    @Test
    fun `Execute Unwrap Then With No Null Values Should Execute It's Function And The Param Should Be Deconstructed`() {
        val name = "Lucas"
        val age = 22
        val balance = 10.0
        val isValid = true
        val params = arrayOf(name, age, balance, isValid)
        var hasExecuted = false
        unwrap(*params) then { (param1, param2, param3, param4) ->
            hasExecuted = true
            assertEquals(name, param1)
            assertEquals(age, param2)
            assertEquals(balance, param3)
            assertEquals(isValid, param4)
        }
        assertTrue(hasExecuted)
    }

    @Test
    fun `Execute Unwrap Then With A Null Value Should Not Execute It's Function`() {
        val name = "Lucas"
        val age = 22
        val balance: Double? = null
        val isValid = true
        val params = arrayOf(name, age, balance, isValid)
        var hasExecuted = false
        unwrap(*params) then { list ->
            hasExecuted = true
            assertNull(list)
        }
        assertFalse(hasExecuted)
    }

    // ----------------- Lazy Var

    @Test
    fun `Declaring Lazy Var Should Have It's Value Null Until First Access`() {
        val defaultValue = "Soooooo Lazy"
        val mSub = object : Any() {
            var sub: String by lazy { defaultValue }
        }
        val javaClass = mSub::class.java
        var field: Field? = null
        javaClass.declaredFields.forEach {
            if (it.name != "sub\$delegate")
                return@forEach
            field = it
            return
        }
        assertNotNull(field)
        assertNull(field?.get(mSub))
        assertEquals(mSub.sub, defaultValue)
        assertNotNull(mSub.sub)
        assertNotNull(field?.get(mSub))
        assertEquals(field?.get(mSub), defaultValue)
    }

    @Test
    fun `Declaring Lazy Var Should Allow To Change It's Value`() {
        val defaultValue = "Soooooo Lazy"
        val newValue = "Not Anymore"
        val mSub = object : Any() {
            var sub: String by lazy { defaultValue }
        }
        val javaClass = mSub::class.java
        var field: Field? = null
        javaClass.declaredFields.forEach {
            if (it.name != "sub\$delegate")
                return@forEach
            field = it
            return
        }

        assertNotNull(field)
        assertNull(field?.get(mSub))
        assertEquals(mSub.sub, defaultValue)
        assertNotNull(mSub.sub)
        assertNotNull(field?.get(mSub))
        assertEquals(field?.get(mSub), defaultValue)

        mSub.sub = newValue
        assertNotNull(field?.get(mSub))
        assertEquals(field?.get(mSub), newValue)
        assertNotNull(mSub.sub)
        assertEquals(mSub.sub, newValue)
        assertNotEquals(mSub.sub, defaultValue)
    }

    @Test
    fun `Declaring Lazy Var Should Allow To Change It's Value To Null If Given The Type And Reset When Accessed`() {
        val defaultValue = "Soooooo Lazy"
        val newValue: String? = null
        val mSub = object : Any() {
            var sub: String? by lazy { defaultValue }
        }
        val javaClass = mSub::class.java
        var field: Field? = null
        javaClass.declaredFields.forEach {
            if (it.name != "sub\$delegate")
                return@forEach
            field = it
            return
        }

        assertNotNull(field)
        assertNull(field?.get(mSub))
        assertEquals(mSub.sub, defaultValue)
        assertNotNull(mSub.sub)
        assertNotNull(field?.get(mSub))
        assertEquals(field?.get(mSub), defaultValue)

        mSub.sub = newValue
        assertNull(field?.get(mSub))
        assertEquals(field?.get(mSub), newValue)
        assertNotNull(mSub.sub)
        assertEquals(mSub.sub, newValue)
        assertNotEquals(mSub.sub, defaultValue)
    }

}