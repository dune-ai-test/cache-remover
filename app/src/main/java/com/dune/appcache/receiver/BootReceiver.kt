package com.dune.appcache.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dune.appcache.data.SettingsRepository
import com.dune.appcache.worker.ScheduledCleanWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = SettingsRepository(context.applicationContext).settings.first()
                if (settings.scheduledCleanEnabled) {
                    ScheduledCleanWorker.schedule(context.applicationContext, settings.scheduledCleanIntervalHours)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
