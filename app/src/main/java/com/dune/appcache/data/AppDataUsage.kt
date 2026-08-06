package com.dune.appcache.data

import androidx.compose.ui.graphics.ImageBitmap

data class AppDataUsage(
    val packageName: String,
    val label: String,
    val icon: ImageBitmap?,
    val wifiBytes: Long,
    val mobileBytes: Long,
) {
    val totalBytes: Long get() = wifiBytes + mobileBytes
}

data class DeviceDataSummary(
    val wifiBytes: Long,
    val mobileBytes: Long,
) {
    val totalBytes: Long get() = wifiBytes + mobileBytes
}
