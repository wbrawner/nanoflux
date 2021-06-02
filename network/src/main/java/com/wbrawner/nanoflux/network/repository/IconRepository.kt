package com.wbrawner.nanoflux.network.repository

import com.wbrawner.nanoflux.network.MinifluxApiService
import com.wbrawner.nanoflux.storage.dao.IconDao
import com.wbrawner.nanoflux.storage.model.Feed
import timber.log.Timber
import javax.inject.Inject

class IconRepository @Inject constructor(
    private val apiService: MinifluxApiService,
    private val iconDao: IconDao,
    private val logger: Timber.Tree
) {
    suspend fun getIcon(id: Long, feedId: Long? = null): Feed.Icon? =
        iconDao.getAllByIds(id).firstOrNull() ?: feedId?.let { feed ->
            apiService.getFeedIcon(feed).also { icon ->
                iconDao.insertAll(icon)
            }
        }

    suspend fun getFeedIcon(feedId: Long): Feed.Icon? = try {
        apiService.getFeedIcon(feedId).also { icon ->
            iconDao.insertAll(icon)
        }
    } catch (e: Exception) {
        // TODO: Check if network exception or 404
        null
    }
}

