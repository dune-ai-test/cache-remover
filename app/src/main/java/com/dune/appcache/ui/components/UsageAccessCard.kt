package com.dune.appcache.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dune.appcache.ui.theme.AccentGreen
import com.dune.appcache.ui.theme.TextPrimary
import com.dune.appcache.ui.theme.TextSecondary

@Composable
fun UsageAccessCard(explanation: String, onGrant: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Text(
            "Let App Cache see your storage",
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
        )
        Text(
            explanation,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
        )
        Button(
            onClick = onGrant,
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
            shape = RoundedCornerShape(20.dp),
        ) {
            Icon(Icons.Rounded.CleaningServices, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Grant access", color = Color.White)
        }
    }
}
