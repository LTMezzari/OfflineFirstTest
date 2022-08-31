package mezzari.torres.lucas.offlinefirst.generic

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
abstract class BaseFragment : Fragment() {
    val navController: NavController get() = findNavController()
}