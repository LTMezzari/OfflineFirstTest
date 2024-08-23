package mezzari.torres.lucas.android.shortcut

import android.content.Intent
import androidx.annotation.DrawableRes

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
interface ShortcutHandler {
    fun registerShortcut(shortcut: ShortcutData, completion: (Intent.() -> Unit)? = null): Boolean

    fun removeAllShortcuts(): Boolean

    fun canUseShortcuts(): Boolean

    data class ShortcutData(
        val id: String,
        val shortLabel: String,
        val longLabel: String,
        @DrawableRes val icon: Int,
        val url: String? = null,
    )
}