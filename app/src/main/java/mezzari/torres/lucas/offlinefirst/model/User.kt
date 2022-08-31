package mezzari.torres.lucas.offlinefirst.model

import com.google.gson.annotations.SerializedName

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
data class User(
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("login")
    var username: String? = null,

    @SerializedName("company")
    var company: String? = null,

    @SerializedName("location")
    var location: String? = null,

    @SerializedName("bio")
    var bio: String? = null,

    @SerializedName("followers")
    var followers: Int = 0,

    @SerializedName("following")
    var following: Int = 0,

    @SerializedName("created_at")
    var enteredAt: String? = null,

    @SerializedName("avatar_url")
    var profileImageSrc: String? = null,

    @SerializedName("html_url")
    var originUrl: String? = null,
)