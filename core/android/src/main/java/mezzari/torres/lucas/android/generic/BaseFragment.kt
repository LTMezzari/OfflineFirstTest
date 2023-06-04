package mezzari.torres.lucas.android.generic

import android.net.Uri
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import mezzari.torres.lucas.android.archive.logError
import mezzari.torres.lucas.network.archive.fromJson
import mezzari.torres.lucas.network.archive.toJson
import java.lang.Exception

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
abstract class BaseFragment : Fragment() {
    protected val navController: NavController get() = findNavController()

    protected fun navigate(@IdRes actionId: Int, bundle: Bundle? = null) {
        try {
            navController.navigate(actionId, bundle)
        } catch (e: Exception) {
            logError(e)
        }
    }

    protected fun navigate(request: NavDeepLinkRequest, navOptions: NavOptions? = null) {
        try {
            navController.navigate(request, navOptions)
        } catch (e: Exception) {
            logError(e)
        }
    }

    protected fun navigateToLink(link: String, navOptions: NavOptions? = null) {
        try {
            val uri = link.toUri()
            val request = NavDeepLinkRequest.Builder.fromUri(uri).build()
            navigate(request, navOptions)
        } catch (e: Exception) {
            logError(e)
        }
    }

    protected fun navigateToLink(link: String, arguments: Map<String, Any> = mapOf(), navOptions: NavOptions? = null) {
        try {
            val uriBuilder = Uri.Builder().encodedPath(link)
            for ((key, argument) in arguments) {
                uriBuilder.appendQueryParameter(key, argument.toJson())
            }
            val request = NavDeepLinkRequest.Builder.fromUri(uriBuilder.build()).build()
            navigate(request, navOptions)
        } catch (e: Exception) {
            logError(e)
        }
    }

    protected inline fun <reified T>findArgument(key: String): T? {
        return when {
            arguments?.containsKey(key) == true -> {
                val argument = arguments?.get(key) ?: return null
                if (!T::class.java.isAssignableFrom(argument::class.java)) {
                    return fromJson(arguments?.get(key) as? String)
                }

                arguments?.get(key) as? T
            }
            arguments?.containsKey("${key}_json") == true -> fromJson(arguments?.get(key) as? String)
            else -> null
        }
    }
}