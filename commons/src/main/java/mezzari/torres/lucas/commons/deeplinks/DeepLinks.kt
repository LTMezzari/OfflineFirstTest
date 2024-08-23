package mezzari.torres.lucas.commons.deeplinks

import mezzari.torres.lucas.android.navigation.NavigationManager

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
enum class DeepLinks(override val url: String): NavigationManager.DeepLinkUrl {
    //User Repositories
    REPOSITORY_SEARCH_FRAGMENT("feature-user-repositories://mezzari.torres.lucas/search_fragment"),
    REPOSITORY_LIST_FRAGMENT("feature-user-repositories://mezzari.torres.lucas/repositories_fragment"),

    //Viacep
    ADDRESS_SEARCH_FRAGMENT("feature-viacep://mezzari.torres.lucas/search_address_fragment"),
    ADDRESS_DETAIL_FRAGMENT("feature-viacep://mezzari.torres.lucas/addressDetailFragment"),
}