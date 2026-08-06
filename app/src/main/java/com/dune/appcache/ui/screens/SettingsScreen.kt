package com.dune.appcache.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoDelete
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dune.appcache.data.AppRepository
import com.dune.appcache.data.formatBytes
import com.dune.appcache.ui.components.BottomNavBar
import com.dune.appcache.ui.components.BottomTab
import com.dune.appcache.ui.components.RowChevron
import com.dune.appcache.ui.components.RowSwatchChevron
import com.dune.appcache.ui.components.RowToggle
import com.dune.appcache.ui.components.RowValueChevron
import com.dune.appcache.ui.components.SettingsRow
import com.dune.appcache.ui.theme.AccentBlue
import com.dune.appcache.ui.theme.AccentBlueBg
import com.dune.appcache.ui.theme.AccentColorPresets
import com.dune.appcache.ui.theme.AccentGreen
import com.dune.appcache.ui.theme.AccentGreenAlt
import com.dune.appcache.ui.theme.AccentGreenBg
import com.dune.appcache.ui.theme.AccentOrange
import com.dune.appcache.ui.theme.AccentOrangeBg
import com.dune.appcache.ui.theme.AccentPurple
import com.dune.appcache.ui.theme.AccentPurpleBg
import com.dune.appcache.ui.theme.TextPrimary
import com.dune.appcache.ui.theme.TextSecondary
import com.dune.appcache.util.CacheActions

private val scheduleOptions = listOf(6 to "Every 6 hours", 12 to "Every 12 hours", 24 to "Daily", 168 to "Weekly")

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenCache: () -> Unit,
    onOpenDataUsage: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel()
    val settings by viewModel.settings.collectAsState()

    var showScheduleDialog by remember { mutableStateOf(false) }
    var showColorDialog by remember { mutableStateOf(false) }
    var ownCacheLabel by remember { mutableStateOf("…") }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        val apps = AppRepository(context).let { repo -> if (repo.hasUsageAccess()) repo.loadApps() else emptyList() }
        val ownBytes = apps.firstOrNull { it.isOwnApp }?.cacheBytes ?: 0L
        ownCacheLabel = formatBytes(ownBytes)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Fixed header — stays put while the list below scrolls.
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp, 16.dp, 16.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(40.dp).background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ChevronLeft, contentDescription = "Back", tint = TextPrimary)
                    }
                }
                Text(
                    "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f).padding(start = 12.dp),
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 100.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                item { SectionLabel("CLEANUP") }
                item {
                    SettingsRow(
                        label = "Auto-clean on unlock",
                        icon = Icons.Rounded.AutoDelete,
                        iconTint = AccentGreen,
                        iconBackground = AccentGreenBg,
                    ) {
                        RowToggle(checked = settings.autoCleanOnUnlock, onCheckedChange = viewModel::setAutoCleanOnUnlock)
                    }
                }
                item {
                    SettingsRow(
                        label = "Scheduled clean",
                        icon = Icons.Rounded.Schedule,
                        iconTint = AccentBlue,
                        iconBackground = AccentBlueBg,
                        onClick = { showScheduleDialog = true },
                    ) {
                        val current = scheduleOptions.firstOrNull { it.first == settings.scheduledCleanIntervalHours }?.second ?: "Off"
                        RowValueChevron(if (settings.scheduledCleanEnabled) current else "Off")
                    }
                }

                item { SectionLabel("APPEARANCE") }
                item {
                    SettingsRow(
                        label = "Accent color",
                        icon = Icons.Rounded.Palette,
                        iconTint = AccentPurple,
                        iconBackground = AccentPurpleBg,
                        onClick = { showColorDialog = true },
                    ) {
                        RowSwatchChevron(Color(settings.accentColorArgb))
                    }
                }

                item { SectionLabel("SYSTEM") }
                item {
                    SettingsRow(
                        label = "Notifications",
                        icon = Icons.Rounded.Notifications,
                        iconTint = AccentOrange,
                        iconBackground = AccentOrangeBg,
                    ) {
                        RowToggle(checked = settings.notificationsEnabled, onCheckedChange = viewModel::setNotificationsEnabled)
                    }
                }
                item {
                    SettingsRow(
                        label = "Storage info",
                        icon = Icons.Rounded.Storage,
                        iconTint = AccentGreenAlt,
                        iconBackground = AccentGreenBg,
                        onClick = { CacheActions.openStorageSettings(context) },
                    ) {
                        RowValueChevron(ownCacheLabel)
                    }
                }
                item {
                    SettingsRow(
                        label = "About",
                        icon = Icons.Rounded.Info,
                        iconTint = AccentPurple,
                        iconBackground = AccentPurpleBg,
                        onClick = onOpenAbout,
                    ) {
                        RowChevron()
                    }
                }
            }
        }

        BottomNavBar(
            selected = BottomTab.SETTINGS,
            onSelect = { tab ->
                when (tab) {
                    BottomTab.CACHE -> onOpenCache()
                    BottomTab.DATA -> onOpenDataUsage()
                    BottomTab.SETTINGS -> Unit
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
        )
    }

    if (showScheduleDialog) {
        ScheduleDialog(
            currentHours = if (settings.scheduledCleanEnabled) settings.scheduledCleanIntervalHours else null,
            onDismiss = { showScheduleDialog = false },
            onSelect = { hours ->
                showScheduleDialog = false
                if (hours == null) viewModel.setScheduledClean(false, settings.scheduledCleanIntervalHours)
                else viewModel.setScheduledClean(true, hours)
            },
        )
    }

    if (showColorDialog) {
        ColorPickerDialog(
            selected = Color(settings.accentColorArgb),
            onDismiss = { showColorDialog = false },
            onSelect = { color ->
                showColorDialog = false
                viewModel.setAccentColor(color.toArgb())
            },
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
}

@Composable
private fun ScheduleDialog(currentHours: Int?, onDismiss: () -> Unit, onSelect: (Int?) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Scheduled clean") },
        text = {
            Column {
                DialogOptionRow("Off", currentHours == null) { onSelect(null) }
                scheduleOptions.forEach { (hours, label) ->
                    DialogOptionRow(label, currentHours == hours) { onSelect(hours) }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
    )
}

@Composable
private fun ColorPickerDialog(selected: Color, onDismiss: () -> Unit, onSelect: (Color) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Accent color") },
        text = {
            Column {
                AccentColorPresets.forEach { color ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = color == selected, onClick = { onSelect(color) })
                        Box(modifier = Modifier.size(20.dp).background(color, CircleShape))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
    )
}

@Composable
private fun DialogOptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label, color = TextPrimary)
    }
}
