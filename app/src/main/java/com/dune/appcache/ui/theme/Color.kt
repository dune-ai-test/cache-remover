package com.dune.appcache.ui.theme

import androidx.compose.ui.graphics.Color

val AppBackground = Color(0xFFF6F6F8)
val CardWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF141416)
val TextSecondary = Color(0xFF8E8E93)

val AccentGreen = Color(0xFF00C896)
val AccentGreenAlt = Color(0xFF00BFA6)
val AccentGreenBg = Color(0xFFE0F7F0)

val AccentPurple = Color(0xFF6A5CE0)
val AccentPurpleBg = Color(0xFFF0ECFE)
val AccentPurpleBgAlt = Color(0xFFEDEAFE)

val AccentBlue = Color(0xFF4285F4)
val AccentBlueBg = Color(0xFFE8F0FE)

val AccentOrange = Color(0xFFFF9F0A)
val AccentOrangeBg = Color(0xFFFFF4E0)

val DangerRed = Color(0xFFE5484D)
val DangerRedBg = Color(0xFFFCE8E8)

/** Rotating palette used for app icon backgrounds when we don't special-case an app. */
val IconBgPalette = listOf(
    AccentGreenBg to AccentGreenAlt,
    AccentPurpleBg to AccentPurple,
    DangerRedBg to DangerRed,
    AccentBlueBg to AccentBlue,
    AccentOrangeBg to AccentOrange,
    AccentPurpleBgAlt to AccentPurple,
)

/** Preset accent color choices shown in Settings → Accent color. */
val AccentColorPresets = listOf(
    AccentGreen,
    AccentPurple,
    AccentBlue,
    AccentOrange,
    DangerRed,
    Color(0xFF141416),
)
