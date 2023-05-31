package mezzari.torres.lucas.android.generic

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import mezzari.torres.lucas.android.BuildConfig
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
            if (BuildConfig.DEBUG)
                e.printStackTrace()
        }
    }

    fun navigate(request: NavDeepLinkRequest) {
        try {
            navController.navigate(request)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG)
                e.printStackTrace()
        }
    }
}