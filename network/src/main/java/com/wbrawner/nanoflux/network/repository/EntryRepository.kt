package com.wbrawner.nanoflux.network.repository

import com.wbrawner.nanoflux.network.EntryResponse
import com.wbrawner.nanoflux.network.MinifluxApiService
import com.wbrawner.nanoflux.storage.dao.EntryDao
import com.wbrawner.nanoflux.storage.model.Entry
import com.wbrawner.nanoflux.storage.model.EntryAndFeed
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class EntryRepository @Inject constructor(
    private val apiService: MinifluxApiService,
    private val entryDao: EntryDao,
    private val logger: Timber.Tree
) {
    fun observeUnread(): Flow<List<EntryAndFeed>> = entryDao.observeUnread()

    fun getCount(): Long = entryDao.getCount()

    suspend fun getAll(fetch: Boolean = false, afterId: Long? = null): List<EntryAndFeed> {
        if (fetch) {
            getEntries { page ->
                apiService.getEntries(
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

    suspend fun getEntry(id: Long): EntryAndFeed {
        entryDao.getAllByIds(id).firstOrNull()?.let {
            return@getEntry it
        }

        entryDao.insertAll(apiService.getEntry(id))
        return entryDao.getAllByIds(id).first()
    }

    suspend fun markEntryRead(entry: Entry) {
        apiService.updateEntries(listOf(entry.id), Entry.Status.READ)
        entryDao.update(entry.copy(status = Entry.Status.READ))
    }

    suspend fun markEntryUnread(entry: Entry) {
        apiService.updateEntries(listOf(entry.id), Entry.Status.UNREAD)
        entryDao.update(entry.copy(status = Entry.Status.UNREAD))
    }

    fun getLatestId(): Long = entryDao.getLatestId()

    private suspend fun getEntries(request: suspend (page: Int) -> EntryResponse) {
        var page = 0
        var totalPages = 1L
        while (page++ < totalPages) {
            val response = request(page)
            entryDao.insertAll(
                response.entries
            )
            totalPages = response.total / 100
        }
    }
}

enum class EntryStatus {
    UNREAD,
    STARRED,
    HISTORY
}