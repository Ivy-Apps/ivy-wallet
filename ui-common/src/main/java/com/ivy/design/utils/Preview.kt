package com.ivy.design.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.ivy.design.IvyContext
import com.ivy.design.api.IvyDesign
import com.ivy.design.api.IvyUI
import com.ivy.design.api.systems.IvyWalletDesign
import com.ivy.design.l0_system.UI


@Composable
fun ComponentPreviewBase(
    design: IvyDesign = defaultDesign(),
    theme: com.ivy.data.Theme = com.ivy.data.Theme.LIGHT,
    content: @Composable BoxScope.() -> Unit
) {
    PreviewBase(
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

@Composable
fun PreviewBase(
    theme: com.ivy.data.Theme = com.ivy.data.Theme.LIGHT,
    design: IvyDesign,
    Content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    design.context().switchTheme(theme = theme)
    IvyUI(
        design = design,
        Content = Content
    )
}

fun defaultDesign(): IvyDesign = object : IvyWalletDesign() {
    override fun context(): IvyContext = object : IvyContext() {
    }
}

@Composable
fun isInPreview(): Boolean = LocalInspectionMode.current