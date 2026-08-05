package com.dune.appcache.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppCacheTheme(
    accentColor: Color = AccentGreen,
    content: @Composable () -> Unit,
) {
    // The design is a light, airy UI — we intentionally don't switch to a dark palette based
    // on the system setting, to keep the card-on-tinted-background look intact.
    val colorScheme = lightColorScheme(
        primary = accentColor,
        onPrimary = CardWhite,
        background = AppBackground,
        onBackground = TextPrimary,
        surface = CardWhite,
        onSurface = TextPrimary,
        error = DangerRed,
        onError = CardWhite,
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
