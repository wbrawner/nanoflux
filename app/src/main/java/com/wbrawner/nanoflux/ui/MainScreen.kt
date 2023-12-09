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
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    Text(text = "nano")
                    Text(text = "flux", color = MaterialTheme.colors.primary)
                },
                actions = {
                    IconButton(onClick = onSettingsClicked) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colors.onSurface)
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.navigationBarsPadding(),
                backgroundColor = MaterialTheme.colors.surface
            ) {
                BottomNavigationItem(
                    selected = navController.currentDestination?.route == null || navController.currentDestination?.route == "unread",
                    onClick = onUnreadClicked,
                    icon = { Icon(Icons.Default.Article, contentDescription = null) },
                    label = { Text("Entries") },
                    selectedContentColor = MaterialTheme.colors.primary
                )
//                BottomNavigationItem(
//                    selected = navController.currentDestination?.route == "starred",
//                    onClick = onStarredClicked,
//                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
//                    label = { Text("Starred") },
//                    selectedContentColor = MaterialTheme.colors.primary
//                )
//                BottomNavigationItem(
//                    selected = navController.currentDestination?.route == "history",
//                    onClick = onHistoryClicked,
//                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
//                    label = { Text("History") },
//                    selectedContentColor = MaterialTheme.colors.primary
//                )
                BottomNavigationItem(
                    selected = navController.currentDestination?.route == "feeds",
                    onClick = onFeedsClicked,
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Feeds") },
                    selectedContentColor = MaterialTheme.colors.primary,
                    enabled = false // remove when implemented
                )
                BottomNavigationItem(
                    selected = navController.currentDestination?.route == "categories",
                    onClick = onCategoriesClicked,
                    icon = { Icon(Icons.Default.Category, contentDescription = null) },
                    label = {
                        Text(
                            text = "Categories",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    selectedContentColor = MaterialTheme.colors.primary,
                    enabled = false // remove when implemented
                )
            }
        },
    ) { padding ->
        // TODO: Extract routes to constants
        NavHost(
            modifier = Modifier.padding(padding),
            navController = navController,
            startDestination = "unread"
        ) {
            composable("unread") {
                EntryListScreen(navController, snackbarHostState, hiltViewModel<UnreadViewModel>())
            }
            composable("starred") {
                EntryListScreen(navController, snackbarHostState, hiltViewModel<StarredViewModel>())
            }
            composable("history") {
                EntryListScreen(navController, snackbarHostState, hiltViewModel<HistoryViewModel>())
            }
            composable(
                "feeds/{feedId}",
                arguments = listOf(navArgument("feedId") { type = NavType.LongType })
            ) {
                EntryListScreen(navController, snackbarHostState, hiltViewModel<HistoryViewModel>())
            }
            composable(
                "categories/{categoryId}",
                arguments = listOf(navArgument("categoryId") { type = NavType.LongType })
            ) {
                EntryListScreen(navController, snackbarHostState, hiltViewModel<HistoryViewModel>())
            }
            composable(
                "entries/{entryId}",
                arguments = listOf(navArgument("entryId") { type = NavType.LongType })
            ) {
                EntryScreen(
                    it.arguments!!.getLong("entryId"),
                    navController,
                    hiltViewModel(),
                )
            }
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