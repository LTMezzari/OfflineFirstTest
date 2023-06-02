package mezzari.torres.lucas.database.entities

import mezzari.torres.lucas.core.model.bo.Cache
import org.junit.Assert.*
import org.junit.Test

/**
 * @author Lucas T. Mezzari
 * @since 31/05/2023
 */
class CacheEntityTest {
    // ----------------------- asModel

    @Test
    fun `Test Valid CacheEntity To Valid Cache Entry`() {
        val id = "Test"
        val response = "Test Response"
        val sub = CacheEntity(id, response)
        val model = sub.asEntry()
        assertEquals(sub.id, model.id)
        assertEquals(sub.response, model.response)
    }
    // ----------------------- asEntity

    @Test
    fun `Test Valid Cache To Valid CacheEntity`() {
        val id = "Test"
        val response = "Test Response"
        val sub = Cache(id, response)
        val entity = sub.asEntity()
        assertEquals(sub.id, entity.id)
        assertEquals(sub.response, entity.response)
    }
}