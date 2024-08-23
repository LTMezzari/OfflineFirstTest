package mezzari.torres.lucas.android.permissions

import android.content.Context
import androidx.fragment.app.FragmentActivity

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
interface PermissionsManager {
    suspend fun requestPermissions(activity: FragmentActivity, permissions: List<String>, callback: ((Map<String, Boolean>) -> Unit)? = null)

    fun checkPermissions(context: Context, vararg permissions: String): Boolean
}