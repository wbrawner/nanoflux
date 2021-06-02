package com.wbrawner.nanoflux.network.repository

import com.wbrawner.nanoflux.network.MinifluxApiService
import com.wbrawner.nanoflux.storage.dao.EntryDao
import com.wbrawner.nanoflux.storage.model.Entry
import com.wbrawner.nanoflux.storage.model.EntryAndFeed
import timber.log.Timber
import javax.inject.Inject

class EntryRepository @Inject constructor(
    private val apiService: MinifluxApiService,
    private val entryDao: EntryDao,
    private val logger: Timber.Tree
) {
    suspend fun getAll(fetch: Boolean = false): List<EntryAndFeed> {
        if (fetch) {
            var page = 0
            while (true) {
                try {
                    entryDao.insertAll(apiService.getEntries(offset = 1000L * page++, limit = 1000).entries)
                } catch (e: Exception) {
                    break
                }
            }
        }
        return entryDao.getAll()
    }

    suspend fun getAllUnread(fetch: Boolean = false): List<EntryAndFeed> {
        if (fetch) {
            var page = 0
            while (true) {
//                try {
                    val response = apiService.getEntries(
                        offset = 10000000000L,// * page++,
                        limit = 1000,
                        status = listOf(Entry.Status.UNREAD)
                    )
                    entryDao.insertAll(
                        response.entries
                    )
//                } catch (e: Exception) {
//                    Log.e("EntryRepository", "Error", e)
//                    break
//                }
            }
        }
        return entryDao.getAllUnread()
    }
}