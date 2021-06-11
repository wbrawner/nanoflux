package com.wbrawner.nanoflux

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.wbrawner.nanoflux.network.repository.CategoryRepository
import com.wbrawner.nanoflux.network.repository.EntryRepository
import com.wbrawner.nanoflux.network.repository.FeedRepository
import com.wbrawner.nanoflux.network.repository.IconRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(context, workerParams) {
    @Inject
    lateinit var categoryRepository: CategoryRepository

    @Inject
    lateinit var entryRepository: EntryRepository

    @Inject
    lateinit var feedRepository: FeedRepository

    @Inject
    lateinit var iconRepository: IconRepository

    override fun doWork(): Result {
        return runBlocking {
            Timber.v("Unread entryRepo: ${entryRepository}r")
            try {
                syncAll(categoryRepository, feedRepository, iconRepository, entryRepository)
                Result.success()
            } catch (e: Exception) {
               Result.failure(Data.Builder().putString("message", e.message?: "Unknown failure").build())
            }
        }
    }
}
