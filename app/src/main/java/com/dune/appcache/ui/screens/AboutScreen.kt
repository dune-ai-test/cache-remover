package com.dune.appcache.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dune.appcache.ui.theme.TextPrimary
import com.dune.appcache.ui.theme.TextSecondary

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ChevronLeft, contentDescription = "Back", tint = TextPrimary)
                }
            }
            Text(
                "About",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                modifier = Modifier.weight(1f).padding(start = 12.dp),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp),
        ) {
            Text("App Cache", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
            Text("Version 1.0", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

            Text(
                "How cache clearing actually works",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                modifier = Modifier.padding(top = 20.dp, bottom = 6.dp),
            )
            Text(
                "Since Android 6.0, apps run in their own sandbox and no app — including this " +
                    "one — can silently delete another app's cache without root access. That's a " +
                    "deliberate security boundary, not a bug.\n\n" +
                    "What App Cache actually does:\n" +
                    "• Reads real cache sizes for every app, once you grant Usage Access.\n" +
                    "• Clears its own cache directly, on demand or on a schedule.\n" +
                    "• For any other app, opens that app's system Info screen to the exact " +
                    "spot where a single tap clears its cache — Android requires that " +
                    "confirmation come from you, not from another app.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }
    }
}
