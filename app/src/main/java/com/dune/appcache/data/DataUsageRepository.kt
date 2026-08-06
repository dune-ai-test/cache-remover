package com.dune.appcache.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.dune.appcache.util.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class DataUsageRepository(private val context: Context) {

    fun hasUsageAccess(): Boolean = PermissionUtils.hasUsageAccess(context)

    private val statsManager: NetworkStatsManager
        get() = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

    private val windowStart: Long
        get() = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)

    private val windowEnd: Long
        get() = System.currentTimeMillis()

    /** Device-wide totals for the summary card at the top of the screen. */
    suspend fun loadDeviceSummary(): DeviceDataSummary = withContext(Dispatchers.IO) {
        DeviceDataSummary(
            wifiBytes = queryDeviceTotal(ConnectivityManager.TYPE_WIFI),
            mobileBytes = queryDeviceTotal(ConnectivityManager.TYPE_MOBILE),
        )
    }

    /** Per-app breakdown, sorted by total bytes used, largest first. */
    suspend fun loadTopApps(limit: Int = 25): List<AppDataUsage> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val installed: List<ApplicationInfo> = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        installed
            .filter { it.enabled }
            .mapNotNull { appInfo ->
                val isUserApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
                val hasLauncherEntry = pm.getLaunchIntentForPackage(appInfo.packageName) != null
                if (!isUserApp && !hasLauncherEntry) return@mapNotNull null

                val wifi = queryUidTotal(ConnectivityManager.TYPE_WIFI, appInfo.uid)
                val mobile = queryUidTotal(ConnectivityManager.TYPE_MOBILE, appInfo.uid)
                if (wifi <= 0L && mobile <= 0L) return@mapNotNull null

                val label = try {
                    pm.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    appInfo.packageName
                }

                val icon: Drawable? = try {
                    pm.getApplicationIcon(appInfo)
                } catch (e: Exception) {
                    null
                }

                AppDataUsage(
                    packageName = appInfo.packageName,
                    label = label,
                    icon = icon?.let { d ->
                        try {
                            (if (d is BitmapDrawable) d.bitmap else d.toBitmap(96, 96)).asImageBitmap()
                        } catch (e: Exception) {
                            null
                        }
                    },
                    wifiBytes = wifi,
                    mobileBytes = mobile,
                )
            }
            .sortedByDescending { it.totalBytes }
            .take(limit)
    }

    private fun queryDeviceTotal(networkType: Int): Long {
        if (!hasUsageAccess()) return 0L
        return try {
            val bucket = statsManager.querySummaryForDevice(networkType, subscriberIdFor(networkType), windowStart, windowEnd)
            bucket.rxBytes + bucket.txBytes
        } catch (e: Exception) {
            0L
        }
    }

    private fun queryUidTotal(networkType: Int, uid: Int): Long {
        if (!hasUsageAccess()) return 0L
        return try {
            val stats = statsManager.queryDetailsForUid(networkType, subscriberIdFor(networkType), windowStart, windowEnd, uid)
            var total = 0L
            val bucket = NetworkStats.Bucket()
            while (stats.hasNextBucket()) {
                stats.getNextBucket(bucket)
                total += bucket.rxBytes + bucket.txBytes
            }
            stats.close()
            total
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * WiFi ignores the subscriber id entirely. For mobile, Android Q+ lets a caller that
     * already holds Usage Access pass an empty string to match every subscriber, without
     * needing the separate READ_PHONE_STATE permission. On pre-Q devices this can fail —
     * we catch that above and simply show 0 for mobile data on those older versions.
     */
    private fun subscriberIdFor(networkType: Int): String? =
        if (networkType == ConnectivityManager.TYPE_MOBILE) "" else null
}
