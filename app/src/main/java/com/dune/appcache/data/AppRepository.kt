package com.dune.appcache.data

import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Process
import android.os.storage.StorageManager
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.dune.appcache.util.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {

    /**
     * Android only lets us query another app's cache size if the user has explicitly granted
     * the "Usage access" special permission (it's not a normal runtime permission dialog).
     */
    fun hasUsageAccess(): Boolean = PermissionUtils.hasUsageAccess(context)

    suspend fun loadApps(): List<AppCacheInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val statsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
        val uuid = StorageManager.UUID_DEFAULT

        val installed: List<ApplicationInfo> = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val canReadStats = hasUsageAccess()

        installed
            .filter { it.enabled }
            .mapNotNull { appInfo ->
                // Skip apps with no launchable UI and no real cache footprint (system services etc.)
                val isUserApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
                val hasLauncherEntry = pm.getLaunchIntentForPackage(appInfo.packageName) != null
                if (!isUserApp && !hasLauncherEntry) return@mapNotNull null

                val label = try {
                    pm.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    appInfo.packageName
                }

                val cacheBytes: Long = if (canReadStats) {
                    try {
                        val stats = statsManager.queryStatsForPackage(
                            uuid,
                            appInfo.packageName,
                            Process.myUserHandle(),
                        )
                        stats.cacheBytes
                    } catch (e: Exception) {
                        0L
                    }
                } else 0L

                if (cacheBytes <= 0L && appInfo.packageName != context.packageName) {
                    return@mapNotNull null
                }

                val icon: Drawable? = try {
                    pm.getApplicationIcon(appInfo)
                } catch (e: Exception) {
                    null
                }

                AppCacheInfo(
                    packageName = appInfo.packageName,
                    label = label,
                    cacheBytes = cacheBytes,
                    icon = icon?.let { d ->
                        try {
                            (if (d is BitmapDrawable) d.bitmap else d.toBitmap(96, 96)).asImageBitmap()
                        } catch (e: Exception) {
                            null
                        }
                    },
                    isOwnApp = appInfo.packageName == context.packageName,
                )
            }
            .sortedByDescending { it.cacheBytes }
    }
}
