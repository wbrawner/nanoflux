package com.wbrawner.nanoflux.ui

import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.wbrawner.nanoflux.data.viewmodel.EntryViewModel
import com.wbrawner.nanoflux.storage.model.EntryAndFeed

@Composable
fun EntryScreen(entryId: Long, entryViewModel: EntryViewModel) {
    val loading by entryViewModel.loading.collectAsState()
    val errorMessage by entryViewModel.errorMessage.collectAsState()
    val entry by entryViewModel.entry.collectAsState()
    LaunchedEffect(key1 = entryId) {
        entryViewModel.loadEntry(entryId)
    }
    if (loading) {
        CircularProgressIndicator()
    }
    errorMessage?.let {
        Text("Unable to load entry: $it")
    }
    entry?.let {
        Entry(it)
    }
}

@Composable
fun Entry(entry: EntryAndFeed) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        ) {
        EntryListItem(
            entry = entry.entry,
            feed = entry.feed,
            onEntryItemClicked = { /*TODO*/ },
            onFeedClicked = { /*TODO*/ },
            onExternalLinkClicked = { /*TODO*/ },
            onShareClicked = { /*TODO*/ },
        )
        // Adds view to Compose
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            factory = { context ->
                TextView(context).apply {
                    text = Html.fromHtml(entry.entry.content)
                }
            },
        )
    }
}