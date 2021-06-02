package com.wbrawner.nanoflux.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.wbrawner.nanoflux.NanofluxApp
import com.wbrawner.nanoflux.SyncWorker
import com.wbrawner.nanoflux.data.viewmodel.AuthViewModel
import com.wbrawner.nanoflux.data.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    authViewModel: AuthViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = authViewModel.state) {
        val workRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
        mainViewModel.loadUnread()
    }
    val state = mainViewModel.state.collectAsState()
    val navController = rememberNavController()
    MainScaffold(
        state.value,
        navController,
        {

        },
        {},
        {},
        {},
        {},
        {}
    )
}

@Composable
fun MainScaffold(
    state: MainViewModel.FeedState,
    navController: NavHostController,
    onUnreadClicked: () -> Unit,
    onStarredClicked: () -> Unit,
    onHistoryClicked: () -> Unit,
    onFeedsClicked: () -> Unit,
    onCategoriesClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
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
            DrawerButton(onClick = onUnreadClicked, icon = Icons.Default.Email, text = "Unread")
            DrawerButton(onClick = onStarredClicked, icon = Icons.Default.Star, text = "Starred")
            DrawerButton(onClick = onHistoryClicked, icon = Icons.Default.DateRange, text = "History")
            DrawerButton(onClick = onFeedsClicked, icon = Icons.Default.List, text = "Feeds")
            DrawerButton(onClick = onCategoriesClicked, icon = Icons.Default.Info, text = "Categories")
            DrawerButton(onClick = onSettingsClicked, icon = Icons.Default.Settings, text = "Settings")
        }
    ) {
        when (state) {
            is MainViewModel.FeedState.Loading -> CircularProgressIndicator()
            is MainViewModel.FeedState.Failed -> Text("TODO: Failed to load entries: ${state.errorMessage}")
            is MainViewModel.FeedState.Success -> EntryList(
                entries = state.entries,
                onEntryItemClicked = { /*TODO*/ },
                onFeedClicked = { /*TODO*/ },
                onToggleReadClicked = { /*TODO*/ },
                onStarClicked = { /*TODO*/ }) {
            }
            is MainViewModel.FeedState.Empty -> Text("TODO: No entries")
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
        MainScaffold(MainViewModel.FeedState.Loading, navController, {}, {}, {}, {}, {}, {})
    }
}