package com.wbrawner.nanoflux

import com.wbrawner.nanoflux.network.repository.CategoryRepository
import com.wbrawner.nanoflux.network.repository.EntryRepository
import com.wbrawner.nanoflux.network.repository.FeedRepository
import com.wbrawner.nanoflux.network.repository.IconRepository

suspend fun syncAll(
    categoryRepository: CategoryRepository,
    feedRepository: FeedRepository,
    iconRepository: IconRepository,
    entryRepository: EntryRepository
) {
    // The order of operations is important here to prevent the DAOs from returning incomplete
    // objects. E.g. The EntryDao returns an EntryAndFeed, which in turn contains a FeedCategoryIcon
    categoryRepository.getAll(true)
    feedRepository.getAll(true).forEach {
        if (it.feed.iconId != null) {
            iconRepository.getFeedIcon(it.feed.id)
        }
    }
    entryRepository.fetch()
}