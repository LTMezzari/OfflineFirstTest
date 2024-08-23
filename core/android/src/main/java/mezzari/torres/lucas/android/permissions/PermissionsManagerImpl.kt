package mezzari.torres.lucas.android.permissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mezzari.torres.lucas.android.logger.AppLogger
import mezzari.torres.lucas.core.interfaces.AppDispatcher
import org.koin.android.ext.android.inject

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
class PermissionsManagerImpl(
    private val dispatcher: AppDispatcher,
    private val logger: AppLogger
) : PermissionsManager {
    override suspend fun requestPermissions(
        activity: FragmentActivity,
        permissions: List<String>,
        callback: ((Map<String, Boolean>) -> Unit)?
    ) {
        coroutineScope {
            launch(dispatcher.io) {
                val pendingPermissions = permissions.filter {
                    ContextCompat.checkSelfPermission(
                        activity,
                        it
                    ) != PackageManager.PERMISSION_GRANTED
                }
                if (pendingPermissions.isEmpty())
                    return@launch
                launch(dispatcher.main) {
                    activity.supportFragmentManager.apply {
                        try {
                            saveBackStack(PERMISSION_REQUEST)
                            val permissionsFragment = PermissionsFragment()
                            permissionsFragment.callback = { fragment, map ->
                                try {
                                    callback?.invoke(map)
                                    beginTransaction().hide(fragment).remove(fragment).commit()
                                    restoreBackStack(PERMISSION_REQUEST)
                                } catch (e: Exception) {
                                    logger.logError(e)
                                    logger.recordError(e)
                                    restoreBackStack(PERMISSION_REQUEST)
                                }
                            }
                            permissionsFragment.requestedPermissions = pendingPermissions
                            beginTransaction().add(permissionsFragment, PERMISSION_REQUEST)
                                .show(permissionsFragment).commit()
                        } catch (e: Exception) {
                            logger.logError(e)
                            logger.recordError(e)
                            restoreBackStack(PERMISSION_REQUEST)
                        }
                    }
                }
            }
        }
    }

    override fun checkPermissions(context: Context, vararg permissions: String): Boolean {
        var hasAllPermissions = true
        permissions.forEach {
            hasAllPermissions = hasAllPermissions
                    && ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
        return hasAllPermissions
    }

    class PermissionsFragment : Fragment() {
        private val dispatcher: AppDispatcher by inject()

        var requestedPermissions: List<String> = arrayListOf()
        var callback: ((PermissionsFragment, Map<String, Boolean>) -> Unit)? = null

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            return View(requireActivity()).apply {
                setBackgroundColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        android.R.color.transparent
                    )
                )
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            requestPermissions()
        }

        private fun requestPermissions() {
            val isSinglePermission = requestedPermissions.size == 1
            if (!isSinglePermission) {
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
                    dispatchCallback(map)
                }.launch(requestedPermissions.toTypedArray())
                return
            }

            val permission = requestedPermissions.first()
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                dispatchCallback(
                    mapOf(
                        permission to isGranted
                    )
                )
            }.launch(permission)
        }

        private fun dispatchCallback(map: Map<String, Boolean>) {
            if (map.size != requestedPermissions.size) {
                return
            }
            lifecycleScope.launch(dispatcher.main) {
                callback?.invoke(this@PermissionsFragment, map)
            }
        }
    }

    companion object {
        const val PERMISSION_REQUEST = "requesting_permission"
    }
}