package com.dune.appcache.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dune.appcache.ui.theme.AccentGreen
import com.dune.appcache.ui.theme.CardWhite
import com.dune.appcache.ui.theme.TextPrimary
import com.dune.appcache.ui.theme.TextSecondary

/**
 * One settings row: icon-in-a-tinted-square, a label, and a trailing control
 * (toggle / value+chevron / chevron / swatch+chevron — pass whatever you need via [trailing]).
 */
@Composable
fun SettingsRow(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CardWhite, RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(iconBackground, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        )
        trailing()
    }
}

@Composable
fun RowToggle(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedTrackColor = AccentGreen,
            checkedThumbColor = CardWhite,
        ),
    )
}

@Composable
fun RowValueChevron(value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(value, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Icon(
            Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.padding(start = 4.dp).size(18.dp),
        )
    }
}

@Composable
fun RowChevron() {
    Icon(
        Icons.Rounded.ChevronRight,
        contentDescription = null,
        tint = TextSecondary,
        modifier = Modifier.size(18.dp),
    )
}

@Composable
fun RowSwatchChevron(color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(color, CircleShape),
        )
        Box(modifier = Modifier.width(8.dp))
        RowChevron()
    }
}
