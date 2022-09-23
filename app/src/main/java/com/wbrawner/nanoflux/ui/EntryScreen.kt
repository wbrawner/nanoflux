package com.wbrawner.nanoflux.ui

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.wbrawner.nanoflux.BuildConfig
import com.wbrawner.nanoflux.data.viewmodel.EntryViewModel
import com.wbrawner.nanoflux.storage.model.Category
import com.wbrawner.nanoflux.storage.model.Entry
import com.wbrawner.nanoflux.storage.model.EntryAndFeed
import com.wbrawner.nanoflux.storage.model.Feed
import kotlinx.coroutines.launch

@Composable
fun EntryScreen(
    entryId: Long,
    navController: NavController,
    entryViewModel: EntryViewModel,
    setTitle: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val loading by entryViewModel.loading.collectAsState()
    val errorMessage by entryViewModel.errorMessage.collectAsState()
    val entry by entryViewModel.entry.collectAsState()
    LaunchedEffect(key1 = entryId) {
        entryViewModel.loadEntry(entryId)
    }
    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    errorMessage?.let {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Unable to load entry: $it")
        }
    }
    entry?.let {
        LaunchedEffect(it.entry.title) {
            // TODO: Use Material3 to use collapsing toolbar
            setTitle(it.entry.title)
        }
        EntryScreen(
            entry = it,
            onFeedClicked = { feed -> navController.navigate("feeds/${feed.id}") },
            onCategoryClicked = { category -> navController.navigate("categories/${category.id}") },
            onToggleReadClicked = entryViewModel::toggleRead,
            onStarClicked = entryViewModel::toggleStar,
            onShareClicked = { entry ->
                coroutineScope.launch {
                    entryViewModel.share(entry)?.let { url ->
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
            onExternalLinkClicked = { entry ->
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(entry.url)))
            },
            onDownloadClicked = entryViewModel::downloadOriginalContent
        )
    }
}

val cssOverride = """
    <style>
    :root {
        --color-background: #FFFFFF;
        --color-foreground: #000000;
        --color-accent: #1976D2;
    }
    
    @media (prefers-color-scheme: dark) {
        :root {
            --color-background: #121212;
            --color-foreground: #FFFFFF;
            --color-accent: #90CAF9;
        }
    }
    
    html, body {
        background: var(--color-background);
        color: var(--color-foreground);
    }
    
    a {
        color: var(--color-accent);
    }
    
    img, video, iframe {
        max-width: 100% !important;
        height: auto !important;
    }
    </style>
""".trimIndent()

@Composable
fun EntryScreen(
    entry: EntryAndFeed,
    onFeedClicked: (feed: Feed) -> Unit,
    onCategoryClicked: (category: Category) -> Unit,
    onToggleReadClicked: (entry: Entry) -> Unit,
    onStarClicked: (entry: Entry) -> Unit,
    onShareClicked: (entry: Entry) -> Unit,
    onExternalLinkClicked: (entry: Entry) -> Unit,
    onDownloadClicked: (entry: Entry) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = 8.dp,
                mainAxisAlignment = FlowMainAxisAlignment.Start,
                crossAxisAlignment = FlowCrossAxisAlignment.Center
            ) {
                FeedButton(
                    icon = entry.feed.icon,
                    feed = entry.feed.feed,
                    onFeedClicked = onFeedClicked
                )
                CategoryButton(
                    category = entry.feed.category,
                    onCategoryClicked = onCategoryClicked
                )
                Spacer(modifier = Modifier.weight(1f))
                val fontStyle = MaterialTheme.typography.body2.copy(
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp
                )
                Text(text = entry.entry.publishedAt.timeSince(), style = fontStyle)
                Text(text = "${entry.entry.readingTime}m read", style = fontStyle)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onToggleReadClicked(entry.entry) }) {
                    val (icon, description) = when (entry.entry.status) {
                        Entry.Status.UNREAD -> Icons.Outlined.Visibility to "Mark read"
                        else -> Icons.Outlined.VisibilityOff to "Make unread"
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = description
                    )
                }
                IconButton(onClick = { onStarClicked(entry.entry) }) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Star"
                    )
                }
                IconButton(onClick = { onShareClicked(entry.entry) }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share"
                    )
                }
                IconButton(onClick = { onExternalLinkClicked(entry.entry) }) {
                    Icon(
                        imageVector = Icons.Outlined.OpenInBrowser,
                        contentDescription = "Open in browser"
                    )
                }
                IconButton(onClick = { onDownloadClicked(entry.entry) }) {
                    Icon(
                        imageVector = Icons.Outlined.Download,
                        contentDescription = "Download original content"
                    )
                }
            }
        }
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
                    settings.javaScriptEnabled = true
                }
            },
            update = {
                val baseUrl = Uri.parse(entry.entry.url)
                    .buildUpon()
                    .path("/")
                    .build()
                    .toString()
                it.loadDataWithBaseURL(
                    baseUrl,
                    cssOverride + entry.entry.content,
                    "text/html",
                    null,
                    null
                )
            }
        )
    }
}