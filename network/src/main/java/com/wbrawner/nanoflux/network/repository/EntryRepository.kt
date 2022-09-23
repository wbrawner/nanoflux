package com.wbrawner.nanoflux.network.repository

import android.net.Uri
import com.wbrawner.nanoflux.network.EntryResponse
import com.wbrawner.nanoflux.network.MinifluxApiService
import com.wbrawner.nanoflux.storage.dao.EntryDao
import com.wbrawner.nanoflux.storage.model.Entry
import com.wbrawner.nanoflux.storage.model.EntryAndFeed
import io.ktor.http.*
import io.ktor.util.network.*
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryRepository @Inject constructor(
    private val apiService: MinifluxApiService,
    private val entryDao: EntryDao,
    private val logger: Timber.Tree
) {
    fun observeRead(): Flow<List<EntryAndFeed>> = entryDao.observeRead()
    fun observeStarred(): Flow<List<EntryAndFeed>> = entryDao.observeStarred()
    fun observeUnread(): Flow<List<EntryAndFeed>> = entryDao.observeUnread()

    fun getCount(): Long = entryDao.getCount()

    suspend fun getAll(fetch: Boolean = false, afterId: Long? = null): List<EntryAndFeed> {
        if (fetch) {
            getEntries { page ->
                apiService.getEntries(
                    order = Entry.Order.PUBLISHED_AT,
                    direction = Entry.SortDirection.DESC,
                    offset = 100L * page,
                    limit = 100,
                    afterEntryId = afterId
                )
            }
        }
        return entryDao.getAll()
    }

    suspend fun getAllUnread(fetch: Boolean = false): List<EntryAndFeed> {
        if (fetch) {
            getEntries { page ->
                apiService.getEntries(
                    offset = 100L * page,
                    limit = 100,
                    status = listOf(Entry.Status.UNREAD)
                )
            }
        }
        return entryDao.getAllUnread()
    }

    suspend fun getEntry(id: Long): Flow<EntryAndFeed> {
        val entry = entryDao.observe(id)
        if (entryDao.get(id) == null) {
            entryDao.insertAll(apiService.getEntry(id))
        }
        return entry
    }

    suspend fun markEntryRead(entry: Entry) {
        entryDao.update(entry.copy(status = Entry.Status.READ))
        retryOnFailure {
            apiService.updateEntries(listOf(entry.id), Entry.Status.READ)
        }
    }

    suspend fun markEntryUnread(entry: Entry) {
        entryDao.update(entry.copy(status = Entry.Status.UNREAD))
        retryOnFailure {
            apiService.updateEntries(listOf(entry.id), Entry.Status.UNREAD)
        }
    }

    suspend fun toggleStar(entry: Entry) {
        entryDao.update(entry.copy(starred = !entry.starred))
        retryOnFailure {
            apiService.toggleEntryBookmark(entry.id)
        }
    }

    suspend fun share(entry: Entry): String? {
        if (entry.shareCode.isNotBlank()) {
            return Uri.parse(apiService.baseUrl)
                .buildUpon()
                .path("/entry/share/${entry.shareCode}")
                .build()
                .toString()
        }

        val response = apiService.shareEntry(entry)
        return response.headers[HttpHeaders.Location]
    }

    suspend fun download(entry: Entry) {
        val response = apiService.fetchOriginalContent(entry)
        entryDao.update(entry.copy(content = response.content))
    }

    fun getLatestId(): Long = entryDao.getLatestId()

    private suspend fun getEntries(request: suspend (page: Int) -> EntryResponse) {
        var page = 0
        var totalPages = 1L
        while (page < totalPages) {
            val response = request(page++)
            entryDao.insertAll(
                response.entries
            )
            totalPages = response.total / 100
        }
    }


    private suspend fun retryOnFailure(request: suspend () -> Any) {
        try {
            request()
        } catch (e: Exception) {
            when (e) {
                is IOException, is UnresolvedAddressException -> {
                    // TODO: Save request to retry later
                    logger.e("Network request failed", e)
                }
                // TODO: Log to Crashlytics or something instead of just crashing
                else -> throw RuntimeException("Unhandled exception marking entry as read", e)
            }
        }
    }
}

enum class EntryStatus {
    UNREAD,
    STARRED,
    HISTORY
}