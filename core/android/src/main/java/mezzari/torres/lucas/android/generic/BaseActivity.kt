package mezzari.torres.lucas.android.generic

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
abstract class BaseActivity : AppCompatActivity() {
    abstract val navController: NavController?
    abstract val toolbar: Toolbar?

    override fun onStart() {
        super.onStart()
        setSupportActionBar(toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}