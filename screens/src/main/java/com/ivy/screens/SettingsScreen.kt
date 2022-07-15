package com.ivy.screens

import com.ivy.frp.view.navigation.Screen

data class SettingsScreen(
    val versionName: String,
    val versionCode: String
) : Screen