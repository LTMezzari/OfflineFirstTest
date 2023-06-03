package mezzari.torres.lucas.database.entities

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import mezzari.torres.lucas.core.model.bo.Repository
import org.junit.Test

/**
 * @author Lucas T. Mezzari
 * @since 01/06/2023
 */
internal class RepositoryEntityTest {
    // ----------------------- asModel

    @Test
    fun `Test Valid RepositoryEntity To Valid Repository Entry`() {
        val id = "ID"
        val name = "Name"
        val fullName = "Full Name"
        val description = "Description"
        val language = "Language"
        val stars = 5
        val userId = "USER ID"
        val sub = RepositoryEntity(id, name, fullName, description, language, stars, userId)
        val model = sub.asEntry()
        assertEquals(sub.id, model.id)
        assertEquals(sub.name, model.name)
        assertEquals(sub.fullName, model.fullName)
        assertEquals(sub.description, model.description)
        assertEquals(sub.language, model.language)
        assertEquals(sub.stars, model.stars)
        assertEquals(sub.userId, model.userId)
    }
    // ----------------------- asEntity

    @Test
    fun `Test Valid Repository To Valid RepositoryEntity`() {
        val id = "ID"
        val name = "Name"
        val fullName = "Full Name"
        val description = "Description"
        val language = "Language"
        val stars = 5
        val userId = "USER ID"
        val sub = Repository(id, name, fullName, description, language, stars, userId = userId)
        val entity = sub.asEntity()
        assertNotNull(entity)
        assertEquals(sub.id, entity?.id)
        assertEquals(sub.name, entity?.name)
        assertEquals(sub.fullName, entity?.fullName)
        assertEquals(sub.description, entity?.description)
        assertEquals(sub.language, entity?.language)
        assertEquals(sub.stars, entity?.stars)
        assertEquals(sub.userId, entity?.userId)
    }

    @Test
    fun `Test Repository With No Id Should Return A Null Entity`() {
        val id = null
        val name = "Name"
        val fullName = "Full Name"
        val description = "Description"
        val language = "Language"
        val stars = 5
        val userId = "USER ID"
        val sub = Repository(id, name, fullName, description, language, stars, userId = userId)
        val entity = sub.asEntity()
        assertNull(entity)
    }

    @Test
    fun `Test Repository With No Name Should Return A Null Entity`() {
        val id = "ID"
        val name = null
        val fullName = "Full Name"
        val description = "Description"
        val language = "Language"
        val stars = 5
        val userId = "USER ID"
        val sub = Repository(id, name, fullName, description, language, stars, userId = userId)
        val entity = sub.asEntity()
        assertNull(entity)
    }

    @Test
    fun `Test Repository With No Full Name Should Return A Valid Entity`() {
        val id = "ID"
        val name = "Name"
        val fullName = null
        val description = "Description"
        val language = "Language"
        val stars = 5
        val userId = "USER ID"
        val sub = Repository(id, name, fullName, description, language, stars, userId = userId)
        val entity = sub.asEntity()
        assertNotNull(entity)
        assertEquals(sub.id, entity?.id)
        assertEquals(sub.name, entity?.name)
        assertEquals(sub.fullName, entity?.fullName)
        assertEquals(sub.description, entity?.description)
        assertEquals(sub.language, entity?.language)
        assertEquals(sub.stars, entity?.stars)
        assertEquals(sub.userId, entity?.userId)
    }

    @Test
    fun `Test Repository With No Description Should Return A Null Entity`() {
        val id = "ID"
        val name = "Name"
        val fullName = "Full Name"
        val description = null
        val language = "Language"
        val stars = 5
        val userId = "USER ID"
        val sub = Repository(id, name, fullName, description, language, stars, userId = userId)
        val entity = sub.asEntity()
        assertNull(entity)
    }

    @Test
    fun `Test Repository With No Language Should Return A Null Entity`() {
        val id = "ID"
        val name = "Name"
        val fullName = "Full Name"
        val description = "Description"
        val language = null
        val stars = 5
        val userId = "USER ID"
        val sub = Repository(id, name, fullName, description, language, stars, userId = userId)
        val entity = sub.asEntity()
        assertNull(entity)
    }

    @Test
    fun `Test Repository With No Stars Should Return A Valid Entity`() {
        val id = "ID"
        val name = "Name"
        val fullName = "Full Name"
        val description = "Description"
        val language = "Language"
        val userId = "USER ID"
        val sub = Repository(id, name, fullName, description, language, userId = userId)
        val entity = sub.asEntity()
        assertNotNull(entity)
        assertEquals(sub.id, entity?.id)
        assertEquals(sub.name, entity?.name)
        assertEquals(sub.fullName, entity?.fullName)
        assertEquals(sub.description, entity?.description)
        assertEquals(sub.language, entity?.language)
        assertEquals(sub.stars, 0)
        assertEquals(sub.userId, entity?.userId)
    }

    @Test
    fun `Test Repository With No User Id Should Return A Null Entity`() {
        val id = "ID"
        val name = "Name"
        val fullName = "Full Name"
        val description = "Description"
        val language = "Language"
        val stars = 5
        val userId = null
        val sub = Repository(id, name, fullName, description, language, stars, userId = userId)
        val entity = sub.asEntity()
        assertNull(entity)
    }
}