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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val categoryRepository: CategoryRepository,
    private val entryRepository: EntryRepository,
    private val logger: Timber.Tree
) : ViewModel() {
    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()
    private val _entry = MutableStateFlow<EntryAndFeed?>(null)
    val entry = _entry.asStateFlow()

    suspend fun loadEntry(id: Long) {
        withContext(Dispatchers.IO) {
            _loading.value = true
            _errorMessage.value = null
            try {
                var markedRead = false
                entryRepository.observeEntry(id).collect {
                    _entry.value = it
                    _loading.value = false
                    if (!markedRead) {
                        entryRepository.markEntryRead(it.entry)
                        markedRead = true
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error fetching entry"
                _loading.value = false
                logger.e(e)
            }
        }
    }

    suspend fun share(entry: Entry): String? {
        // Miniflux API doesn't currently support sharing so we have to send the external link for now
        // https://github.com/miniflux/v2/issues/844
//        return withContext(Dispatchers.IO) {
//            entryRepository.share(entry)
//        }
        return entry.url
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

    fun downloadOriginalContent(entry: Entry) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                entryRepository.download(entry)
            } catch (e: Exception) {
                _errorMessage.emit("Failed to fetch original content: ${e.message}")
            }
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }
}