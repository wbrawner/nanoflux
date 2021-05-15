package com.wbrawner.nanoflux

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.wbrawner.nanoflux.data.MinifluxApiService
import com.wbrawner.nanoflux.data.repository.EntryRepository
import com.wbrawner.nanoflux.data.repository.FeedRepository
import com.wbrawner.nanoflux.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(context, workerParams) {
    @Inject
    lateinit var entryRepository: EntryRepository
    @Inject
    lateinit var feedRepository: FeedRepository

    override fun doWork(): Result {
        runBlocking {
            feedRepository.getAll()
            entryRepository.getAll()
        }
        return Result.success()
    }
}
