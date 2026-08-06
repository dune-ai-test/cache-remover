package com.dune.appcache.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dune.appcache.data.AppDataUsage
import com.dune.appcache.data.DataUsageRepository
import com.dune.appcache.data.DeviceDataSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DataUsageUiState(
    val isLoading: Boolean = true,
    val hasUsageAccess: Boolean = true,
    val summary: DeviceDataSummary = DeviceDataSummary(0L, 0L),
    val apps: List<AppDataUsage> = emptyList(),
)

class DataUsageViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DataUsageRepository(application)

    private val _uiState = MutableStateFlow(DataUsageUiState())
    val uiState: StateFlow<DataUsageUiState> = _uiState.asStateFlow()

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
            if (!hasAccess) {
                _uiState.value = DataUsageUiState(isLoading = false, hasUsageAccess = false)
                return@launch
            }
            val summary = repository.loadDeviceSummary()
            val apps = repository.loadTopApps()
            _uiState.value = DataUsageUiState(
                isLoading = false,
                hasUsageAccess = true,
                summary = summary,
                apps = apps,
            )
        }
    }
}
