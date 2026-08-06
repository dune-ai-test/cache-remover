package com.dune.appcache.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dune.appcache.data.AppCacheInfo
import com.dune.appcache.data.formatBytes
import com.dune.appcache.ui.components.BottomNavBar
import com.dune.appcache.ui.components.BottomTab
import com.dune.appcache.ui.components.UsageAccessCard
import com.dune.appcache.ui.theme.AccentGreen
import com.dune.appcache.ui.theme.DangerRed
import com.dune.appcache.ui.theme.DangerRedBg
import com.dune.appcache.ui.theme.IconBgPalette
import com.dune.appcache.ui.theme.TextPrimary
import com.dune.appcache.ui.theme.TextSecondary
import com.dune.appcache.util.CacheActions

@Composable
fun HomeScreen(onOpenSettings: () -> Unit, onOpenDataUsage: () -> Unit) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Re-check real cache sizes whenever the user comes back — e.g. after clearing an app's
    // cache from the App Info screen we sent them to, or after granting Usage Access.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var showRemoveAllDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 168.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                HeaderRow(
                    cachedLabel = formatBytes(uiState.totalCacheBytes),
                    appCount = uiState.appCount,
                    onOpenSettings = onOpenSettings,
                )
            }

            if (!uiState.hasUsageAccess) {
                item {
                    UsageAccessCard(
                        explanation = "Android requires a one-time \"Usage access\" permission before any app can read how much cache other apps are using.",
                        onGrant = { CacheActions.openUsageAccessSettings(context) },
                    )
                }
            }

            if (uiState.hasUsageAccess) {
                item {
                    Text(
                        "All apps",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                    )
                }

                if (uiState.isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AccentGreen)
                        }
                    }
                } else if (uiState.apps.isEmpty()) {
                    item {
                        Text(
                            "Nothing cached right now — you're all clean.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 24.dp),
                        )
                    }
                } else {
                    items(uiState.apps, key = { it.packageName }) { app ->
                        AppRow(
                            app = app,
                            onRemove = {
                                if (app.isOwnApp) {
                                    CacheActions.clearOwnCache(context)
                                    viewModel.refresh()
                                } else {
                                    CacheActions.openAppInfo(context, app.packageName)
                                }
                            },
                        )
                    }
                }
            }
        }

        if (uiState.hasUsageAccess && uiState.apps.isNotEmpty()) {
            Button(
                onClick = { showRemoveAllDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = 88.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
            ) {
                Icon(Icons.Rounded.DeleteSweep, contentDescription = null, tint = Color.White)
                Spacer4dp()
                Text("Remove All", color = Color.White, style = MaterialTheme.typography.bodyLarge)
            }
        }

        BottomNavBar(
            selected = BottomTab.CACHE,
            onSelect = { tab ->
                when (tab) {
                    BottomTab.CACHE -> Unit
                    BottomTab.DATA -> onOpenDataUsage()
                    BottomTab.SETTINGS -> onOpenSettings()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
        )
    }

    if (showRemoveAllDialog) {
        RemoveAllDialog(
            onDismiss = { showRemoveAllDialog = false },
            onOpenStorageSettings = {
                showRemoveAllDialog = false
                CacheActions.clearOwnCache(context)
                CacheActions.openStorageSettings(context)
            },
        )
    }
}

@Composable
private fun Spacer4dp() {
    Box(modifier = Modifier.width(8.dp))
}

@Composable
private fun HeaderRow(cachedLabel: String, appCount: Int, onOpenSettings: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text("App Cache", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
            Text(
                "$cachedLabel cached · $appCount apps",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = TextPrimary)
            }
        }
    }
}

@Composable
private fun AppRow(app: AppCacheInfo, onRemove: () -> Unit) {
    val paletteIndex = remember(app.packageName) { (app.packageName.hashCode().mod(IconBgPalette.size)) }
    val (bg, tint) = IconBgPalette[paletteIndex]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(bg, RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (app.icon != null) {
                Image(
                    painter = BitmapPainter(app.icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            } else {
                Text(app.label.take(1).uppercase(), color = tint, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(app.label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, maxLines = 1)
            Text(formatBytes(app.cacheBytes), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        }

        Row(
            modifier = Modifier
                .background(DangerRedBg, RoundedCornerShape(15.dp))
                .clickable(onClick = onRemove)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Delete, contentDescription = null, tint = DangerRed, modifier = Modifier.size(14.dp))
            Box(modifier = Modifier.width(6.dp))
            Text(
                if (app.isOwnApp) "Clear" else "Remove",
                color = DangerRed,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
private fun RemoveAllDialog(onDismiss: () -> Unit, onOpenStorageSettings: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Clear all cache") },
        text = {
            Text(
                "For your safety, Android requires confirming each app's cache clear " +
                    "individually — this app can't silently wipe other apps' data. We've cleared " +
                    "App Cache's own cache, and can take you to your device's storage manager to " +
                    "clear the rest in bulk, or you can tap Remove on each app in the list."
            )
        },
        confirmButton = {
            Button(onClick = onOpenStorageSettings, colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)) {
                Text("Open Storage Settings", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                Text("Cancel", color = TextSecondary)
            }
        },
    )
}
