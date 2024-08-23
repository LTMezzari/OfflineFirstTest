package mezzari.torres.lucas.network.archive

import android.content.Context
import android.net.ConnectivityManager

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
fun getConnectivityManager(context: Context): ConnectivityManager? {
    return context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
}