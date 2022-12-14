package mezzari.torres.lucas.core.archive

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author Lucas T. Mezzari
 * @since 11/11/2022
 */
fun <T>guard(vararg values: T?): List<T>? {
    val list = arrayListOf<T>()
    values.forEach {
        if (it == null)
            return null
        list.add(it)
    }
    return list
}

inline infix fun <T>T?.elvis(elseBlock: () -> Nothing): T {
    if (this == null) {
        elseBlock()
    }
    return this
}

fun <T>unwrap(vararg values: T?): List<T>? {
    val array = arrayListOf<T>()
    values.forEach { value ->
        if (value == null)
            return null
        array += value
    }
    return array
}

infix fun <T> T?.then(executable: (T) -> Unit) {
    if (this != null)
        executable(this)
    return
}

fun <T> lazy(initializer: () -> T): ReadWriteProperty<Any?, T> {
    return object : ReadWriteProperty<Any?, T> {
        var value: T? = null

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val current = value
            return if (current == null) {
                val new = initializer()
                value = new
                new
            } else {
                current
            }
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            this.value = value
        }

    }
}