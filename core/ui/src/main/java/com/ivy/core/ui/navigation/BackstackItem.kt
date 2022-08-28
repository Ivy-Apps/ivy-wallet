package com.ivy.core.ui.navigation

import com.ivy.frp.view.navigation.Screen

sealed class BackstackItem {
    data class Overlay(val id: String, val onBack: () -> BackResult) : BackstackItem()
    data class FullScreen(val screen: Screen) : BackstackItem()
}