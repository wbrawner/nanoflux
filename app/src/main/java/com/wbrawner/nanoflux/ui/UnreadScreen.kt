package com.wbrawner.nanoflux.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
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
    errorMessage?.let {
        coroutineScope.launch {
            when (snackbarHostState.showSnackbar(it, "Retry")) {
                SnackbarResult.ActionPerformed -> unreadViewModel.refresh()
                else -> unreadViewModel.dismissError()
            }
        }
    }
    val context = LocalContext.current
    EntryList(
        entries = entries,
        onEntryItemClicked = {
            navController.navigate("entries/${it.id}")
        },
        onFeedClicked = {
            navController.navigate("feeds/${it.id}")
        },
        onToggleReadClicked = { unreadViewModel.toggleRead(it) },
        onStarClicked = { unreadViewModel.toggleStar(it) },
        onExternalLinkClicked = {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
        },
        onShareClicked = { entry ->
            coroutineScope.launch {
                unreadViewModel.share(entry)?.let { url ->
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            url
                        )
                    }
                    context.startActivity(Intent.createChooser(intent, null))
                }
            }
        },
        isRefreshing = loading,
        onRefresh = {
            unreadViewModel.refresh()
        }
    )
}