package mezzari.torres.lucas.android.generic

import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import mezzari.torres.lucas.android.logger.AppLogger
import mezzari.torres.lucas.core.interfaces.AppDispatcher
import mezzari.torres.lucas.network.archive.getConnectivityManager
import org.koin.android.ext.android.inject

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
abstract class BaseActivity : AppCompatActivity() {
    protected val dispatcher: AppDispatcher by inject()
    protected val logger: AppLogger by inject()

    abstract val navController: NavController?
    abstract val toolbar: Toolbar?

    open val connectivityCallback: ConnectivityManager.NetworkCallback by lazy {
        object: ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                lifecycleScope.launch (dispatcher.main) {
                    onConnectionAvailable(network)
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                lifecycleScope.launch (dispatcher.main) {
                    onConnectionLost(network)
                }
            }

            override fun onUnavailable() {
                super.onUnavailable()
                lifecycleScope.launch (dispatcher.main) {
                    onConnectionLost(null)
                }
            }
        }
    }

    private var hasConnection: Boolean = false
        set(value) {
            if (field == value) {
                return
            }

            field = value
            lifecycleScope.launch (dispatcher.main) {
                if (field) {
                    onConnectionAvailable(null)
                } else {
                    onConnectionLost(null)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        hasConnection = statusBoard.hasNetworkConnection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getConnectivityManager(this)
                ?.registerDefaultNetworkCallback(connectivityCallback)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            try {
                onBackPressedDispatcher.onBackPressed()
            } catch (e: Exception) {
                logger.logError(e)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    protected open fun onConnectionAvailable(network: Network?) {}

    protected open fun onConnectionLost(network: Network?) {}
}