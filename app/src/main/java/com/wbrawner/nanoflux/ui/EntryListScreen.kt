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
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.wbrawner.nanoflux.data.viewmodel.EntryListViewModel
import kotlinx.coroutines.launch

@Composable
fun EntryListScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    entryListViewModel: EntryListViewModel
) {
    val loading by entryListViewModel.loading.collectAsState()
    val errorMessage by entryListViewModel.errorMessage.collectAsState()
    val entries = entryListViewModel.entries.collectAsLazyPagingItems()
    val coroutineScope = rememberCoroutineScope()
    errorMessage?.let {
        coroutineScope.launch {
            when (snackbarHostState.showSnackbar(it, "Retry")) {
                SnackbarResult.ActionPerformed -> entryListViewModel.refresh()
                else -> entryListViewModel.dismissError()
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
        onCategoryClicked = {
            navController.navigate("categories/${it.id}")
        },
        onToggleReadClicked = { entryListViewModel.toggleRead(it) },
        onStarClicked = { entryListViewModel.toggleStar(it) },
        onExternalLinkClicked = {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
        },
        onShareClicked = { entry ->
            coroutineScope.launch {
                entryListViewModel.share(entry)?.let { url ->
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
            entryListViewModel.refresh()
        }
    )
}