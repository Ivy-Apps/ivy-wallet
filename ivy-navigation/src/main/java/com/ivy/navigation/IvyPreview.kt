package com.ivy.navigation

import androidx.compose.runtime.Composable
import com.ivy.design.system.IvyMaterial3Theme

@Composable
fun IvyPreview(
    dark: Boolean = false,
    content: @Composable () -> Unit,
) {
    NavigationRoot(navigation = Navigation()) {
        IvyMaterial3Theme(dark = dark, content = content)
    }
}