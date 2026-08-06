package com.dune.appcache.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dune.appcache.data.AppDataUsage
import com.dune.appcache.data.formatBytes
import com.dune.appcache.ui.components.BottomNavBar
import com.dune.appcache.ui.components.BottomTab
import com.dune.appcache.ui.components.UsageAccessCard
import com.dune.appcache.ui.theme.AccentBlue
import com.dune.appcache.ui.theme.AccentGreen
import com.dune.appcache.ui.theme.IconBgPalette
import com.dune.appcache.ui.theme.TextPrimary
import com.dune.appcache.ui.theme.TextSecondary
import com.dune.appcache.util.CacheActions

@Composable
fun DataUsageScreen(onOpenSettings: () -> Unit, onOpenCache: () -> Unit) {
    val context = LocalContext.current
    val viewModel: DataUsageViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Re-check after the user comes back from granting Usage Access.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column {
                    Text("Data Usage", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                    Text(
                        "Last 30 days · Wi-Fi + mobile",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }
            }

            if (!uiState.hasUsageAccess) {
                item {
                    UsageAccessCard(
                        explanation = "Android requires the same one-time \"Usage access\" permission before any app can read how much data other apps have used.",
                        onGrant = { CacheActions.openUsageAccessSettings(context) },
                    )
                }
            } else {
                item {
                    SummaryCard(
                        totalLabel = formatBytes(uiState.summary.totalBytes),
                        wifiLabel = formatBytes(uiState.summary.wifiBytes),
                        mobileLabel = formatBytes(uiState.summary.mobileBytes),
                    )
                }

                item {
                    Text(
                        "Top apps by data",
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
                            "No app data usage recorded in the last 30 days yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 24.dp),
                        )
                    }
                } else {
                    items(uiState.apps, key = { it.packageName }) { app ->
                        AppDataRow(app)
                    }
                }
            }
        }

        BottomNavBar(
            selected = BottomTab.DATA,
            onSelect = { tab ->
                when (tab) {
                    BottomTab.CACHE -> onOpenCache()
                    BottomTab.DATA -> Unit
                    BottomTab.SETTINGS -> onOpenSettings()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
        )
    }
}

@Composable
private fun SummaryCard(totalLabel: String, wifiLabel: String, mobileLabel: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(22.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Total this month", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Text(
                totalLabel,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp),
                color = TextPrimary,
            )
        }
        Column(horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Wifi, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(15.dp))
                Box(modifier = Modifier.width(6.dp))
                Text(wifiLabel, color = AccentBlue, style = MaterialTheme.typography.bodyMedium)
            }
            Box(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.SignalCellularAlt, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(15.dp))
                Box(modifier = Modifier.width(6.dp))
                Text(mobileLabel, color = AccentGreen, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun AppDataRow(app: AppDataUsage) {
    val paletteIndex = remember(app.packageName) { app.packageName.hashCode().mod(IconBgPalette.size) }
    val (bg, tint) = IconBgPalette[paletteIndex]

    val total = app.totalBytes.coerceAtLeast(1L)
    val wifiFraction = (app.wifiBytes.toFloat() / total.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(10.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(bg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (app.icon != null) {
                    Image(
                        painter = BitmapPainter(app.icon),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                } else {
                    Text(app.label.take(1).uppercase(), color = tint, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Column(modifier = Modifier.weight(1f).padding(start = 12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(app.label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, maxLines = 1)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Wifi, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(13.dp))
                    Box(modifier = Modifier.width(3.dp))
                    Text(formatBytes(app.wifiBytes), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                    Box(modifier = Modifier.width(8.dp))
                    Icon(Icons.Rounded.SignalCellularAlt, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(13.dp))
                    Box(modifier = Modifier.width(3.dp))
                    Text(formatBytes(app.mobileBytes), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                }
            }

            Text(
                formatBytes(app.totalBytes),
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
            )
        }

        Box(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color(0xFFECECF0), RoundedCornerShape(3.dp)),
        ) {
            Box(
                modifier = Modifier
                    .weight(wifiFraction.coerceAtLeast(0.001f))
                    .fillMaxHeight()
                    .background(AccentBlue),
            )
            Box(
                modifier = Modifier
                    .weight((1f - wifiFraction).coerceAtLeast(0.001f))
                    .fillMaxHeight()
                    .background(AccentGreen),
            )
        }
    }
}
