package com.wbrawner.nanoflux.network.repository

import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import com.wbrawner.nanoflux.network.MinifluxApiService
import com.wbrawner.nanoflux.network.PREF_KEY_BASE_URL
import com.wbrawner.nanoflux.storage.dao.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

const val PREF_KEY_AUTH_TOKEN = "authToken"
const val PREF_KEY_CURRENT_USER = "currentUser"

class UserRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val apiService: MinifluxApiService,
    private val userDao: UserDao,
    private val logger: Timber.Tree
) {
    private val _currentUser: MutableSharedFlow<com.wbrawner.nanoflux.storage.model.User> = MutableSharedFlow(replay = 1)

    init {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                getCurrentUser()
            } catch (e: Exception) {
                logger.e(e, "No previously logged in user")
            }
        }
    }

    suspend fun login(server: String, username: String, password: String) {
        var correctedServer = if (server.startsWith("http")) {
            server
        } else {
            "http://$server"
        }
        if (!correctedServer.endsWith("/v1")) {
            correctedServer = if (correctedServer.endsWith("/")) {
                "${correctedServer}v1"
            } else {
                "$correctedServer/v1"
            }
        }
        val credentials = Base64.encode(
            "$username:$password".encodeToByteArray(),
            Base64.DEFAULT or Base64.NO_WRAP
        )
            .decodeToString()
        apiService.setBaseUrl(correctedServer)
        sharedPreferences.edit {
            putString(PREF_KEY_BASE_URL, correctedServer)
            putString(PREF_KEY_AUTH_TOKEN, "Basic $credentials")
        }
        try {
            val user = apiService.getCurrentUser()
            _currentUser.emit(user)
            userDao.insertAll(user)
            sharedPreferences.edit {
                putLong(PREF_KEY_CURRENT_USER, user.id)
            }
        } catch (e: Exception) {
            sharedPreferences.edit {
                remove(PREF_KEY_BASE_URL)
                remove(PREF_KEY_AUTH_TOKEN)
                remove(PREF_KEY_CURRENT_USER)
            }
            throw e
        }
    }

    suspend fun logout() {
        userDao.deleteAll()
        sharedPreferences.edit {
            remove(PREF_KEY_BASE_URL)
            remove(PREF_KEY_AUTH_TOKEN)
            remove(PREF_KEY_CURRENT_USER)
        }
    }

    suspend fun getCurrentUser(): com.wbrawner.nanoflux.storage.model.User {
        return _currentUser.replayCache.firstOrNull()
            ?: sharedPreferences.getLong(PREF_KEY_CURRENT_USER, -1L).let {
                if (it == -1L) {
                    null
                } else {
                    userDao.getAllByIds(it).firstOrNull()
                }
            }
            ?: apiService.getCurrentUser().also {
                userDao.insertAll(it)
                _currentUser.emit(it)
            }
    }
}