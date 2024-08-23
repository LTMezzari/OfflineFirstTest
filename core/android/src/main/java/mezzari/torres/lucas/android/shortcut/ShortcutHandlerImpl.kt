package mezzari.torres.lucas.android.shortcut

import android.app.Application
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
class ShortcutHandlerImpl(
    private val application: Application
): ShortcutHandler {
    private val shortcutManager: ShortcutManager? by lazy {
        if (!canUseShortcuts()) {
            return@lazy null
        }
        application.getSystemService(ShortcutManager::class.java)
    }
    override fun registerShortcut(
        shortcut: ShortcutHandler.ShortcutData,
        completion: (Intent.() -> Unit)?
    ): Boolean {
        if (!canUseShortcuts()) {
            return false
        }

        if (shortcutManager?.dynamicShortcuts?.firstOrNull { it.id == shortcut.id } != null) {
            return false
        }

        val shortcutInfo = createShortcut(shortcut)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            shortcutManager?.pushDynamicShortcut(shortcutInfo)
        } else {
            val currentShortcuts = ArrayList(shortcutManager?.dynamicShortcuts ?: arrayListOf())
            currentShortcuts += shortcutInfo
            shortcutManager?.dynamicShortcuts = currentShortcuts
        }
        return true
    }

    override fun removeAllShortcuts(): Boolean {
        if (!canUseShortcuts()) {
            return false
        }

        shortcutManager?.removeAllDynamicShortcuts()
        return true
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N_MR1)
    override fun canUseShortcuts(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun createShortcut(
        shortcut: ShortcutHandler.ShortcutData,
        completion: (Intent.() -> Unit)? = null
    ): ShortcutInfo {
        return ShortcutInfo.Builder(application, shortcut.id)
            .setShortLabel(shortcut.shortLabel)
            .setLongLabel(shortcut.longLabel)
            .setIcon(Icon.createWithResource(application, shortcut.icon))
            .setIntent(
                Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = "offline-first://mezzari.torres.lucas".toUri()
                    flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    putExtra("isFromShortcut", true)
                    putExtra("destination", shortcut.url)
                    completion?.invoke(this)
                }
            )
            .build()
    }
}