package com.wbrawner.nanoflux.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.wbrawner.nanoflux.storage.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    @Provides
    fun providesSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @Singleton
    fun providesNanofluxDatabase(@ApplicationContext context: Context): NanofluxDatabase =
        Room.databaseBuilder(context, NanofluxDatabase::class.java, "nanoflux")
            .fallbackToDestructiveMigrationFrom(1)
            .build()

    @Provides
    @Singleton
    fun providesCategoryDao(nanofluxDatabase: NanofluxDatabase): CategoryDao = nanofluxDatabase.categoryDao()

    @Provides
    @Singleton
    fun providesEntryDao(nanofluxDatabase: NanofluxDatabase): EntryDao = nanofluxDatabase.entryDao()

    @Provides
    @Singleton
    fun providesFeedDao(nanofluxDatabase: NanofluxDatabase): FeedDao = nanofluxDatabase.feedDao()

    @Provides
    @Singleton
    fun providesIconDao(nanofluxDatabase: NanofluxDatabase): IconDao = nanofluxDatabase.iconDao()

    @Provides
    @Singleton
    fun providesUserDao(nanofluxDatabase: NanofluxDatabase): UserDao = nanofluxDatabase.userDao()
}