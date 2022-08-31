package mezzari.torres.lucas.offlinefirst.persistence

import mezzari.torres.lucas.offlinefirst.model.User

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
class SessionManager : ISessionManager {
    private var _user: User? = null
    override var user: User?
        get() = _user
        set(value) {
            _user = value
        }
}