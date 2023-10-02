package com.ivy.design.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivy.base.legacy.Theme
import com.ivy.design.IvyContext
import com.ivy.design.api.IvyDesign
import com.ivy.design.api.IvyUI
import com.ivy.design.api.systems.IvyWalletDesign
import com.ivy.design.l0_system.UI

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun IvyComponentPreview(
    design: IvyDesign = defaultDesign(),
    theme: Theme = Theme.LIGHT,
    content: @Composable BoxScope.() -> Unit
) {
    IvyPreview(
        design = design,
        theme = theme
    ) {
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

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun IvyPreview(
    theme: Theme = Theme.LIGHT,
    design: IvyDesign,
    Content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    design.context().switchTheme(theme = theme)
    IvyUI(
        design = design,
        content = Content
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun defaultDesign(): IvyDesign = object : IvyWalletDesign() {
    override fun context(): IvyContext = object : IvyContext() {
    }
}
