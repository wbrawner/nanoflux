package com.wbrawner.nanoflux.network

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import timber.log.Timber

const val PREF_KEY_BASE_URL = "BASE_URL"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideMinifluxApiService(
        sharedPreferences: SharedPreferences,
        logger: Timber.Tree
    ): MinifluxApiService =
        KtorMinifluxApiService(sharedPreferences, logger)
}
