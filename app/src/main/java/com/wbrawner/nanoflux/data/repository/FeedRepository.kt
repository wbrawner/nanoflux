package com.wbrawner.nanoflux.data.repository

import com.wbrawner.nanoflux.data.MinifluxApiService
import com.wbrawner.nanoflux.data.dao.FeedDao
import com.wbrawner.nanoflux.data.model.FeedCategoryIcon
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class FeedRepository @Inject constructor(
    private val apiService: MinifluxApiService,
    private val feedDao: FeedDao,
    private val logger: Timber.Tree
) {
    private val feeds: Flow<List<FeedCategoryIcon>> = feedDao.getAll()

    fun getAll(): Flow<List<FeedCategoryIcon>> {
        GlobalScope.launch {
            feedDao.insertAll(apiService.getFeeds())
        }
        return feeds
    }
}