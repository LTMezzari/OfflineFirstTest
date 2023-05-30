package mezzari.torres.lucas.android.generic

import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
abstract class BaseActivity : FragmentActivity() {
    abstract val navController: NavController?
}