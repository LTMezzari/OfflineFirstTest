package mezzari.torres.lucas.android.generic

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
abstract class BaseActivity : FragmentActivity() {
    abstract val navController: NavController?

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}