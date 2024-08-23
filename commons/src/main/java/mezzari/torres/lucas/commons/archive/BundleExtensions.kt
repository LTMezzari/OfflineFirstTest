package mezzari.torres.lucas.commons.archive

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
fun <T : Serializable> Bundle.getSerializableExtra(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getSerializable(key, clazz)
    } else {
        this.getSerializable(key) as? T
    }
}

inline fun <reified T : Serializable> Bundle.getSerializableExtra(key: String): T? {
    return getSerializableExtra(key, T::class.java)
}

fun <T : Parcelable> Bundle.getParcelableExtra(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getParcelable(key, clazz)
    } else {
        this.getParcelable(key)
    }
}

inline fun <reified T : Parcelable> Bundle.getParcelableExtra(key: String): T? {
    return getParcelableExtra(key, T::class.java)
}

fun <T : Parcelable> Bundle.getParcelableArrayListExtra(key: String, clazz: Class<T>): ArrayList<T>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getParcelableArrayList(key, clazz)
    } else {
        this.getParcelableArrayList(key)
    }
}

inline fun <reified T : Parcelable> Bundle.getParcelableArrayListExtra(key: String): ArrayList<T>? {
    return getParcelableArrayListExtra(key, T::class.java)
}