package com.scritch.app.settings

import androidx.compose.runtime.Composable
import platform.Foundation.NSBundle

@Composable
actual fun getAppVersionWithBuildNumber(): String {
    val infoDict = NSBundle.mainBundle.infoDictionary
    val versionName = infoDict?.get("CFBundleShortVersionString") as? String ?: "?"
    val buildNumber = infoDict?.get("CFBundleVersion") as? String ?: "?"
    return "iOS $versionName ($buildNumber)"
}