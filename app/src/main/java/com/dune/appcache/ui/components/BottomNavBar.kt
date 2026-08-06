package com.dune.appcache.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.DataUsage
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dune.appcache.ui.theme.AccentGreen
import com.dune.appcache.ui.theme.AccentGreenBg
import com.dune.appcache.ui.theme.TextSecondary

enum class BottomTab(val route: String, val label: String, val icon: ImageVector) {
    CACHE("home", "Cache", Icons.Rounded.CleaningServices),
    DATA("data", "Data", Icons.Rounded.DataUsage),
    SETTINGS("settings", "Settings", Icons.Rounded.Settings),
}

@Composable
fun BottomNavBar(
    selected: BottomTab,
    onSelect: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(Color(0xF2FFFFFF), RoundedCornerShape(28.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BottomTab.entries.forEach { tab ->
            val isSelected = tab == selected
            Column(
                modifier = Modifier
                    .clickable { onSelect(tab) }
                    .background(if (isSelected) AccentGreenBg else Color.Transparent, RoundedCornerShape(20.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    tab.icon,
                    contentDescription = tab.label,
                    tint = if (isSelected) AccentGreen else TextSecondary,
                    modifier = Modifier,
                )
                Text(
                    tab.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) AccentGreen else TextSecondary,
                )
            }
        }
    }
}
