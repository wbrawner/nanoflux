package com.wbrawner.nanoflux.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.wbrawner.nanoflux.NanofluxApp
import com.wbrawner.nanoflux.SyncWorker
import com.wbrawner.nanoflux.data.viewmodel.*
import kotlinx.coroutines.launch
import java.time.Duration

@Composable
fun MainScreen(
    authViewModel: AuthViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresDeviceIdle(true)
            .build()
        val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = Duration.ofHours(1),
            flexTimeInterval = Duration.ofMinutes(45)
        )
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context)
            .enqueue(workRequest)
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
    val (title, setTitle) = remember { mutableStateOf("Unread") }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            if (navController.currentDestination?.route?.startsWith("entries/") == true) {
                                navController.popBackStack()
                            } else {
                                scaffoldState.drawerState.open()
                            }
                        }
                    }) {
                        val icon =
                            if (navController.currentDestination?.route?.startsWith("entries/") == true) {
                                Icons.Default.ArrowBack
                            } else {
                                Icons.Default.Menu
                            }
                        Icon(
                            imageVector = icon,
                            contentDescription = "Menu"
                        )
                    }
                },
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                })
        },
        drawerContent = {
            val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 8.dp
            Row(Modifier.padding(top = topPadding, start = 8.dp, end = 8.dp, bottom = 8.dp)) {
                Text(text = "nano", fontSize = 24.sp)
                Text(text = "flux", color = MaterialTheme.colors.primary, fontSize = 24.sp)
            }
            DrawerButton(
                onClick = {
                    onUnreadClicked()
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                icon = Icons.Default.Email, text = "Unread"
            )
            DrawerButton(
                onClick = {
                    onStarredClicked()
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                icon = Icons.Default.Star,
                text = "Starred"
            )
            DrawerButton(
                onClick = {
                    onHistoryClicked()
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                icon = Icons.Default.DateRange,
                text = "History"
            )
            DrawerButton(
                onClick = {
                    onFeedsClicked()
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                icon = Icons.Default.List,
                text = "Feeds"
            )
            DrawerButton(
                onClick = {
                    onCategoriesClicked()
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                icon = Icons.Default.Info,
                text = "Categories"
            )
            DrawerButton(
                onClick = {
                    onSettingsClicked()
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                icon = Icons.Default.Settings,
                text = "Settings"
            )
        }
    ) {
        // TODO: Extract routes to constants
        NavHost(
            modifier = Modifier.padding(it),
            navController = navController,
            startDestination = "unread"
        ) {
            composable("unread") {
                LaunchedEffect(navController.currentBackStackEntry) {
                    setTitle("Unread")
                }
                EntryListScreen(navController, snackbarHostState, hiltViewModel<UnreadViewModel>())
            }
            composable("starred") {
                LaunchedEffect(navController.currentBackStackEntry) {
                    setTitle("Starred")
                }
                EntryListScreen(navController, snackbarHostState, hiltViewModel<StarredViewModel>())
            }
            composable("history") {
                LaunchedEffect(navController.currentBackStackEntry) {
                    setTitle("History")
                }
                EntryListScreen(navController, snackbarHostState, hiltViewModel<HistoryViewModel>())
            }
            composable(
                "entries/{entryId}",
                arguments = listOf(navArgument("entryId") { type = NavType.LongType })
            ) {
                LaunchedEffect(navController.currentBackStackEntry) {
                    setTitle("")
                }
                EntryScreen(
                    it.arguments!!.getLong("entryId"),
                    navController,
                    hiltViewModel(),
                    setTitle,
                )
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface
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