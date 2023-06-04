package mezzari.torres.lucas.network.archive

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * @author Lucas T. Mezzari
 * @since 03/06/2023
 */

/**
 * Parses the object to a String JSON.
 *
 * When the object is null returns a null string.
 */
fun Any?.toJson(): String {
    return Gson().toJson(this)
}

inline fun <reified T>fromJson(json: String?): T? {
    val type = object : TypeToken<T>() {}.type
    return fromJson(json, type)
}

fun <T>fromJson(json: String?, type: Type): T? {
    if (json == null || json.trim().isEmpty())
        return null
    return Gson().fromJson(json, type) as? T
}