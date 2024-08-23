package mezzari.torres.lucas.commons.archive

import android.content.res.Resources
import android.util.TypedValue

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
fun Float.toPx(resources: Resources): Int {
    val r: Resources = resources
    val px: Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        r.displayMetrics
    )
    return px.toInt()
}