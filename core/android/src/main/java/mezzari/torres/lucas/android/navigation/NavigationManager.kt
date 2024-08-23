package mezzari.torres.lucas.android.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDirections
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import mezzari.torres.lucas.android.logger.AppLogger
import mezzari.torres.lucas.core.archive.elvis
import mezzari.torres.lucas.core.archive.guard
import mezzari.torres.lucas.network.archive.toJson
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
class NavigationManager private constructor(
    private val context: Context,
    private val navController: NavController
) : KoinComponent {

    private val logger: AppLogger by inject()

    // ----------------------- Arguments

    private var arguments: HashMap<String, Any?> = hashMapOf()
    private var bundle: Bundle? = null

    fun withBundle(bundle: Bundle?): NavigationManager {
        this.bundle = bundle
        return this
    }

    fun withArguments(arguments: Map<String, Any?>): NavigationManager {
        this.arguments = HashMap(arguments)
        return this
    }

    fun addArgument(key: String, value: Any?): NavigationManager {
        this.arguments[key] = value
        return this
    }

    fun removeArgument(key: String): NavigationManager {
        arguments.remove(key)
        return this
    }

    fun clearArguments(): NavigationManager {
        arguments.clear()
        return this
    }

    // ----------------------- NavOptions

    private var popUpToId: Int? = null
    private var inclusive: Boolean = false

    fun popTo(@IdRes id: Int, inclusive: Boolean = false): NavigationManager {
        popUpToId = id
        this.inclusive = inclusive
        return this
    }

    fun popBackStack(): NavigationManager {
        var rootGraph: NavGraph = navController.graph
        while (rootGraph.parent != null) {
            rootGraph = rootGraph.parent ?: rootGraph
        }
        popUpToId = rootGraph.id
        inclusive = false
        return this
    }

    // ----------------------- Navigate

    fun navigateUp(): Boolean {
        return tryNavigation {
            navController.navigateUp()
        }
    }

    fun navigateTo(
        link: DeepLinkUrl
    ): Boolean {
        return navigateTo(link.url)
    }

    fun navigateTo(
        link: Uri
    ): Boolean {
        return tryNavigation {
            val newLink = appendArgumentsToUri(link.buildUpon())
            navController.navigate(newLink.build(), buildNavOptions())
            true
        }
    }

    fun navigateTo(
        link: String
    ): Boolean {
        return tryNavigation {
            navController.navigate(buildDeepLinkRequest(link), buildNavOptions())
            true
        }
    }

    fun navigateTo(@IdRes id: Int): Boolean {
        return tryNavigation {
            navController.navigate(id, buildArguments(), buildNavOptions())
            true
        }
    }

    fun navigateTo(direction: NavDirections): Boolean {
        return tryNavigation {
            navController.navigate(direction, buildNavOptions())
            true
        }
    }

    fun navigateTo(kClass: KClass<*>): Boolean {
        return tryNavigation {
            val arguments = buildArguments()
            val intent = Intent(context, kClass.java)
            arguments?.run {
                intent.putExtras(this)
            }
            context.startActivity(intent)
            true
        }
    }

    fun navigateTo(intent: Intent): Boolean {
        return tryNavigation {
            context.startActivity(intent)
            true
        }
    }

    // ----------------------- Methods

    private fun buildDeepLinkRequest(link: String): NavDeepLinkRequest {
        val uriBuilder = Uri.Builder().encodedPath(link)
        appendArgumentsToUri(uriBuilder)
        appendBundleToUri(uriBuilder)
        val uri = uriBuilder.build()
        logger.logMessage(uri.toString())
        return NavDeepLinkRequest.Builder.fromUri(uri).build()
    }

    private fun appendArgumentsToUri(uriBuilder: Uri.Builder): Uri.Builder {
        if (arguments.isEmpty()) {
            return uriBuilder
        }

        for ((key, argument) in arguments) {
            if (argument == null) {
                continue
            }
            uriBuilder.appendQueryParameter(key, argument.toJson())
        }

        return uriBuilder
    }

    private fun appendBundleToUri(uriBuilder: Uri.Builder): Uri.Builder {
        val (bundle) = guard(bundle) elvis {
            return uriBuilder
        }

        if (bundle.isEmpty) {
            return uriBuilder
        }

        for (key in bundle.keySet()) {
            val argument = bundle.get(key)
            uriBuilder.appendQueryParameter(key, argument.toJson())
        }

        return uriBuilder
    }

    private fun buildNavOptions(): NavOptions? {
        val (popId) = guard(popUpToId) elvis {
            return null
        }

        return navOptions {
            popUpTo(popId) {
                this.inclusive = true
            }
        }
    }

    private fun buildArguments(): Bundle? {
        if (bundle != null) {
            return bundle
        }

        if (arguments.isEmpty()) {
            return null
        }

        val bundle = Bundle()
        for ((key, value) in arguments) {
            appendToBundle(bundle, key, value)
        }
        return bundle
    }

    private fun appendToBundle(bundle: Bundle, key: String, value: Any?) {
        if (value == null) {
            return
        }

        when (value) {
            is Int -> bundle.putInt(key, value)
            is Long -> bundle.putLong(key, value)
            is Double -> bundle.putDouble(key, value)
            is String -> bundle.putString(key, value)
            is Float -> bundle.putFloat(key, value)
            is Char -> bundle.putChar(key, value)
            is CharSequence -> bundle.putCharSequence(key, value)
            is Parcelable -> bundle.putParcelable(key, value)
            is Array<*> -> bundle.putParcelableArray(key, value as Array<Parcelable>)
            is ArrayList<*> -> bundle.putParcelableArrayList(key, value as ArrayList<Parcelable>)
            is Serializable -> bundle.putSerializable(key, value)
        }
    }

    private fun tryNavigation(block: () -> Boolean): Boolean {
        return try {
            block()
        } catch (e: Exception) {
            logger.logError(e)
            false
        }
    }

    companion object {
        fun with(context: Context, navController: NavController): NavigationManager {
            return NavigationManager(context, navController)
        }

        fun of(activity: FragmentActivity, @IdRes navHostId: Int): NavigationManager {
            return NavigationManager(activity, activity.findNavController(navHostId))
        }

        fun of(fragment: Fragment): NavigationManager {
            return NavigationManager(fragment.requireContext(), fragment.findNavController())
        }

        fun of(dialog: DialogFragment): NavigationManager {
            return NavigationManager(dialog.requireContext(), dialog.findNavController())
        }

        fun of(view: View): NavigationManager {
            return NavigationManager(view.context, view.findNavController())
        }
    }

    interface DeepLinkUrl {
        val url: String
    }
}