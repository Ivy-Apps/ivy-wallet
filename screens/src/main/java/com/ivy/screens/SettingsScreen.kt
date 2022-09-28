package com.ivy.screens

import com.ivy.frp.view.navigation.Screen

@Deprecated("will be deleted")
data class SettingsScreen(
    val versionName: String,
    val versionCode: String
) : Screen