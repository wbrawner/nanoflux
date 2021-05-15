package com.wbrawner.nanoflux.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wbrawner.nanoflux.NanofluxApp
import com.wbrawner.nanoflux.data.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(authViewModel: AuthViewModel) {
    MainScaffold(
        authViewModel::logout
    )
}

@Composable
fun MainScaffold(
    onLogoutClicked: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(navigationIcon = {
                IconButton(onClick = { coroutineScope.launch { scaffoldState.drawerState.open() } }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu"
                    )
                }
            }, title = { Text(text = "Unread") })
        },
        drawerContent = {
            TextButton(onClick = onLogoutClicked, modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
//                    Image(
//                        imageVector = icon,
//                        contentDescription = null, // decorative
//                        colorFilter = ColorFilter.tint(textIconColor),
//                        alpha = imageAlpha
//                    )
//                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Logout",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    ) {

    }
}

@Composable
@Preview
fun MainScaffold_Preview() {
    NanofluxApp {
        MainScaffold {}
    }
}