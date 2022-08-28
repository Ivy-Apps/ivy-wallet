package com.ivy.core.ui.navigation

import com.ivy.frp.view.navigation.Screen

sealed class BackstackItem {
    data class Overlay(val id: String, val onBack: () -> BackstackItemResult) : BackstackItem()
    data class FullScreen(val screen: Screen) : BackstackItem()
}