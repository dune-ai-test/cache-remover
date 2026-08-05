package com.dune.appcache.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "app_cache_settings")

data class AppSettings(
    val autoCleanOnUnlock: Boolean = false,
    val scheduledCleanEnabled: Boolean = false,
    val scheduledCleanIntervalHours: Int = 24,
    val notificationsEnabled: Boolean = true,
    val accentColorArgb: Int = 0xFF00C896.toInt(),
)

class SettingsRepository(private val context: Context) {

    private object Keys {
        val AUTO_CLEAN = booleanPreferencesKey("auto_clean_on_unlock")
        val SCHEDULED_ENABLED = booleanPreferencesKey("scheduled_clean_enabled")
        val SCHEDULED_HOURS = intPreferencesKey("scheduled_clean_hours")
        val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val ACCENT_COLOR = intPreferencesKey("accent_color_argb")
        // kept for forward-compatibility if we ever store a named preset instead of raw argb
        val ACCENT_NAME = stringPreferencesKey("accent_color_name")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            autoCleanOnUnlock = prefs[Keys.AUTO_CLEAN] ?: false,
            scheduledCleanEnabled = prefs[Keys.SCHEDULED_ENABLED] ?: false,
            scheduledCleanIntervalHours = prefs[Keys.SCHEDULED_HOURS] ?: 24,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: true,
            accentColorArgb = prefs[Keys.ACCENT_COLOR] ?: 0xFF00C896.toInt(),
        )
    }

    suspend fun setAutoCleanOnUnlock(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_CLEAN] = enabled }
    }

    suspend fun setScheduledClean(enabled: Boolean, hours: Int = 24) {
        context.dataStore.edit {
            it[Keys.SCHEDULED_ENABLED] = enabled
            it[Keys.SCHEDULED_HOURS] = hours
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS] = enabled }
    }

    suspend fun setAccentColor(argb: Int) {
        context.dataStore.edit { it[Keys.ACCENT_COLOR] = argb }
    }
}
