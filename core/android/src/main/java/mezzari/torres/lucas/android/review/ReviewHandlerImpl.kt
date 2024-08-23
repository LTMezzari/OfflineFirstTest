package mezzari.torres.lucas.android.review

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.play.core.review.ReviewManagerFactory
import mezzari.torres.lucas.android.R
import mezzari.torres.lucas.android.logger.AppLogger
import mezzari.torres.lucas.android.signaler.EventSignaler

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
class ReviewHandlerImpl(
    private val logger: AppLogger,
//    private val preferences: AppPreferences,
    private val signaler: EventSignaler
): ReviewHandler {
    override fun shouldRequestReview(): Boolean {
//        val lastReviewDate = preferences.lastReviewDate
//        val hasAskedReview = preferences.hasAskedReview
//        val nextReview = if (hasAskedReview) {
//            lastReviewDate.plusMonths(6)
//        } else {
//            lastReviewDate.plusDays(3)
//        }
//        val now = DateTime.now().withTimeAtStartOfDay()
//        if (lastReviewDate.isEqual(now)) {
//            preferences.lastReviewDate = now
//        }
//        return now.isEqual(nextReview) || now.isAfter(nextReview)
        return false
    }

    override fun requestReview(activity: FragmentActivity) {
        val reviewManager = ReviewManagerFactory.create(activity)
        val request = reviewManager.requestReviewFlow()

        try {
            request.addOnFailureListener { error ->
                dispatchError(activity, error)
            }

            request.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    dispatchError(activity, null)
                    return@addOnCompleteListener
                }

//                preferences.hasAskedReview = true
//                preferences.lastReviewDate = DateTime.now()

                if (activity.isDestroyed || activity.isFinishing) {
                    return@addOnCompleteListener
                }

                reviewManager.launchReviewFlow(activity, task.result)
            }
        } catch (e: Exception) {
            dispatchError(activity, e)
        }
    }

    private fun dispatchError(activity: FragmentActivity, exception: Exception?) {
        logger.logError(exception)
        signaler.dispatchEvent(REVIEW_ERROR_EVENT, Bundle().apply {
            putString(EventSignaler.KEY_ERROR, activity.getString(R.string.message_failed_review))
        })
    }

    companion object {
        const val REVIEW_ERROR_EVENT = "ReviewHandlerImpl::REVIEW_ERROR_EVENT"
    }
}