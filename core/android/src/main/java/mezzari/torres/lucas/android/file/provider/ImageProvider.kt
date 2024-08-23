package mezzari.torres.lucas.android.file.provider

import android.Manifest
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mezzari.torres.lucas.android.R
import mezzari.torres.lucas.android.file.FileProvider
import mezzari.torres.lucas.android.logger.AppLogger
import mezzari.torres.lucas.android.permissions.PermissionsManager
import mezzari.torres.lucas.core.archive.elvis
import mezzari.torres.lucas.core.archive.guard
import mezzari.torres.lucas.core.interfaces.AppDispatcher
import org.koin.android.ext.android.inject
import java.io.File

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
class ImageProvider(
    private val dispatcher: AppDispatcher,
    private val logger: AppLogger,
    private val permissionsManager: PermissionsManager,
) : FileProvider<Bitmap> {

    private val requiredPermissions by lazy {
        val permissions = mutableListOf(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissions
    }

    override suspend fun fetchFile(activity: FragmentActivity, callback: (Bitmap?) -> Unit) {
        fetchFile(activity, emptyMap(), callback)
    }

    override suspend fun fetchFile(
        activity: FragmentActivity,
        params: Map<String, Any>,
        callback: (Bitmap?) -> Unit
    ) {
        coroutineScope {
            launch(dispatcher.main) {
                requestPermissions(activity) { hasPermissions ->
                    if (!hasPermissions) {
                        callback.invoke(null)
                        return@requestPermissions
                    }

                    activity.supportFragmentManager.apply {
                        try {
                            saveBackStack(IMAGE_REQUEST)
                            val pickerFragment = PickerFragment()
                            pickerFragment.logger = logger
                            pickerFragment.permissionsManager = permissionsManager
                            pickerFragment.params = params
                            pickerFragment.permissions = requiredPermissions
                            pickerFragment.callback = { fragment, bitmap ->
                                try {
                                    callback.invoke(bitmap)
                                    beginTransaction().hide(fragment).remove(fragment).commit()
                                    restoreBackStack(IMAGE_REQUEST)
                                } catch (e: Exception) {
                                    logger.logError(e)
                                    logger.recordError(e)
                                    restoreBackStack(IMAGE_REQUEST)
                                }
                            }
                            beginTransaction().add(pickerFragment, IMAGE_REQUEST)
                                .show(pickerFragment).commit()
                        } catch (e: Exception) {
                            logger.logError(e)
                            logger.recordError(e)
                            restoreBackStack(IMAGE_REQUEST)
                        }
                    }
                }
            }
        }
    }

    private suspend fun requestPermissions(
        activity: FragmentActivity,
        callback: (Boolean) -> Unit
    ) {
        if (permissionsManager.checkPermissions(activity, *requiredPermissions.toTypedArray())) {
            callback(true)
            return
        }

        permissionsManager.requestPermissions(activity, requiredPermissions) request@{
            var hasAllPermissions = true
            for ((_, isPermitted) in it.entries) {
                hasAllPermissions = hasAllPermissions && isPermitted
            }
            if (!hasAllPermissions) {
                notifyPermissionMissing(activity)
                callback(false)
                return@request
            }
            callback(true)
        }
    }

    private fun notifyPermissionMissing(activity: FragmentActivity) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.label_attention)
            .setMessage(R.string.message_image_permission_not_granted)
            .setPositiveButton(R.string.label_ok, null)
            .show()
    }

    class PickerFragment : Fragment() {
        private val dispatcher: AppDispatcher by inject()
        var params: Map<String, Any> = mapOf()
        var permissions: List<String> = listOf()
        var callback: ((PickerFragment, Bitmap?) -> Unit)? = null
        var logger: AppLogger? = null
        var permissionsManager: PermissionsManager? = null

        private val cameraUri: Uri by lazy {
            val tmpFile = File.createTempFile("tmp", ".jpg", context?.cacheDir)
            androidx.core.content.FileProvider.getUriForFile(
                requireContext(), "mezzari.torres.lucas.core.android.files", tmpFile
            )
        }

        private lateinit var cameraImageContract: ActivityResultLauncher<Uri>
        private lateinit var galleryImageContract: ActivityResultLauncher<PickVisualMediaRequest>

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
            galleryImageContract = registerForActivityResult(
                ActivityResultContracts.PickVisualMedia()
            ) { result ->
                dispatchCallback(result)
            }
            cameraImageContract =
                registerForActivityResult(ActivityResultContracts.TakePicture()) { hasFile ->
                    if (!hasFile) {
                        dispatchCallback(null)
                        return@registerForActivityResult
                    }
                    dispatchCallback(cameraUri)
                }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val (context) = guard(context) elvis {
                return
            }

            if (permissionsManager
                    ?.checkPermissions(context, * permissions.toTypedArray()) == false
            ) {
                return
            }

            when {
                params["isCameraOnly"] == true -> cameraImageContract.launch(cameraUri)
                params["isGalleryOnly"] == true -> cameraImageContract.launch(cameraUri)
                else -> showDialog()
            }
        }

        private fun showDialog() {
            AlertDialog.Builder(requireContext()).apply {
                setTitle(R.string.title_image_picker)
                setItems(R.array.array_image_picker) { _, witch ->
                    when (witch) {
                        0 -> cameraImageContract.launch(cameraUri)
                        1 -> galleryImageContract.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                }
                setOnCancelListener {
                    dispatchCallback(null)
                }
            }.show()
        }

        private fun dispatchCallback(uri: Uri?) {
            lifecycleScope.launch(dispatcher.io) {
                val bitmap = decodeBitmap(uri)
                lifecycleScope.launch(dispatcher.main) {
                    callback?.invoke(this@PickerFragment, bitmap)
                }
            }
        }

        private fun decodeBitmap(uri: Uri?): Bitmap? {
            return try {
                uri?.let {
                    context?.let {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val source = ImageDecoder.createSource(
                                it.contentResolver,
                                uri
                            )
                            val drawable = ImageDecoder.decodeDrawable(source) { decoder, _, _ ->
                                decoder.setTargetSampleSize(2)
                            }

                            drawable.toBitmap()
                        } else {
                            MediaStore.Images.Media.getBitmap(it.contentResolver, uri)
                        }
                    }
                }
            } catch (e: Exception) {
                logger?.logError(e)
                null
            }
        }
    }

    companion object {
        const val IMAGE_REQUEST = "requesting_images"

        const val IS_CAMERA_ONLY = "isCameraOnly"
        const val IS_GALLERY_ONLY = "isGalleryOnly"
    }
}