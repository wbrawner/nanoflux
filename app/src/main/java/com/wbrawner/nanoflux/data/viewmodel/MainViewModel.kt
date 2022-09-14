package com.wbrawner.nanoflux.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wbrawner.nanoflux.network.repository.CategoryRepository
import com.wbrawner.nanoflux.network.repository.EntryRepository
import com.wbrawner.nanoflux.network.repository.FeedRepository
import com.wbrawner.nanoflux.network.repository.IconRepository
import com.wbrawner.nanoflux.syncAll
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val categoryRepository: CategoryRepository,
    private val iconRepository: IconRepository,
    private val entryRepository: EntryRepository,
    private val logger: Timber.Tree
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (entryRepository.getCount() == 0L) {
                syncAll(categoryRepository, feedRepository, iconRepository, entryRepository)
            }
        }
    }
}