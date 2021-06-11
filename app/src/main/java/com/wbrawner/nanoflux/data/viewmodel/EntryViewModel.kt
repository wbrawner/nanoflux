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
                val entry = entryRepository.getEntry(id)
                _entry.value = entry
                _loading.value = false
                entryRepository.markEntryRead(entry.entry)
            } catch (e: Exception) {
                _errorMessage.value = e.message?: "Error fetching entry"
                _loading.value = false
                logger.e(e)
            }
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    // TODO: Get Base URL
    fun getShareUrl(entry: Entry) = "baseUrl/${entry.shareCode}"
}