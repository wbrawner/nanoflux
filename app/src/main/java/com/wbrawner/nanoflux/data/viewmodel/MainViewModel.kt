package com.wbrawner.nanoflux.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wbrawner.nanoflux.network.repository.CategoryRepository
import com.wbrawner.nanoflux.network.repository.EntryRepository
import com.wbrawner.nanoflux.network.repository.FeedRepository
import com.wbrawner.nanoflux.storage.model.EntryAndFeed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val categoryRepository: CategoryRepository,
    private val entryRepository: EntryRepository,
    private val logger: Timber.Tree
) : ViewModel() {
    private val _state = MutableStateFlow<FeedState>(FeedState.Loading)
    val state = _state.asStateFlow()

    fun loadUnread() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var entries = entryRepository.getAllUnread()
                if (entries.isEmpty()) entries = entryRepository.getAllUnread(true)
                val state = if (entries.isEmpty()) FeedState.Empty else FeedState.Success(entries)
                _state.emit(state)
            } catch (e: Exception) {
                _state.emit(FeedState.Failed(e.localizedMessage))
            }
        }
    }

    sealed class FeedState {
        object Loading : FeedState()
        class Failed(val errorMessage: String?): FeedState()
        object Empty: FeedState()
        class Success(val entries: List<EntryAndFeed>): FeedState()
    }
}