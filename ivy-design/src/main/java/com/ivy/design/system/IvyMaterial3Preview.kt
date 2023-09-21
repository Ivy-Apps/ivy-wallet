package com.ivy.design.system

import androidx.compose.runtime.Composable

@Composable
fun IvyPreview(
    dark: Boolean = false,
    content: @Composable () -> Unit,
) {
    IvyMaterial3Theme(dark = dark, content = content)
}