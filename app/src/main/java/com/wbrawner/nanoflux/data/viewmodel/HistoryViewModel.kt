package com.wbrawner.nanoflux.data.viewmodel

import com.wbrawner.nanoflux.network.repository.CategoryRepository
import com.wbrawner.nanoflux.network.repository.EntryRepository
import com.wbrawner.nanoflux.network.repository.FeedRepository
import com.wbrawner.nanoflux.network.repository.IconRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
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
    override val entries = entryRepository.observeRead()
}