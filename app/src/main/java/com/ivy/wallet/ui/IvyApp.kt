package com.ivy.wallet.ui

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import com.ivy.design.IvyContext
import com.ivy.design.api.IvyDesign
import com.ivy.design.api.ivyContext
import com.ivy.design.api.systems.IvyWalletDesign
import com.ivy.design.l0_system.Theme
import com.ivy.design.utils.IvyPreview


@Composable
fun IvyAppPreview(
    theme: Theme = Theme.LIGHT,
    Content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    IvyPreview(
        theme = theme,
        design = appDesign(IvyWalletCtx()),
        Content = Content
    )
}

@Composable
fun ivyWalletCtx(): IvyWalletCtx {
    return ivyContext() as IvyWalletCtx
}

fun appDesign(context: IvyWalletCtx): IvyDesign = object : IvyWalletDesign() {
    override fun context(): IvyContext = context
}
