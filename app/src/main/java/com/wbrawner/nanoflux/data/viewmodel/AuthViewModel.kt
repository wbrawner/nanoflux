package com.wbrawner.nanoflux.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wbrawner.nanoflux.data.model.User
import com.wbrawner.nanoflux.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val logger: Timber.Tree
) : ViewModel() {
    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                _state.emit(AuthState.Authenticated(userRepository.getCurrentUser()))
            } catch (e: Exception) {
                logger.e(e, "Unable to login user")
                _state.emit(AuthState.Unauthenticated())
            }
        }
    }

    fun login(server: String, username: String, password: String) {
        viewModelScope.launch {
            if (server.isBlank()) {
                _state.emit(
                    AuthState.Unauthenticated(
                        server,
                        username,
                        password,
                        "Please enter a valid server URL"
                    )
                )
                return@launch
            }
            if (username.isBlank()) {
                _state.emit(
                    AuthState.Unauthenticated(
                        server,
                        username,
                        password,
                        "Please enter a valid username"
                    )
                )
                return@launch
            }
            if (password.isBlank()) {
                _state.emit(
                    AuthState.Unauthenticated(
                        server,
                        username,
                        password,
                        "Please enter a valid password"
                    )
                )
                return@launch
            }
            _state.emit(AuthState.Loading)
            try {
                userRepository.login(server.trim(), username.trim(), password.trim())
                _state.emit(AuthState.Authenticated(userRepository.getCurrentUser()))
            } catch (e: Exception) {
                logger.e(e, "Login failed")
                _state.emit(AuthState.Unauthenticated(server, username, password, e.message))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _state.emit(AuthState.Unauthenticated("", "", ""))
        }
    }

    sealed class AuthState {
        object Loading : AuthState()
        class Authenticated(val user: User) : AuthState()
        class Unauthenticated(
            val server: String = "",
            val username: String = "",
            val password: String = "",
            val errorMessage: String? = null
        ) : AuthState()
    }
}