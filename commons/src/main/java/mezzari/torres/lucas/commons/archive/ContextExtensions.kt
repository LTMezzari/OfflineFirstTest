package mezzari.torres.lucas.commons.archive

import android.content.Context
import android.os.IBinder
import android.view.inputmethod.InputMethodManager

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
fun Context.showKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun Context.hideKeyboard(windowToken: IBinder) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}