package com.wbrawner.nanoflux.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.wbrawner.nanoflux.NanofluxApp
import com.wbrawner.nanoflux.storage.model.*
import com.wbrawner.nanoflux.ui.theme.Green700
import com.wbrawner.nanoflux.ui.theme.Yellow700
import java.util.*
import kotlin.math.roundToInt

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun EntryList(
    entries: LazyPagingItems<EntryAndFeed>,
    onEntryItemClicked: (entry: Entry) -> Unit,
    onFeedClicked: (feed: Feed) -> Unit,
    onCategoryClicked: (category: Category) -> Unit,
    onToggleReadClicked: (entry: Entry) -> Unit,
    onStarClicked: (entry: Entry) -> Unit,
    onShareClicked: (entry: Entry) -> Unit,
    onExternalLinkClicked: (entry: Entry) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = onRefresh
    ) {
        LazyColumn {
            itemsIndexed(entries, { _, entry -> entry.entry.id }) { index, entry ->
                if (entry == null) {
                    EntryListPlaceholder()
                    return@itemsIndexed
                }
                val dismissState = rememberDismissState()
                LaunchedEffect(key1 = dismissState.currentValue, key2 = entry.entry.id) {
                    when (dismissState.currentValue) {
                        DismissValue.DismissedToStart -> {
                            onToggleReadClicked(entry.entry)
                        }
                        DismissValue.DismissedToEnd -> {
                            onStarClicked(entry.entry)
                            dismissState.reset()
                        }
                        DismissValue.Default -> {}
                    }
                }
                SwipeToDismiss(
                    modifier = Modifier.animateItemPlacement(),
                    state = dismissState,
                    background = {
                        val (backgroundColor, foregroundColor, text, icon) = when (dismissState.dismissDirection) {
                            DismissDirection.StartToEnd ->
                                if (entry.entry.starred) {
                                    EntrySwipeState(
                                        Yellow700,
                                        Color.Black,
                                        "Unstarred",
                                        Icons.Outlined.Star
                                    )
                                } else {
                                    EntrySwipeState(
                                        Yellow700,
                                        Color.Black,
                                        "Starred",
                                        Icons.Filled.Star
                                    )
                                }
                            DismissDirection.EndToStart -> EntrySwipeState(
                                Green700,
                                Color.White,
                                "Read",
                                Icons.Default.Email
                            )
                            else -> EntrySwipeState(
                                MaterialTheme.colors.surface,
                                MaterialTheme.colors.onSurface,
                                "",
                                Icons.Default.Info
                            )
                        }
                        Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = foregroundColor
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = text, color = foregroundColor)
                            }
                        }
                    }
                ) {
                    EntryListItem(
                        entry.entry,
                        entry.feed,
                        onEntryItemClicked,
                        onFeedClicked = onFeedClicked,
                        onCategoryClicked = onCategoryClicked,
                        onExternalLinkClicked = onExternalLinkClicked,
                        onShareClicked = onShareClicked,
                    )
                }
                if (index < entries.itemCount - 1) {
                    Divider()
                }
            }
        }
    }
}

data class EntrySwipeState(
    val backgroundColor: Color,
    val foregroundColor: Color,
    val text: String,
    val icon: ImageVector
)

@Composable
fun EntryListPlaceholder() {
    Surface(color = MaterialTheme.colors.surface) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
            ) {}
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
            ) {}
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
            ) {}
        }
    }
}

@Composable
fun EntryListItem(
    entry: Entry,
    feed: FeedCategoryIcon,
    onEntryItemClicked: (entry: Entry) -> Unit,
    onFeedClicked: (feed: Feed) -> Unit,
    onCategoryClicked: (category: Category) -> Unit,
    onExternalLinkClicked: (entry: Entry) -> Unit,
    onShareClicked: (entry: Entry) -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onEntryItemClicked(entry) },
        color = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = entry.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            FlowRow(
                crossAxisAlignment = FlowCrossAxisAlignment.Center,
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = 8.dp
            ) {
                FeedButton(feed.icon, feed.feed, onFeedClicked)
                CategoryButton(feed.category, onCategoryClicked)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val fontStyle = MaterialTheme.typography.body2.copy(
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp
                )
                Text(text = entry.publishedAt.timeSince(), style = fontStyle)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${entry.readingTime}m read", style = fontStyle)
                IconButton(onClick = { onExternalLinkClicked(entry) }) {
                    Icon(
                        imageVector = Icons.Outlined.OpenInBrowser,
                        contentDescription = "Open in browser"
                    )
                }
                IconButton(onClick = { onShareClicked(entry) }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share"
                    )
                }
            }
        }
    }
}

@Composable
fun FeedButton(
    icon: Feed.Icon?,
    feed: Feed,
    onFeedClicked: (feed: Feed) -> Unit
) {
    TextButton(
        onClick = { onFeedClicked(feed) },
        contentPadding = PaddingValues(
            start = 0.dp,
            top = ButtonDefaults.ContentPadding.calculateTopPadding(),
            end = 8.dp,
            bottom = ButtonDefaults.ContentPadding.calculateBottomPadding()
        )
    ) {
        icon?.let {
            val bytes = Base64.decode(it.data.substringAfter(","), Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ?.asImageBitmap()
                ?: return@let
            Image(
                modifier = Modifier
                    .width(16.dp)
                    .height(16.dp),
                bitmap = bitmap,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = feed.title,
            style = MaterialTheme.typography.body2,
            fontSize = 12.sp,
            color = MaterialTheme.colors.onSurface
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CategoryButton(
    category: Category,
    onCategoryClicked: (category: Category) -> Unit
) {
    Chip(
        onClick = { onCategoryClicked(category) },
    ) {
        Text(
            text = category.title,
            style = MaterialTheme.typography.body2,
            fontSize = 12.sp,
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
fun EntryListItem_Preview() {
    NanofluxApp {
        EntryListItem(
            entry = Entry(
                id = 0,
                userId = 0,
                feedId = 0,
                status = Entry.Status.UNREAD,
                hash = "",
                title = "Entry title",
                url = "https://example.com/entries/1",
                commentsUrl = "",
                publishedAt = GregorianCalendar(2019, 11, 1).time,
                createdAt = Date(),
                content = "Entry content here",
                author = "Jane Doe",
                shareCode = "shareCode",
                starred = false,
                readingTime = 12,
                enclosures = null
            ),
            feed = FeedCategoryIcon(
                feed = Feed(
                    id = 0,
                    userId = 0,
                    title = "Feed Title",
                    siteUrl = "",
                    feedUrl = "",
                    checkedAt = Date(),
                    etagHeader = "",
                    lastModifiedHeader = "",
                    parsingErrorMessage = "",
                    parsingErrorCount = 0,
                    scraperRules = "",
                    rewriteRules = "",
                    crawler = false,
                    blocklistRules = "",
                    keeplistRules = "",
                    userAgent = "",
                    username = "",
                    password = "",
                    disabled = false,
                    ignoreHttpCache = false,
                    fetchViaProxy = false,
                    categoryId = 0,
                    iconId = 0,
                    hideGlobally = false
                ),
                category = Category(
                    id = 2,
                    title = "Category Title",
                    userId = 0,
                    hideGlobally = false
                ),
                icon = Feed.Icon(
                    id = 0,
                    data = "image/png;base64,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mqn1+gmHMxozqxKVH9R8xeGXNnMXNAq8mcovnOVnP48/lUpcnu94xgnnHc97rVawH0Vjfzr7SHP/fRwUekrquGJ2PHtagJ+7Tb93cXmb49MOzusCDpzntGeXbi1+xw+2DSr/rq4e9r+nhcOf/4FzPk78wWHLbnb1Azs5DwQSf9yjee5ezc+9EMATAeIec7nj1t8D+Mdbdrb71/N0PALoC5A8z32B1b7fX5ywROYFZSCAtgAJ78hU2HCuzwJMlHtFQxBAV4AJciXe8XcyOEDyFeW0RQA9AQbK1pjgZ/7XZEtf6V4PAXQEuFZ6x/28x/yS/fKv6Zs0BFAXoPZB+SLZf/ZNgPdVXtQ0BFAXQOlhGptL+ZR/W6UXlVMXAVQFULyNrr9P5/7XqbUzHwFUBVijVmbXOb4IcKfqy2qBAGoC3Kxa569+5F9G+WceViCAkgClvlStc9iPO4z6qr+ulgigIsD16oX8OC20QL2dpxFARYDJ6oU+8OE/wE/q7XyBACoCbFMvdKK85wK013lhFyOAvACNdCp19VyADJ12hiKAvADpOpVmeS7ANp12liCAvADLdSrtifM4/wpaL2wfAsgLoPcDyxU9FqChVjd5iQggK4DmE1UbeyzAdXrtVEMAWQGq65Vq47EAPfXauRwBZAXQfKBmL48FGK7XTkcEkBVA83F6IzwW4Fm9dvojgKwAmg/TetZjAWbqtTMSAWQFGKlXaqbHAszSaycdAWQFSNcrNQsBEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAABEAAQABAAEAAQABAAEAAQABAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAAARAA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",
                    mimeType = "image/png"
                )
            ),
            {}, {}, {}, {}, {}
        )
    }
}

@Composable
@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
fun EntryListPlaceholder_Preview() {
    NanofluxApp {
        EntryListPlaceholder()
    }
}

private const val MILLIS_IN_SECOND = 1000.0
private const val MILLIS_IN_MINUTE = 60000.0
private const val MILLIS_IN_HOUR = 3_600_000.0
private const val MILLIS_IN_DAY = 86_400_000.0
private const val MILLIS_IN_WEEK = 604_800_000.0
private const val MILLIS_IN_MONTH = 2_628_000_000.0
private const val MILLIS_IN_YEAR = 31_540_000_000.0

val now = GregorianCalendar().timeInMillis
fun Date.timeSince(): String {
    val difference = now - time
    return when {
        difference >= MILLIS_IN_YEAR -> "${(difference / MILLIS_IN_YEAR).roundToInt()} years ago"
        difference >= MILLIS_IN_MONTH -> "${(difference / MILLIS_IN_MONTH).roundToInt()} months ago"
        difference >= MILLIS_IN_WEEK -> "${(difference / MILLIS_IN_WEEK).roundToInt()} weeks ago"
        difference >= MILLIS_IN_DAY -> "${(difference / MILLIS_IN_DAY).roundToInt()} days ago"
        difference >= MILLIS_IN_HOUR -> "${(difference / MILLIS_IN_HOUR).roundToInt()} hours ago"
        difference >= MILLIS_IN_MINUTE -> "${(difference / MILLIS_IN_MINUTE).roundToInt()} minutes ago"
        else -> "${(difference / MILLIS_IN_SECOND).roundToInt()} seconds ago"
    }
}