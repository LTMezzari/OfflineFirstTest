package mezzari.torres.lucas.offlinefirst.di

import android.app.Application
import mezzari.torres.lucas.core.di.coreModule
import mezzari.torres.lucas.database.di.getDatabaseModule
import mezzari.torres.lucas.network.di.networkModule
import mezzari.torres.lucas.user_repositories.di.userRepositoriesModule
import mezzari.torres.lucas.android.di.getAndroidModule
import org.koin.core.module.Module

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
fun getModules(application: Application): List<Module> {
    return listOf(
        coreModule,
        getDatabaseModule(application),
        networkModule,
        getAndroidModule(application),
        userRepositoriesModule
    )
}