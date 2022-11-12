package com.wbrawner.nanoflux.data.viewmodel

import com.wbrawner.nanoflux.network.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StarredViewModel @Inject constructor(
    feedRepository: FeedRepository,
    categoryRepository: CategoryRepository,
    entryRepository: EntryRepository,
    iconRepository: IconRepository,
    logger: Timber.Tree,
) : EntryListViewModel(
    feedRepository,
    categoryRepository,
    entryRepository,
    iconRepository,
    logger
) {
    override val entryStatus: EntryStatus = EntryStatus.STARRED
}