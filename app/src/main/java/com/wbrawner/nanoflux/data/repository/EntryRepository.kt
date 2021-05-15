package com.wbrawner.nanoflux.data.repository

import com.wbrawner.nanoflux.data.MinifluxApiService
import com.wbrawner.nanoflux.data.dao.EntryDao
import com.wbrawner.nanoflux.data.dao.FeedDao
import com.wbrawner.nanoflux.data.model.Entry
import com.wbrawner.nanoflux.data.model.FeedCategoryIcon
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class EntryRepository @Inject constructor(
    private val apiService: MinifluxApiService,
    private val entryDao: EntryDao,
    private val logger: Timber.Tree
) {
    private val entries: Flow<List<Entry>> = entryDao.getAll()

    fun getAll(): Flow<List<Entry>> {
        GlobalScope.launch {
            entryDao.insertAll(apiService.getEntries())
        }
        return entries
    }
}