package com.wbrawner.nanoflux.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wbrawner.nanoflux.network.repository.*
import com.wbrawner.nanoflux.storage.model.Entry
import com.wbrawner.nanoflux.storage.model.EntryAndFeed
import com.wbrawner.nanoflux.syncAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class EntryListViewModel(
    private val feedRepository: FeedRepository,
    private val categoryRepository: CategoryRepository,
    private val entryRepository: EntryRepository,
    private val iconRepository: IconRepository,
    private val logger: Timber.Tree,
) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()
    abstract val entries: Flow<List<EntryAndFeed>>

    fun dismissError() {
        _errorMessage.value = null
    }

    suspend fun share(entry: Entry): String? {
        // Miniflux API doesn't currently support sharing so we have to send the external link for now
        // https://github.com/miniflux/v2/issues/844
//        return withContext(Dispatchers.IO) {
//            entryRepository.share(entry)
//        }
        return entry.url
    }

    fun refresh(status: EntryStatus? = null) = viewModelScope.launch(Dispatchers.IO) {
        _loading.emit(true)
        syncAll(categoryRepository, feedRepository, iconRepository, entryRepository)
        _loading.emit(false)
    }

    fun toggleRead(entry: Entry) = viewModelScope.launch(Dispatchers.IO) {
        if (entry.status == Entry.Status.READ) {
            entryRepository.markEntryUnread(entry)
        } else if (entry.status == Entry.Status.UNREAD) {
            entryRepository.markEntryRead(entry)
        }
    }

    fun toggleStar(entry: Entry) = viewModelScope.launch(Dispatchers.IO) {
        entryRepository.toggleStar(entry)
    }
}