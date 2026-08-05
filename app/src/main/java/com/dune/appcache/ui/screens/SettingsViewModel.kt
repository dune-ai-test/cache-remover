package com.dune.appcache.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dune.appcache.data.AppSettings
import com.dune.appcache.data.SettingsRepository
import com.dune.appcache.worker.ScheduledCleanWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)

    val settings: StateFlow<AppSettings> = repository.settings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AppSettings(),
    )

    fun setAutoCleanOnUnlock(enabled: Boolean) {
        viewModelScope.launch { repository.setAutoCleanOnUnlock(enabled) }
    }

    fun setScheduledClean(enabled: Boolean, hours: Int) {
        viewModelScope.launch {
            repository.setScheduledClean(enabled, hours)
            val app = getApplication<Application>()
            if (enabled) {
                ScheduledCleanWorker.schedule(app, hours)
            } else {
                ScheduledCleanWorker.cancel(app)
            }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setNotificationsEnabled(enabled) }
    }

    fun setAccentColor(argb: Int) {
        viewModelScope.launch { repository.setAccentColor(argb) }
    }
}
