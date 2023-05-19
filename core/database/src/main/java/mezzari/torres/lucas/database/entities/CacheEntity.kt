package mezzari.torres.lucas.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import mezzari.torres.lucas.core.model.bo.Cache

/**
 * @author Lucas T. Mezzari
 * @since 02/09/2022
 */
@Entity
class CacheEntity(
    @PrimaryKey
    val id: String,
    val response: String
)

fun Cache.asEntity(): CacheEntity {
    return CacheEntity(
        id,
        response
    )
}

fun CacheEntity.asEntry(): Cache {
    return Cache(
        id = id,
        response = response
    )
}