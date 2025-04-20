package com.scritch.app.settings

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getAppVersionWithBuildNumber(): String {
    val context = LocalContext.current
    return try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionName = pInfo.versionName ?: "?"
        val versionCode = if (android.os.Build.VERSION.SDK_INT >= 28) {
            pInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            pInfo.versionCode.toLong()
        }
        "Android $versionName ($versionCode)"
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}