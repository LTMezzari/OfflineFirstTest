package mezzari.torres.lucas.android.translation

import androidx.annotation.StringRes

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
interface StringTranslation {
    fun getString(@StringRes stringId: Int): String

    fun getString(@StringRes stringId: Int, vararg objects: Any): String

    fun getString(string: String): String?
}