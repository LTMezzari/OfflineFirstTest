package mezzari.torres.lucas.database.entities

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import mezzari.torres.lucas.core.model.bo.User
import org.junit.Test

/**
 * @author Lucas T. Mezzari
 * @since 01/06/2023
 */
internal class UserEntityTest {
    // ----------------------- asModel

    @Test
    fun `Test Valid UserEntity To Valid User Entry`() {
        val id = "ID"
        val name = "Name"
        val userName = "User Name"
        val sub = UserEntity(id, name, userName)
        val model = sub.asEntry()
        assertEquals(sub.id, model.id)
        assertEquals(sub.name, model.name)
        assertEquals(sub.username, model.username)
    }
    // ----------------------- asEntity

    @Test
    fun `Test Valid User To Valid UserEntity`() {
        val id = "ID"
        val name = "Name"
        val userName = "User Name"
        val sub = User(id, name, userName)
        val entity = sub.asEntity()
        assertNotNull(entity)
        assertEquals(sub.id, entity?.id)
        assertEquals(sub.name, entity?.name)
        assertEquals(sub.username, entity?.username)
    }

    @Test
    fun `Test User With No Id Should Return A Null Entity`() {
        val id = null
        val name = "Name"
        val userName = "User Name"
        val sub = User(id, name, userName)
        val entity = sub.asEntity()
        assertNull(entity)
    }

    @Test
    fun `Test User With No Name Should Return A Null Entity`() {
        val id = "ID"
        val name = null
        val userName = "User Name"
        val sub = User(id, name, userName)
        val entity = sub.asEntity()
        assertNull(entity)
    }

    @Test
    fun `Test Repository With No User Name Should Return A Valid Entity`() {
        val id = "ID"
        val name = "Name"
        val userName = null
        val sub = User(id, name, userName)
        val entity = sub.asEntity()
        assertNull(entity)
    }
}