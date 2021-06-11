package com.wbrawner.nanoflux.ui

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wbrawner.nanoflux.data.viewmodel.UnreadViewModel
import kotlinx.coroutines.launch

@Composable
fun UnreadScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    unreadViewModel: UnreadViewModel = viewModel()
) {
    val loading by unreadViewModel.loading.collectAsState()
    val errorMessage by unreadViewModel.errorMessage.collectAsState()
    val entries by unreadViewModel.entries.collectAsState(emptyList())
    val coroutineScope = rememberCoroutineScope()
    if (loading) {
        CircularProgressIndicator()
    }
    errorMessage?.let {
        coroutineScope.launch {
            when (snackbarHostState.showSnackbar(it, "Retry")) {
//                SnackbarResult.ActionPerformed -> unreadViewModel.loadUnread()
                else -> unreadViewModel.dismissError()
            }
        }
    }
    if (entries.isEmpty()) {
        Text("TODO: No entries")
    } else {
        EntryList(
            entries = entries,
            onEntryItemClicked = {
                navController.navigate("entries/${it.id}")
            },
            onFeedClicked = {
                navController.navigate("feeds/${it.id}")
            },
            onToggleReadClicked = { /*TODO*/ },
            onStarClicked = { /*TODO*/ }) {
        }
    }
}