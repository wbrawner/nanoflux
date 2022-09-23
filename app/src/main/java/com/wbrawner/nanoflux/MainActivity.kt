package com.wbrawner.nanoflux

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.wbrawner.nanoflux.data.viewmodel.AuthViewModel
import com.wbrawner.nanoflux.ui.MainScreen
import com.wbrawner.nanoflux.ui.auth.AuthScreen
import com.wbrawner.nanoflux.ui.theme.NanofluxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authenticated by authViewModel.state.collectAsState()
            NanofluxApp {
                if (authenticated is AuthViewModel.AuthState.Authenticated) {
                    MainScreen(authViewModel)
                } else {
                    AuthScreen(authViewModel)
                }
            }
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}

@Composable
fun NanofluxApp(content: @Composable () -> Unit) {
    NanofluxTheme {
        Surface(color = MaterialTheme.colors.background) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    NanofluxApp {

    }
}
