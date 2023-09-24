package com.ivy.design.system

import androidx.compose.runtime.Composable
import com.ivy.navigation.Navigation
import com.ivy.navigation.NavigationRoot

@Composable
fun IvyPreview(
    dark: Boolean = false,
    content: @Composable () -> Unit,
) {
    NavigationRoot(navigation = Navigation()) {
        IvyMaterial3Theme(dark = dark, content = content)
    }
}