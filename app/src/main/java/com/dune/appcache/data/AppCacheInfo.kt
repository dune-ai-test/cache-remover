package com.dune.appcache.data

import androidx.compose.ui.graphics.ImageBitmap

data class AppCacheInfo(
    val packageName: String,
    val label: String,
    val cacheBytes: Long,
    val icon: ImageBitmap?,
    val isOwnApp: Boolean,
)

/** Formats bytes the same way the design's "184 MB" / "1.2 GB" labels look. */
fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 MB"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    return when {
        gb >= 1.0 -> String.format("%.1f GB", gb)
        mb >= 1.0 -> String.format("%.1f MB", mb)
        else -> String.format("%.0f KB", kb)
    }
}
