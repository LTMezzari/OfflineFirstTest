package mezzari.torres.lucas.android.review

import androidx.fragment.app.FragmentActivity

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
interface ReviewHandler {
    fun shouldRequestReview(): Boolean

    fun requestReview(activity: FragmentActivity)
}