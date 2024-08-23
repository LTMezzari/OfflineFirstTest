package mezzari.torres.lucas.android.generic

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import mezzari.torres.lucas.android.interfaces.MessagePresenter
import mezzari.torres.lucas.android.logger.AppLogger
import mezzari.torres.lucas.android.navigation.NavigationManager
import mezzari.torres.lucas.core.archive.elvis
import mezzari.torres.lucas.network.archive.fromJson
import org.koin.android.ext.android.inject

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
abstract class BaseFragment : Fragment() {

    protected val logger: AppLogger by inject()

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitializeViews()
        onBindViews()
        onAddListeners()
        onAddObservables()
    }

    open fun onBindViews() {}

    open fun onInitializeViews() {}

    open fun onAddListeners() {}

    open fun onAddObservables() {}

    open fun showMessage(
        @StringRes messageId: Int?,
        duration: Int = Snackbar.LENGTH_LONG,
        type: MessagePresenter.Type = MessagePresenter.Type.ERROR
    ) {
        val message = messageId ?: return
        if (context == null || this@BaseFragment.isDetached) {
            return
        }
        showMessage(getString(message), duration, type)
    }

    open fun showMessage(
        message: String?,
        duration: Int = Snackbar.LENGTH_LONG,
        type: MessagePresenter.Type = MessagePresenter.Type.ERROR
    ) {
        val text = message ?: return
        val presenter = activity as? MessagePresenter elvis {
            logger.logMessage(text)
            return
        }
        presenter.showMessage(text, duration, type)
    }

    fun navigateTo(
        link: String?,
        arguments: Map<String, Any> = mapOf()
    ) {
        if (link == null || context == null) {
            return
        }
        NavigationManager.of(this).withArguments(arguments).navigateTo(link)
    }

    fun navigateTo(direction: Int?, extras: Bundle? = null) {
        if (direction == null || context == null) {
            return
        }
        NavigationManager.of(this).withBundle(extras).navigateTo(direction)
    }

    fun navigateBack() {
        if (context == null) {
            return
        }
        NavigationManager.of(this).navigateUp()
    }

    protected inline fun <reified T> findArgument(key: String): T? {
        return when {
            arguments?.containsKey(key) == true -> {
                val argument = arguments?.get(key) ?: return null
                if (argument is T) {
                    if (argument is String) {
                        return argument.removePrefix("\"").removeSuffix("\"") as? T
                    }

                    return argument
                }

                if (!T::class.java.isAssignableFrom(argument::class.java)) {
                    return fromJson(argument as? String)
                }

                argument as? T
            }

            else -> null
        }
    }
}