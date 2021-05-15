package com.wbrawner.nanoflux.di

import android.content.SharedPreferences
import androidx.preference.Preference
import androidx.preference.PreferenceDataStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.wbrawner.nanoflux.BuildConfig
import com.wbrawner.nanoflux.data.MinifluxApiService
import com.wbrawner.nanoflux.data.repository.PREF_KEY_AUTH_TOKEN
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

const val PREF_KEY_BASE_URL = "BASE_URL"
private const val DEFAULT_BASE_URL = "https://base_url"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun providesOkHttpClient(prefs: SharedPreferences): OkHttpClient {
        val client = OkHttpClient.Builder()
            .addInterceptor {
                val old = it.request()
                val newUrl = prefs.getString(PREF_KEY_BASE_URL, null)
                if (old.url.host != "base_url" || newUrl == null) {
                    return@addInterceptor it.proceed(it.request())
                }
                val new = old.newBuilder()
                val url = old.url.toString().replace(DEFAULT_BASE_URL, newUrl).toHttpUrl()
                new.header("Authorization", prefs.getString(PREF_KEY_AUTH_TOKEN, null) ?: "")
                it.proceed(new.url(url).build())
            }
        if (BuildConfig.DEBUG) {
            client.addInterceptor(HttpLoggingInterceptor(logger = HttpLoggingInterceptor.Logger.DEFAULT).also {
                it.setLevel(HttpLoggingInterceptor.Level.BODY)
            })
        }
        return client.build()
    }

    @Provides
    fun providesMoshi(): Moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .build()

    @Provides
    fun providesRetrofit(moshi: Moshi, okHttpClient: OkHttpClient) = Retrofit.Builder()
        .baseUrl(DEFAULT_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    fun provideMinifluxApiService(retrofit: Retrofit) =
        retrofit.create(MinifluxApiService::class.java)
}
