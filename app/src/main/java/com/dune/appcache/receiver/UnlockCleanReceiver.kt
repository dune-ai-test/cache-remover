package com.dune.appcache.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dune.appcache.data.SettingsRepository
import com.dune.appcache.util.CacheActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UnlockCleanReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_USER_PRESENT) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = SettingsRepository(context.applicationContext).settings.first()
                if (settings.autoCleanOnUnlock) {
                    CacheActions.clearOwnCache(context.applicationContext)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
