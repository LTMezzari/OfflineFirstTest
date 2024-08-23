package mezzari.torres.lucas.android.interfaces

import androidx.annotation.StringRes

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
interface MessagePresenter {
    fun showMessage(@StringRes messageId: Int?, duration: Int, type: Type)

    fun showMessage(message: String?, duration: Int, type: Type)

    enum class Type {
        SUCCESS,
        WARNING,
        ERROR
    }
}