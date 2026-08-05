package com.dune.appcache.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object CacheActions {

    /** The one cache we're actually allowed to delete without any extra permission. */
    fun clearOwnCache(context: Context) {
        context.cacheDir?.deleteRecursively()
        context.externalCacheDir?.deleteRecursively()
    }

    /**
     * For every other app, Android requires the user to confirm the deletion themselves —
     * we can only bring them straight to the right screen and the right button.
     */
    fun openAppInfo(context: Context, packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openUsageAccessSettings(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openStorageSettings(context: Context) {
        val intent = Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            context.startActivity(
                Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}
