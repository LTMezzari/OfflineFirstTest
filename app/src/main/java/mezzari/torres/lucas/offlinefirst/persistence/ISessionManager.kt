package mezzari.torres.lucas.offlinefirst.persistence

import mezzari.torres.lucas.offlinefirst.model.User

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
interface ISessionManager {
    var user: User?
}