package com.dune.appcache.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dune.appcache.data.AppCacheInfo
import com.dune.appcache.data.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val hasUsageAccess: Boolean = true,
    val apps: List<AppCacheInfo> = emptyList(),
) {
    val totalCacheBytes: Long get() = apps.sumOf { it.cacheBytes }
    val appCount: Int get() = apps.size
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            // Only show the full-screen spinner on the very first load — once we have data,
            // a refresh should swap it in quietly instead of flashing the list away.
            val alreadyHasData = _uiState.value.apps.isNotEmpty()
            if (!alreadyHasData) _uiState.value = _uiState.value.copy(isLoading = true)
            val hasAccess = repository.hasUsageAccess()
            val apps = if (hasAccess) repository.loadApps() else emptyList()
            _uiState.value = HomeUiState(
                isLoading = false,
                hasUsageAccess = hasAccess,
                apps = apps,
            )
        }
    }

    /** Optimistically drop an app from the list once the user has cleared it themselves. */
    fun markCleared(packageName: String) {
        _uiState.value = _uiState.value.copy(
            apps = _uiState.value.apps.filterNot { it.packageName == packageName },
        )
    }
}
