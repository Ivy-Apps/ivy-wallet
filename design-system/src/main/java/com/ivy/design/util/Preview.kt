package com.ivy.design.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.ivy.data.Theme
import com.ivy.design.api.IvyDesign
import com.ivy.design.api.IvyUI
import com.ivy.design.api.setAppDesign
import com.ivy.design.api.systems.ivyWalletDesign
import com.ivy.design.l0_system.UI


@Composable
fun ComponentPreview(
    design: IvyDesign = ivyWalletDesign(theme = Theme.Auto, isSystemInDarkTheme = false),
    content: @Composable BoxScope.() -> Unit
) {
    IvyPreview(design = design) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.pure),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun IvyPreview(
    design: IvyDesign = ivyWalletDesign(theme = Theme.Auto, isSystemInDarkTheme = false),
    Content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    SideEffect {
        setAppDesign(design)
    }
    IvyUI(
        Content = Content
    )
}

@Composable
inline fun <reified VM : ViewModel> hiltViewModelPreviewSafe(
    key: String? = null,
): VM? =
    if (isInPreview()) null else hiltViewModel(key = key)

@Composable
fun isInPreview(): Boolean = LocalInspectionMode.current