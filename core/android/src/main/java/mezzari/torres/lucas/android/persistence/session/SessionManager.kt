package mezzari.torres.lucas.android.persistence.session

import mezzari.torres.lucas.core.model.bo.User

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
interface SessionManager {
    var user: User?
}