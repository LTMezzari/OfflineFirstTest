package mezzari.torres.lucas.android.persistence.preferences

import android.content.Context
import android.content.SharedPreferences
import mezzari.torres.lucas.core.model.User

/**
 * @author Lucas T. Mezzari
 * @since 05/09/2022
 */
class PreferencesManager(context: Context): IPreferencesManager {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("GITHUB_TEST_APPLICATION", Context.MODE_PRIVATE)

    override var user: User? set(value) {
        sharedPreferences.also {
            val editor = it.edit()

            if (value == null) {
                editor.remove("user_id")
                editor.remove("user_name")
                editor.remove("user_username")
                editor.remove("user_company")
                editor.remove("user_location")
                editor.remove("user_bio")
                editor.remove("user_followers")
                editor.remove("user_following")
                editor.remove("user_created_at")
                editor.remove("user_avatar_url")
                editor.remove("user_html_url")
                return@also
            }

            editor.putString("user_id", value.id)
            editor.putString("user_name", value.name)
            editor.putString("user_username", value.username)
            editor.putString("user_company", value.company)
            editor.putString("user_location", value.location)
            editor.putString("user_bio", value.bio)
            editor.putInt("user_followers", value.followers)
            editor.putInt("user_following", value.following)
            editor.putString("user_created_at", value.enteredAt)
            editor.putString("user_avatar_url", value.profileImageSrc)
            editor.putString("user_html_url", value.originUrl)

            editor.apply()
        }
    } get() {
        sharedPreferences.also {
            if (!it.contains("user_id"))
                return@also

            return User().apply {
                id = it.getString("user_id", null)
                name = it.getString("user_name", null)
                username = it.getString("user_username", null)
                company = it.getString("user_company", null)
                location = it.getString("user_location", null)
                bio = it.getString("user_bio", null)
                followers = it.getInt("user_followers", 0)
                following = it.getInt("user_following", 0)
                enteredAt = it.getString("user_created_at", null)
                profileImageSrc = it.getString("user_avatar_url", null)
                originUrl = it.getString("user_html_url", null)
            }
        }
        return null
    }
}