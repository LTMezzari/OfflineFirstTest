package mezzari.torres.lucas.android.file

import androidx.fragment.app.FragmentActivity

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
interface FileProvider<T> {
    suspend fun fetchFile(activity: FragmentActivity, callback: (T?) -> Unit)
    suspend fun fetchFile(activity: FragmentActivity, params: Map<String, Any>, callback: (T?) -> Unit)
}