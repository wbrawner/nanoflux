package com.wbrawner.nanoflux.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wbrawner.nanoflux.network.repository.CategoryRepository
import com.wbrawner.nanoflux.network.repository.EntryRepository
import com.wbrawner.nanoflux.network.repository.FeedRepository
import com.wbrawner.nanoflux.storage.model.Entry
import com.wbrawner.nanoflux.storage.model.EntryAndFeed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UnreadViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val categoryRepository: CategoryRepository,
    private val entryRepository: EntryRepository,
    private val logger: Timber.Tree
) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()
    val entries = entryRepository.observeUnread()

    init {
        logger.v("Unread entryRepo: ${entryRepository}r")
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    // TODO: Get Base URL
    fun getShareUrl(entry: Entry) = "baseUrl/${entry.shareCode}"

    fun refresh() = viewModelScope.launch(Dispatchers.IO) {
        _loading.emit(true)
        feedRepository.getAll(fetch = true)
        categoryRepository.getAll(fetch = true)
        entryRepository.getAll(fetch = true)
        _loading.emit(false)
    }
}