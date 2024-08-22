package mezzari.torres.lucas.offlinefirst.di

import android.app.Application
import mezzari.torres.lucas.data.viacep.di.viacepDataModule
import mezzari.torres.lucas.core.di.coreModule
import mezzari.torres.lucas.database.di.getDatabaseModule
import mezzari.torres.lucas.network.di.networkModule
import mezzari.torres.lucas.feature.user_repositories.di.userRepositoriesFeatureModule
import mezzari.torres.lucas.android.di.getAndroidModule
import mezzari.torres.lucas.data.user_repositories.di.userRepositoriesDataModule
import mezzari.torres.lucas.feature.viacep.di.viacepFeatureModule
import org.koin.core.module.Module

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
fun getModules(application: Application): List<Module> {
    return listOf(
        //Core
        coreModule,
        getDatabaseModule(application),
        networkModule,
        getAndroidModule(application),

        // Data
        userRepositoriesDataModule,
        viacepDataModule,

        // Features
        userRepositoriesFeatureModule,
        viacepFeatureModule
    )
}