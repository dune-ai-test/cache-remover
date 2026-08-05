package com.dune.appcache.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dune.appcache.util.CacheActions
import java.util.concurrent.TimeUnit

class ScheduledCleanWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        // Same honest boundary as everywhere else in the app: a background job can only ever
        // clear this app's own cache, not other apps' — those still need the user's tap.
        CacheActions.clearOwnCache(applicationContext)
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "scheduled_cache_clean"

        fun schedule(context: Context, intervalHours: Int) {
            val hours = intervalHours.coerceAtLeast(1).toLong()
            val request = PeriodicWorkRequestBuilder<ScheduledCleanWorker>(hours, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request,
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
