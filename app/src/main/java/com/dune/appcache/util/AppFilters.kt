package com.dune.appcache.util

import android.content.pm.ApplicationInfo

object AppFilters {
    /**
     * True for genuine system components (Settings, SystemUI, telephony stack, etc.) that a
     * user never installed and shouldn't see in a cleaner app. Apps that started as
     * preinstalled but have since been updated through the Play Store (Chrome, Gmail, Maps —
     * flagged FLAG_UPDATED_SYSTEM_APP) are treated as normal user apps, since people do
     * actively use and want to manage those.
     */
    fun isSystemOnly(appInfo: ApplicationInfo): Boolean {
        val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        val wasUpdated = (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
        return isSystem && !wasUpdated
    }
}
