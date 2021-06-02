package com.wbrawner.nanoflux.network

import android.content.SharedPreferences
import com.wbrawner.nanoflux.network.repository.PREF_KEY_AUTH_TOKEN
import com.wbrawner.nanoflux.storage.model.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import timber.log.Timber
import java.io.File
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject


interface MinifluxApiService {
    fun setBaseUrl(url: String)

    suspend fun discoverSubscriptions(
        url: String,
        username: String? = null,
        password: String? = null,
        userAgent: String? = null,
        fetchViaProxy: Boolean? = null
    ): DiscoverResponse

    suspend fun getFeeds(): List<FeedJson>

    suspend fun getCategoryFeeds(categoryId: Long): List<FeedJson>

    suspend fun getFeed(id: Long): FeedJson

    suspend fun getFeedIcon(feedId: Long): Feed.Icon

    suspend fun createFeed(url: String, categoryId: Long?): CreateFeedResponse

    suspend fun updateFeed(id: Long, feed: FeedJson): Feed

    suspend fun refreshFeeds(): HttpResponse

    suspend fun refreshFeed(id: Long): HttpResponse

    suspend fun deleteFeed(id: Long): HttpResponse

    suspend fun getFeedEntries(
        feedId: Long,
        status: List<Entry.Status>? = null,
        offset: Long? = null,
        limit: Long? = null,
        order: Entry.Order? = null,
        direction: Entry.SortDirection? = null,
        before: Long? = null,
        after: Long? = null,
        beforeEntryId: Long? = null,
        afterEntryId: Long? = null,
        starred: Boolean? = null,
        search: String? = null,
        categoryId: Long? = null,
    ): EntryResponse

    suspend fun getFeedEntry(feedId: Long, entryId: Long): Entry

    suspend fun getEntries(
        status: List<Entry.Status>? = null,
        offset: Long? = null,
        limit: Long? = null,
        order: Entry.Order? = null,
        direction: Entry.SortDirection? = null,
        before: Long? = null,
        after: Long? = null,
        beforeEntryId: Long? = null,
        afterEntryId: Long? = null,
        starred: Boolean? = null,
        search: String? = null,
        categoryId: Long? = null,
    ): EntryResponse

    suspend fun getEntry(entryId: Long): Entry

    suspend fun markFeedEntriesAsRead(id: Long): HttpResponse

    suspend fun updateEntries(entryIds: List<Long>, status: Entry.Status): HttpResponse

    suspend fun toggleEntryBookmark(id: Long): HttpResponse

    suspend fun getCategories(): List<Category>

    suspend fun createCategory(title: String): Category

    suspend fun updateCategory(id: Long, title: String): Category

    suspend fun deleteCategory(id: Long): HttpResponse

    suspend fun markCategoryEntriesAsRead(id: Long): HttpResponse

    //@GET("export")
    suspend fun opmlExport(): HttpResponse

    //@POST("import")
    suspend fun opmlImport(opml: File): HttpResponse

//    //@POST("users")
//    suspend fun createUser(user: UserRequest): User

    //@PUT("users/{id}")
    suspend fun updateUser(id: Long, user: User): User

    //@GET("me")
    suspend fun getCurrentUser(): User

    //@GET("users/{id}")
    suspend fun getUser(id: Long): User

    //@GET("users/{name}")
    suspend fun getUserByName(name: String): User

    //@GET("users")
    suspend fun getUses(): List<User>

    //@DELETE("users/{id}")
    suspend fun deleteUser(id: Long)

    //@PUT("users/{id}/mark-all-as-read")
    suspend fun markUserEntriesAsRead(id: Long): HttpResponse

    //@GET("/healthcheck")
    suspend fun healthcheck(): String

    //@GET("/version")
    suspend fun version(): String
}

@Serializable
data class DiscoverRequest(
    val url: String,
    val username: String? = null,
    val password: String? = null,
    val userAgent: String? = null,
    @SerialName("fetch_via_proxy")
    val fetchViaProxy: Boolean? = null
)

@Serializable
data class DiscoverResponse(val url: String, val title: String, val type: String)

@Serializable
data class ErrorResponse(@SerialName("error_message") val errorMessage: String)

@Serializable
data class FeedRequest(
    @SerialName("feed_url")
    val feedUrl: String,
    @SerialName("category_id")
    val categoryId: Long? = null
)

@Serializable
data class CreateFeedResponse(@SerialName("feed_id") val feedId: Long)

@Serializable
data class EntryResponse(val total: Long, val entries: List<Entry>)

@Suppress("unused")
class KtorMinifluxApiService @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val _logger: Timber.Tree
) : MinifluxApiService {
    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    _logger.v(message)
                }

            }
            level = LogLevel.ALL
        }
    }
    private val _baseUrl: AtomicReference<String?> = AtomicReference()
    private val baseUrl: String
        get() = _baseUrl.get() ?: run {
            sharedPreferences.getString(PREF_KEY_BASE_URL, null)?.let {
                _baseUrl.set(it)
                it
            } ?: ""
        }

    private fun url(
        path: String,
        vararg queryParams: Pair<String, Any?>
    ): Url {
        val url = URLBuilder(baseUrl)
        if (path.startsWith('/')) {
            url.path(path)
        } else {
            url.encodedPath += "/$path"
        }
        for (param in queryParams) {
            param.second?.let {
                url.parameters.append(param.first, it.toString())
            }
        }
        return url.build()
    }

    override fun setBaseUrl(url: String) {
        _baseUrl.set(url)
    }

    override suspend fun discoverSubscriptions(
        url: String,
        username: String?,
        password: String?,
        userAgent: String?,
        fetchViaProxy: Boolean?
    ): DiscoverResponse = client.post(url("discover")) {
        body = DiscoverRequest(url, username, password, userAgent, fetchViaProxy)
        headers {
            header("Authorization", sharedPreferences.getString(PREF_KEY_AUTH_TOKEN, "").toString())
        }
    }

    override suspend fun getFeeds(): List<FeedJson> = client.get(url("feeds")) {
        headers {
            header("Authorization", sharedPreferences.getString(PREF_KEY_AUTH_TOKEN, "").toString())
        }
    }

    override suspend fun getCategoryFeeds(categoryId: Long): List<FeedJson> =
        client.get(baseUrl + "categories/$categoryId/feeds") {
            headers {
                header(
                    "Authorization",
                    sharedPreferences.getString(PREF_KEY_AUTH_TOKEN, "").toString()
                )
            }
        }

    override suspend fun getFeed(id: Long): FeedJson = client.get(url("feeds/$id")) {
        headers {
            header("Authorization", sharedPreferences.getString(PREF_KEY_AUTH_TOKEN, "").toString())
        }
    }

    override suspend fun getFeedIcon(feedId: Long): Feed.Icon =
        client.get(url("feeds/$feedId/icon")) {
            headers {
                header(
                    "Authorization",
                    sharedPreferences.getString(PREF_KEY_AUTH_TOKEN, "").toString()
                )
            }
        }

    override suspend fun createFeed(url: String, categoryId: Long?): CreateFeedResponse =
        client.post(url("feeds")) {
            body = FeedRequest(url, categoryId)
            headers {
                header(
                    "Authorization",
                    sharedPreferences.getString(PREF_KEY_AUTH_TOKEN, "").toString()
                )
            }
        }

    override suspend fun updateFeed(id: Long, feed: FeedJson): Feed = client.put(url("feeds/$id")) {
        body = feed
    }

    override suspend fun refreshFeeds(): HttpResponse = client.put(url("feeds/refresh"))

    override suspend fun refreshFeed(id: Long): HttpResponse = client.put(url("feeds/$id/refresh"))

    override suspend fun deleteFeed(id: Long): HttpResponse = client.delete(url("feeds/$id"))

    override suspend fun getFeedEntries(
        feedId: Long,
        status: List<Entry.Status>?,
        offset: Long?,
        limit: Long?,
        order: Entry.Order?,
        direction: Entry.SortDirection?,
        before: Long?,
        after: Long?,
        beforeEntryId: Long?,
        afterEntryId: Long?,
        starred: Boolean?,
        search: String?,
        categoryId: Long?
    ): EntryResponse = client.get(
        url(
            "feeds/$feedId/entries",
            "status" to status?.joinToString(","),
            "offset" to offset,
            "limit" to limit,
            "order" to order,
            "direction" to direction,
            "before" to before,
            "after" to after,
            "before_entry_id" to beforeEntryId,
            "after_entry_id" to afterEntryId,
            "starred" to starred,
            "search" to search,
            "category_id" to categoryId,
        )
    )

    override suspend fun getFeedEntry(feedId: Long, entryId: Long): Entry =
        client.get(url("feeds/$feedId/entries/$entryId"))

    override suspend fun getEntries(
        status: List<Entry.Status>?,
        offset: Long?,
        limit: Long?,
        order: Entry.Order?,
        direction: Entry.SortDirection?,
        before: Long?,
        after: Long?,
        beforeEntryId: Long?,
        afterEntryId: Long?,
        starred: Boolean?,
        search: String?,
        categoryId: Long?
    ): EntryResponse = client.get(
        url(
            "entries",
            "status" to status?.joinToString(",") { it.name.toLowerCase() },
            "offset" to offset,
            "limit" to limit,
            "order" to order,
            "direction" to direction,
            "before" to before,
            "after" to after,
            "before_entry_id" to beforeEntryId,
            "after_entry_id" to afterEntryId,
            "starred" to starred,
            "search" to search,
            "category_id" to categoryId,
        )
    ) {
        headers {
            header("Authorization", sharedPreferences.getString(PREF_KEY_AUTH_TOKEN, "").toString())
        }
    }

    override suspend fun getEntry(entryId: Long): Entry = client.get(url("entries/$entryId"))

    override suspend fun markFeedEntriesAsRead(id: Long): HttpResponse =
        client.put(url("feeds/$id/mark-all-as-read"))

    override suspend fun updateEntries(entryIds: List<Long>, status: Entry.Status): HttpResponse =
        client.put(url("entries")) {
//            body = @Serializable object {
//                val entry_ids = entryIds
//                val status = status
//            }
        }

    override suspend fun toggleEntryBookmark(id: Long): HttpResponse =
        client.put(url("entries/$id/bookmark"))

    override suspend fun getCategories(): List<Category> = client.get(url("categories")) {
        headers {
            header("Authorization", sharedPreferences.getString(PREF_KEY_AUTH_TOKEN, "").toString())
        }
    }

    override suspend fun createCategory(title: String): Category = client.post(url("categories")) {
//        body = @Serializable object {
//            val title = title
//        }
    }

    override suspend fun updateCategory(id: Long, title: String): Category =
        client.put(url("categories/$id")) {
//            body = @Serializable object {
//                val title = title
//            }
        }

    override suspend fun deleteCategory(id: Long): HttpResponse =
        client.delete(url("categories/$id"))

    override suspend fun markCategoryEntriesAsRead(id: Long): HttpResponse =
        client.put(url("categories/$id/mark-all-as-read"))

    override suspend fun opmlExport(): HttpResponse {
        TODO("Not yet implemented")
    }

    override suspend fun opmlImport(opml: File): HttpResponse {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(id: Long, user: User): User {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentUser(): User = client.get(url("me")) {
        headers {
            header("Authorization", sharedPreferences.getString(PREF_KEY_AUTH_TOKEN, "").toString())
        }
    }

    override suspend fun getUser(id: Long): User = client.get(url("users/$id"))

    override suspend fun getUserByName(name: String): User = client.get(url("users/$name"))

    override suspend fun getUses(): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun markUserEntriesAsRead(id: Long): HttpResponse {
        TODO("Not yet implemented")
    }

    override suspend fun healthcheck(): String {
        TODO("Not yet implemented")
    }

    override suspend fun version(): String {
        TODO("Not yet implemented")
    }
}