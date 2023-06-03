package mezzari.torres.lucas.android.generic

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import mezzari.torres.lucas.android.archive.logError
import java.lang.Exception

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
abstract class BaseFragment : Fragment() {
    val navController: NavController get() = findNavController()

    fun navigate(@IdRes actionId: Int, bundle: Bundle? = null) {
        try {
            navController.navigate(actionId, bundle)
        } catch (e: Exception) {
            logError(e)
        }
    }

    fun navigate(request: NavDeepLinkRequest, navOptions: NavOptions? = null) {
        try {
            navController.navigate(request, navOptions)
        } catch (e: Exception) {
            logError(e)
        }
    }

    fun navigateToLink(link: String, navOptions: NavOptions? = null) {
        try {
            val uri = link.toUri()
            val request = NavDeepLinkRequest.Builder.fromUri(uri).build()
            navigate(request, navOptions)
        } catch (e: Exception) {
            logError(e)
        }
    }
}