package mezzari.torres.lucas.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import mezzari.torres.lucas.core.model.Repository

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
@Entity
class RepositoryEntity (
    @PrimaryKey
    var id: String,
    var name: String,
    var fullName: String? = null,
    var description: String,
    var language: String,
    var stars: Int = 0,
    var userId: String,
)

fun Repository.asEntity(): RepositoryEntity? {
    val id = id ?: return null
    val name = name ?: return null
    val fullName = fullName ?: return null
    val description = description ?: return null
    val language = language ?: return null
    val userId = userId ?: return null

    return RepositoryEntity(
        id,
        name,
        fullName,
        description,
        language,
        stars,
        userId,
    )
}

fun RepositoryEntity.asEntry(): Repository {
    return Repository(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        language = language,
        stars = stars,
        userId = userId,
    )
}