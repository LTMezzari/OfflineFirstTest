package mezzari.torres.lucas.network.archive

import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.MalformedJsonException
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.Test
import java.lang.Exception
import java.lang.NumberFormatException

/**
 * @author Lucas T. Mezzari
 * @since 03/06/2023
 */
internal class JsonUtilsTest {
    internal data class TestDataClass(
        val string: String,
        val number: Int,
        val decimal: Double,
        val boolean: Boolean
    )

    internal class TestClass(
        val nullable: String?,
        val subclass: TestDataClass?
    )

    internal class TestArray(
        val array: Array<Int>,
        val list: List<String>,
    )

    internal class TestCustomName(
        @SerializedName("customName", alternate = ["otherName"])
        val name: String
    )

    // ------------------------------------ toJson

    @Test
    fun `Test To JSON With A Data Class Should Have A Valid JSON`() {
        val string = "Hello"
        val number = 1
        val decimal = .3
        val boolean = false
        val expected =
            "{\"string\":\"$string\",\"number\":$number,\"decimal\":$decimal,\"boolean\":$boolean}"
        val sub = TestDataClass(string, number, decimal, boolean)
        val result = sub.toJson()
        assertEquals(expected, result)
    }

    @Test
    fun `Test To JSON With A Class Should Have A Valid JSON`() {
        val string = "Hello"
        val number = 1
        val decimal = .3
        val boolean = false
        val nullable = "Not Nullable"
        val subclass = TestDataClass(string, number, decimal, boolean)
        val expected =
            "{\"nullable\":\"$nullable\",\"subclass\":{\"string\":\"$string\",\"number\":$number,\"decimal\":$decimal,\"boolean\":$boolean}}"
        val sub = TestClass(nullable, subclass)
        val result = sub.toJson()
        assertEquals(expected, result)
    }

    @Test
    fun `Test To JSON With A Null Value Should Have A Valid JSON`() {
        val string = "Hello"
        val number = 1
        val decimal = .3
        val boolean = false
        val nullable = null
        val subclass = TestDataClass(string, number, decimal, boolean)
        val expected =
            "{\"subclass\":{\"string\":\"$string\",\"number\":$number,\"decimal\":$decimal,\"boolean\":$boolean}}"
        val sub = TestClass(nullable, subclass)
        val result = sub.toJson()
        assertEquals(expected, result)
    }

    @Test
    fun `Test To JSON With A Null Object Should Have A Valid JSON`() {
        val nullable = "Not Nullable"
        val subclass = null
        val expected = "{\"nullable\":\"$nullable\"}"
        val sub = TestClass(nullable, subclass)
        val result = sub.toJson()
        assertEquals(expected, result)
    }

    @Test
    fun `Test To JSON With Array Should Have A Valid JSON`() {
        val nullable = "Not Nullable"
        val subclass = null
        val expected = "[{\"nullable\":\"$nullable\"},{\"nullable\":\"$nullable\"}]"
        val sub = arrayOf(TestClass(nullable, subclass), TestClass(nullable, subclass))
        val result = sub.toJson()
        assertEquals(expected, result)
    }

    @Test
    fun `Test To JSON With List Should Have A Valid JSON`() {
        val string = "Hello"
        val number = 1
        val decimal = .3
        val boolean = false
        val expected =
            "[{\"string\":\"$string\",\"number\":$number,\"decimal\":$decimal,\"boolean\":$boolean}]"
        val sub = listOf(
            TestDataClass(string, number, decimal, boolean)
        )
        val result = sub.toJson()
        assertEquals(expected, result)
    }

    @Test
    fun `Test To JSON With Internal List Or Array Should Have A Valid JSON`() {
        val array = arrayOf(1, 2, 3)
        val list = listOf("1", "2", "3")
        val expected = "{\"array\":[1,2,3],\"list\":[\"1\",\"2\",\"3\"]}"
        val sub = TestArray(array, list)
        val result = sub.toJson()
        assertEquals(expected, result)
    }

    @Test
    fun `Test To JSON With Internal List Or Array Empty Should Have A Valid JSON`() {
        val array = arrayOf(1, 2, 3)
        val list = listOf<String>()
        val expected = "{\"array\":[1,2,3],\"list\":[]}"
        val sub = TestArray(array, list)
        val result = sub.toJson()
        assertEquals(expected, result)
    }

    @Test
    fun `Test To JSON With Custom Name Should Have A Valid JSON`() {
        val name = "Test"
        val expected = "{\"customName\":\"$name\"}"
        val sub = TestCustomName(name)
        val result = sub.toJson()
        assertEquals(expected, result)
    }

    @Test
    fun `Test To JSON With Empty Array Or List Should Have A Valid JSON`() {
        val expected = "[]"
        assertEquals(expected, listOf<TestDataClass>().toJson())
        assertEquals(expected, listOf<TestClass>().toJson())
    }

    @Test
    fun `Test To JSON With Everything Null Should Have A Empty JSON`() {
        val nullable = null
        val subclass = null
        val expected = "{}"
        val sub = TestClass(nullable, subclass)
        val result = sub.toJson()
        assertEquals(expected, result)
    }

    @Test
    fun `Test To JSON With A Null Value Should Have A Null String`() {
        val expected = "null"
        val sub = null
        val result = sub.toJson()
        assertEquals(expected, result)
    }

    // ------------------------------------ fromJson

    @Test
    fun `Test From JSON With Valid JSON Should Parse Correctly`() {
        val string = "Hello"
        val number = 1
        val decimal = .3
        val boolean = false
        val nullable = "Not Nullable"
        val json =
            "{\"nullable\":\"$nullable\",\"subclass\":{\"string\":\"$string\",\"number\":$number,\"decimal\":$decimal,\"boolean\":$boolean}}"
        val sub = fromJson<TestClass>(json, object : TypeToken<TestClass>() {}.type)
        assertNotNull(sub)
        assertTrue(sub is TestClass)
        assertEquals(nullable, sub?.nullable)
        assertEquals(string, sub?.subclass?.string)
        assertEquals(number, sub?.subclass?.number)
        assertEquals(decimal, sub?.subclass?.decimal)
        assertEquals(boolean, sub?.subclass?.boolean)
    }

    @Test
    fun `Test From JSON With Nullable Properties JSON Should Parse Correctly`() {
        val string = "Hello"
        val number = 1
        val decimal = .3
        val boolean = false
        val json =
            "{\"subclass\":{\"string\":\"$string\",\"number\":$number,\"decimal\":$decimal,\"boolean\":$boolean}}"
        val sub = fromJson<TestClass>(json, object : TypeToken<TestClass>() {}.type)
        assertNotNull(sub)
        assertTrue(sub is TestClass)
        assertNull(sub?.nullable)
        assertEquals(string, sub?.subclass?.string)
        assertEquals(number, sub?.subclass?.number)
        assertEquals(decimal, sub?.subclass?.decimal)
        assertEquals(boolean, sub?.subclass?.boolean)
    }

    @Test
    fun `Test From JSON With List Of Objects JSON Should Parse Correctly`() {
        val string = "Hello"
        val number = 1
        val decimal = .3
        val boolean = false
        val json =
            "[{\"string\":\"$string\",\"number\":$number,\"decimal\":$decimal,\"boolean\":$boolean}]"
        val sub = fromJson<List<TestDataClass>>(json)
        assertNotNull(sub)
        assertTrue(sub is List<TestDataClass>)
        assertTrue(sub?.isNotEmpty() == true)
        val first = sub?.first()
        assertEquals(string, first?.string)
        assertEquals(number, first?.number)
        assertEquals(decimal, first?.decimal)
        assertEquals(boolean, first?.boolean)
    }

    @Test
    fun `Test From JSON With Object With Array Or List Should Parse Correctly`() {
        val json = "{\"list\":[\"1\",\"2\",\"3\",],\"array\":[1,2,3]}"
        val sub = fromJson<TestArray>(json)
        assertNotNull(sub)
        assertTrue(sub is TestArray)
        assertTrue(sub?.array?.isNotEmpty() == true)
        assertTrue(sub?.list?.isNotEmpty() == true)
    }

    @Test
    fun `Test From JSON With Custom Name Should Parse Correctly`() {
        val name = "Test"
        val json = "{\"customName\":\"$name\"}"
        val sub = fromJson<TestCustomName>(json)
        assertNotNull(sub)
        assertTrue(sub is TestCustomName)
        assertEquals(name, sub?.name)
    }

    @Test
    fun `Test From JSON With Alternate Name Should Parse Correctly`() {
        val name = "Test"
        val json = "{\"otherName\":\"$name\"}"
        val sub = fromJson<TestCustomName>(json)
        assertNotNull(sub)
        assertTrue(sub is TestCustomName)
        assertEquals(name, sub?.name)
    }

    @Test
    fun `Test From JSON With Object With Empty Array Or List Should Parse Correctly`() {
        val json = "{\"list\":[],\"array\":[]}"
        val sub = fromJson<TestArray>(json)
        assertNotNull(sub)
        assertTrue(sub is TestArray)
        assertTrue(sub?.array?.isEmpty() == true)
        assertTrue(sub?.list?.isEmpty() == true)
    }

    @Test
    fun `Test From JSON With Empty JSON Should Parse Correctly`() {
        val json = "{}"
        val sub = fromJson<TestClass>(json, object : TypeToken<TestClass>() {}.type)
        assertNotNull(sub)
        assertTrue(sub is TestClass)
        assertNull(sub?.nullable)
        assertNull(sub?.subclass)
    }

    @Test
    fun `Test From JSON With Empty Array JSON Should Parse Correctly`() {
        val json = "[]"
        val sub1 = fromJson<List<TestClass>>(json)
        val sub2 = fromJson<Array<TestDataClass>>(json)
        assertNotNull(sub1)
        assertNotNull(sub2)
        assertTrue(sub1 is List<TestClass>)
        assertTrue(sub2 is Array<TestDataClass>)
    }

    @Test
    fun `Test From JSON With Null Value Should Return Null`() {
        val json = null
        val sub = fromJson<TestClass>(json, object : TypeToken<TestClass>() {}.type)
        assertNull(sub)
    }

    @Test
    fun `Test From JSON With Empty JSON Value Should Return Null`() {
        val json = ""
        val sub = fromJson<TestClass>(json, object : TypeToken<TestClass>() {}.type)
        assertNull(sub)
    }

    @Test
    fun `Test From JSON With A Blank JSON Value Should Return Null`() {
        val json = ""
        val sub = fromJson<TestClass>(json, object : TypeToken<TestClass>() {}.type)
        assertNull(sub)
    }

    @Test
    fun `Test From JSON With Wrong Type In String Should Parse Correctly`() {
        val nullable = 2
        val json = "{\"nullable\":$nullable}"
        val sub = fromJson<TestClass>(json, object : TypeToken<TestClass>() {}.type)
        assertNotNull(sub)
        assertTrue(sub is TestClass)
        assertNotNull(sub?.nullable)
        assertEquals(nullable.toString(), sub?.nullable)
    }

    @Test
    fun `Test From JSON With Missing Properties JSON Should Fill With Defaults`() {
        val string = "Hello"
        val json = "{\"string\":\"$string\"}"
        val sub = fromJson<TestDataClass>(json)
        assertNotNull(sub)
        assertTrue(sub is TestDataClass)
    }

    @Test
    fun `Test From JSON With Wrong Type On Number Or Boolean Should Try To Parse`() {
        val string = "Hello"
        val number = "1"
        val decimal = "2.0"
        val boolean = "true"
        val json = "{\"string\":\"$string\",\"number\":\"$number\",\"decimal\":\"$decimal\",\"boolean\":\"$boolean\"}"
        val sub = fromJson<TestDataClass>(json)
        assertNotNull(sub)
        assertTrue(sub is TestDataClass)
        assertEquals(string, sub?.string)
        assertEquals(number.toInt(), sub?.number)
        assertEquals(decimal.toDouble(), sub?.decimal)
        assertEquals(boolean.toBoolean(), sub?.boolean)
    }

    @Test
    fun `Test From JSON With Error On Parse Should Throw Exception`() {
        val string = "Hello"
        val number = "AAAA"
        val decimal = "BBBB"
        val boolean = "CCCC"
        val json = "{\"string\":\"$string\",\"number\":\"$number\",\"decimal\":\"$decimal\",\"boolean\":\"$boolean\"}"
        try {
            val sub = fromJson<TestDataClass>(json)
            assertNotNull(sub)
            fail()
        } catch (e: NumberFormatException) {
            assertTrue(
                e.message?.contains(number) == true
                        || e.message?.contains(decimal) == true
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `Test From JSON With Invalid JSON Should Throw Exception`() {
        val json = "{\"test\":\"a\",}"
        val json2 = "{"
        try {
            val sub = fromJson<TestDataClass>(json)
            val sub2 = fromJson<TestDataClass>(json2)
            assertNotNull(sub)
            assertNotNull(sub2)
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
            assertTrue(
                e is MalformedJsonException
                        || e is JsonSyntaxException
            )
        }
    }
}