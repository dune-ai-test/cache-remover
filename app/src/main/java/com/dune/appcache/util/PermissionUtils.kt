package com.dune.appcache.util

import android.app.AppOpsManager
import android.content.Context
import android.os.Build
import android.os.Process

object PermissionUtils {
    /**
     * Both per-app cache sizes (StorageStatsManager) and per-app data usage
     * (NetworkStatsManager) are gated behind the same special "Usage access" permission,
     * which the user grants once from Settings — it's not a runtime dialog.
     */
    fun hasUsageAccess(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName,
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName,
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
}
