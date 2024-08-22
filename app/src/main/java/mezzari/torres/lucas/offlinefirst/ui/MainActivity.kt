package mezzari.torres.lucas.offlinefirst.ui

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import mezzari.torres.lucas.offlinefirst.R
import mezzari.torres.lucas.offlinefirst.databinding.ActivityMainBinding
import mezzari.torres.lucas.android.generic.BaseActivity

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class MainActivity : BaseActivity() {
    override val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.fgv_nav_host) as NavHostFragment).navController
    }

    override val toolbar: Toolbar get() = binding.toolbar

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.toolbar.setupWithNavController(
            navController,
            AppBarConfiguration(
                setOf(
                    R.id.splashFragment,
                    mezzari.torres.lucas.feature.user_repositories.R.id.searchFragment,
                    mezzari.torres.lucas.feature.viacep.R.id.searchAddressFragment
                )
            )
        )
        binding.bnvNavigation.setupWithNavController(navController)
    }
}