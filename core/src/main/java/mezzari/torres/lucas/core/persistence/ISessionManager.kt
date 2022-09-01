package mezzari.torres.lucas.core.persistence

import mezzari.torres.lucas.core.model.User

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
interface ISessionManager {
    var user: User?
}