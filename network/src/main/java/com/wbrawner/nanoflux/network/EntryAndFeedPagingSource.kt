package com.wbrawner.nanoflux.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wbrawner.nanoflux.network.repository.EntryRepository
import com.wbrawner.nanoflux.network.repository.EntryStatus
import com.wbrawner.nanoflux.storage.model.EntryAndFeed
import timber.log.Timber

class EntryAndFeedPagingSource(
    private val entryRepository: EntryRepository,
    private val entryStatus: EntryStatus? = null
) : PagingSource<Int, EntryAndFeed>() {
    init {
        Timber.tag("Nanoflux").d("EntryAndFeedPagingSource created")
    }

    override fun getRefreshKey(state: PagingState<Int, EntryAndFeed>): Int? = state.anchorPosition
        ?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EntryAndFeed> {
        return try {
            val nextPageNumber = params.key ?: 1
            val offset = params.loadSize * (nextPageNumber - 1)
            val nextKey = if (offset + params.loadSize < entryRepository.count()) {
                nextPageNumber + 1
            } else {
                null
            }
            val loadFunction = when (entryStatus) {
                EntryStatus.UNREAD -> entryRepository::loadUnread
                EntryStatus.HISTORY -> entryRepository::loadRead
                EntryStatus.STARRED -> entryRepository::loadStarred
                else -> entryRepository::load
            }
            LoadResult.Page(
                data = loadFunction(params.loadSize, offset),
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Timber.e("Failed to load page", e)
            LoadResult.Error(e)
        }
    }
}