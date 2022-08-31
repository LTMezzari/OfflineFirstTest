package mezzari.torres.lucas.offlinefirst.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import mezzari.torres.lucas.offlinefirst.model.User

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
@Entity
class UserEntity(
    @PrimaryKey
    var id: String,
    var name: String,
    var username: String,
)

fun User.asEntity(): UserEntity? {
    val id = id ?: return null
    val name = name ?: return null
    val username = username ?: return null

    return UserEntity(
        id,
        name,
        username
    )
}

fun UserEntity.asEntry(): User {
    return User(
        id = id,
        name = name,
        username = username
    )
}