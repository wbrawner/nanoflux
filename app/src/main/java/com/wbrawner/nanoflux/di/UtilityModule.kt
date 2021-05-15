package com.wbrawner.nanoflux.di

import com.wbrawner.nanoflux.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
object UtilityModule {
    @Provides
    fun providesLogger(): Timber.Tree {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        return Timber.asTree()
    }
}