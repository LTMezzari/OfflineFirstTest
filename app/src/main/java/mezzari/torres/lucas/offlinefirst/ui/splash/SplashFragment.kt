package mezzari.torres.lucas.offlinefirst.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mezzari.torres.lucas.android.generic.BaseFragment
import mezzari.torres.lucas.core.interfaces.AppDispatcher
import mezzari.torres.lucas.offlinefirst.R
import mezzari.torres.lucas.offlinefirst.databinding.FragmentSplashBinding
import org.koin.android.ext.android.inject

/**
 * @author Lucas T. Mezzari
 * @since 30/05/2023
 */
class SplashFragment: BaseFragment() {

    private lateinit var binding: FragmentSplashBinding
    private val dispatcher: AppDispatcher by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSplashBinding.inflate(inflater, container, false).let {
            binding = it
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(dispatcher.io) {
            delay(2000)
            launch(dispatcher.main) {
                // Move to next page
                navigateTo(R.id.action_splashFragment_to_navigation_user_repositories)
            }
        }
    }
}