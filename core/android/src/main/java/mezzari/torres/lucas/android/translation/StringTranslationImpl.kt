package mezzari.torres.lucas.android.translation

import android.app.Application
import mezzari.torres.lucas.android.R

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
class StringTranslationImpl(private val application: Application) : StringTranslation {
    override fun getString(stringId: Int): String {
        return application.getString(stringId)
    }

    override fun getString(stringId: Int, vararg objects: Any): String {
        return application.getString(stringId, *objects)
    }

    override fun getString(string: String): String? {
        when {
            string.contains("Unable to resolve host") || string.contains("unknown error") -> {
                return application.getString(R.string.message_connection_error)
            }
        }
        return string
    }
}