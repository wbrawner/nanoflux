package com.wbrawner.nanoflux.network.repository

import com.wbrawner.nanoflux.network.MinifluxApiService
import com.wbrawner.nanoflux.storage.dao.FeedDao
import com.wbrawner.nanoflux.storage.model.FeedCategoryIcon
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
    private val apiService: MinifluxApiService,
    private val feedDao: FeedDao,
    private val iconRepository: IconRepository,
    private val categoryRepository: CategoryRepository,
    private val logger: Timber.Tree
) {
    suspend fun getAll(fetch: Boolean = false): List<FeedCategoryIcon> {
        if (fetch) {
            feedDao.insertAll(apiService.getFeeds().map { it.asFeed() })
        }
        return feedDao.getAll()
    }
}