package com.example.database.entity

import com.example.dietboxtest.core.model.Cache
import org.junit.Assert.*
import org.junit.Test

class CacheEntityTest {
    // ----------------------- asModel

    @Test
    fun `Test Valid CacheEntity To Valid Cache Model`() {
        val id = "Test"
        val response = "Test Response"
        val sub = CacheEntity(id, response)
        val model = sub.asModel()
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