package mezzari.torres.lucas.network.archive

import android.content.Context
import androidx.annotation.RawRes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.lang.ClassCastException
import kotlin.reflect.KClass

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
inline fun <reified T: Any> parseResponse(response: Any?) = parseResponse(response, T::class)

fun <T: Any> parseResponse(response: Any?, kClass: KClass<T>): T? {
    if (response == null)
        return null

    try {
        val jClass = kClass.java
        val gson = GsonBuilder().create()

        val target: Any? = if (response is Array<*> && response.size == 1) {
            response[0]
        } else response

        if (target is LinkedTreeMap<*, *> && LinkedTreeMap::class.java != jClass)
            return gson.fromJson(Gson().toJson(target), jClass)

        if (target is JSONObject && JSONObject::class.java != jClass)
            return gson.fromJson(target.toString(), jClass)

        return target as? T
    } catch (e: ClassCastException) {
        return null
    } catch (e: JsonParseException) {
        return null
    }
}

inline fun <reified T> readJsonResource(context: Context, @RawRes rawResId: Int): T? {
    try {
        context.resources.openRawResource(rawResId).bufferedReader().use {
            return Gson().fromJson(it, object: TypeToken<T>() {}.type)
        }
    } catch (e: Exception) {
        return null
    }
}