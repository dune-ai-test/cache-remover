package com.dune.appcache

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import com.dune.appcache.receiver.UnlockCleanReceiver

class AppCacheApplication : Application() {

    private val unlockReceiver = UnlockCleanReceiver()

    override fun onCreate() {
        super.onCreate()

        // ACTION_USER_PRESENT (screen unlock) is an implicit broadcast that Android will not
        // deliver to a manifest-declared receiver on API 26+, so we register it dynamically
        // here instead. UnlockCleanReceiver checks the "Auto-clean on unlock" setting itself
        // before doing anything. This only fires while our process is resident in memory —
        // there's no reliable way to catch every unlock from a killed process without a
        // foreground service, which we deliberately avoid for a minimal cache app.
        registerReceiver(unlockReceiver, IntentFilter(Intent.ACTION_USER_PRESENT))
    }
}
