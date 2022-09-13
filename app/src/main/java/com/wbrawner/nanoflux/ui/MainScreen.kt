package com.wbrawner.nanoflux.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.wbrawner.nanoflux.NanofluxApp
import com.wbrawner.nanoflux.SyncWorker
import com.wbrawner.nanoflux.data.viewmodel.AuthViewModel
import com.wbrawner.nanoflux.data.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun MainScreen(
    authViewModel: AuthViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = context) {
        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequestBuilder<SyncWorker>().build())
    }
    val navController = rememberNavController()
    MainScaffold(
        navController,
        { navController.navigate("unread") },
        { navController.navigate("starred") },
        { navController.navigate("history") },
        { navController.navigate("feeds") },
        { navController.navigate("categories") },
        { navController.navigate("settings") },
    )
}

@Composable
fun MainScaffold(
    navController: NavHostController,
    onUnreadClicked: () -> Unit,
    onStarredClicked: () -> Unit,
    onHistoryClicked: () -> Unit,
    onFeedsClicked: () -> Unit,
    onCategoriesClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { scaffoldState.drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
                title = {
                    Text(
                        text = navController.currentDestination?.displayName?.capitalize(Locale.ENGLISH)
                            ?: "Unread"
                    )
                })
        },
        drawerContent = {
            DrawerButton(onClick = onUnreadClicked, icon = Icons.Default.Email, text = "Unread")
            DrawerButton(onClick = onStarredClicked, icon = Icons.Default.Star, text = "Starred")
            DrawerButton(
                onClick = onHistoryClicked,
                icon = Icons.Default.DateRange,
                text = "History"
            )
            DrawerButton(onClick = onFeedsClicked, icon = Icons.Default.List, text = "Feeds")
            DrawerButton(
                onClick = onCategoriesClicked,
                icon = Icons.Default.Info,
                text = "Categories"
            )
            DrawerButton(
                onClick = onSettingsClicked,
                icon = Icons.Default.Settings,
                text = "Settings"
            )
        }
    ) {
        // TODO: Extract routes to constants
        NavHost(navController, startDestination = "unread") {
            composable("unread") {
                UnreadScreen(navController, snackbarHostState, hiltViewModel())
            }
            composable(
                "entries/{entryId}",
                arguments = listOf(navArgument("entryId") { type = NavType.LongType })
            ) {
                EntryScreen(it.arguments!!.getLong("entryId"), hiltViewModel())
            }
        }
    }
}

@Composable
fun DrawerButton(onClick: () -> Unit, icon: ImageVector, text: String) {
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                imageVector = icon,
                contentDescription = null, // decorative
//                        colorFilter = ColorFilter.tint(textIconColor),
//                        alpha = imageAlpha
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = text,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
@Preview
fun MainScaffold_Preview() {
    NanofluxApp {
        val navController = rememberNavController()
        MainScaffold(navController, {}, {}, {}, {}, {}, {})
    }
}