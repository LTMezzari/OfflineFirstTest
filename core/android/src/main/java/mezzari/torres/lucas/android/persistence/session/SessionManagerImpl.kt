package mezzari.torres.lucas.android.persistence.session

import mezzari.torres.lucas.android.persistence.preferences.PreferencesManager
import mezzari.torres.lucas.core.model.User

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
class SessionManagerImpl(private val preferencesManager: PreferencesManager): SessionManager {
    private var _user: User? = null
    override var user: User?
        get() {
            if (_user == null)
                _user = preferencesManager.user
            return _user
        }
        set(value) {
            _user = value
        }
}