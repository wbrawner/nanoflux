package com.wbrawner.nanoflux.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wbrawner.nanoflux.NanofluxApp
import com.wbrawner.nanoflux.data.viewmodel.AuthViewModel
import com.wbrawner.nanoflux.ui.theme.NanofluxTheme
import timber.log.Timber

@Composable
fun AuthScreen(authViewModel: AuthViewModel) {
    val vmState = authViewModel.state.collectAsState()
    val state = vmState.value
    if (state is AuthViewModel.AuthState.Loading) {
        CircularProgressIndicator()
    } else if (state is AuthViewModel.AuthState.Unauthenticated) {
        AuthForm(
            state.server,
            state.username,
            state.password,
            authViewModel::login,
            state.errorMessage
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthForm(
    initialServer: String,
    initialUsername: String,
    initialPassword: String,
    onLoginClicked: (String, String, String) -> Unit,
    error: String? = null,
) {
    val scrollState = rememberScrollState()
    val serverFocus = remember { FocusRequester() }
    val usernameFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }
    val (server: String, setServer: (String) -> Unit) = remember { mutableStateOf(initialServer) }
    val (username: String, setUsername: (String) -> Unit) = remember {
        mutableStateOf(
            initialUsername
        )
    }
    val (password: String, setPassword: (String) -> Unit) = remember {
        mutableStateOf(
            initialPassword
        )
    }
    fun handleKeyEvent(
        previousFocusRequester: FocusRequester?,
        nextFocusRequester: FocusRequester?
    ): (KeyEvent) -> Boolean = {
        when (it.key) {
            Key.Tab -> {
                if (it.isShiftPressed) {
                    previousFocusRequester?.requestFocus()
                    Timber.d("Shift+Tab pressed")
                } else {
                    nextFocusRequester?.requestFocus()
                    Timber.d("Tab pressed")
                }
                true
            }
            Key.Enter -> {
                Timber.d("Enter pressed")
                onLoginClicked(server, username, password)
                true
            }
            else -> false
        }
    }
    Column(
        modifier = Modifier
            .verticalScroll(state = scrollState)
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        val commonModifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
        if (!error.isNullOrBlank()) {
            BasicText(
                text = error,
                style = TextStyle.Default.copy(
                    color = MaterialTheme.colors.error,
                    textAlign = TextAlign.Center
                ),
                modifier = commonModifier
            )
        }
        OutlinedTextField(
            value = server,
            onValueChange = setServer,
            label = {
                BasicText(
                    text = "Miniflux Server URL",
                    style = TextStyle.Default.copy(color = MaterialTheme.colors.onSurface)
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { usernameFocus.requestFocus() }),
            modifier = commonModifier
                .focusRequester(serverFocus)
                .onPreviewKeyEvent(handleKeyEvent(null, usernameFocus)),
            maxLines = 1
        )
        OutlinedTextField(
            value = username,
            onValueChange = setUsername,
            label = {
                BasicText(
                    text = "Username",
                    style = TextStyle.Default.copy(color = MaterialTheme.colors.onSurface)
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() }),
            modifier = commonModifier
                .focusRequester(usernameFocus)
                .onPreviewKeyEvent(handleKeyEvent(serverFocus, passwordFocus)),
            maxLines = 1
        )
        OutlinedTextField(
            value = password,
            onValueChange = setPassword,
            label = {
                BasicText(
                    text = "Password",
                    style = TextStyle.Default.copy(color = MaterialTheme.colors.onSurface)
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(onGo = {
                onLoginClicked(server, username, password)
            }),
            modifier = commonModifier
                .focusRequester(passwordFocus)
                .onPreviewKeyEvent(handleKeyEvent(usernameFocus, null)),
            visualTransformation = PasswordVisualTransformation(),
            maxLines = 1
        )
        Button(
            onClick = { onLoginClicked(server, username, password) },
            modifier = commonModifier,
        ) {
            BasicText(
                "Login",
                style = TextStyle.Default.copy(color = MaterialTheme.colors.onPrimary)
            )
        }
    }
}

@Composable
@Preview
fun Preview_AuthScreen() {
    NanofluxApp {
        AuthForm("https://example.org", "myuser", "mypass", { _, _, _ -> }, "Invalid password")
    }
}

@Composable
@Preview
fun Preview_Night_AuthScreen() {
    NanofluxTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colors.surface) {
            AuthForm("https://example.org", "myuser", "mypass", { _, _, _ -> }, "Invalid password")
        }
    }
}