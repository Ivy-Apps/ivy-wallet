package com.ivy.base

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.ivy.design.IvyContext
import com.ivy.design.api.IvyDesign
import com.ivy.design.api.ivyContext
import com.ivy.design.api.systems.IvyWalletDesign
import com.ivy.data.Theme
import com.ivy.design.l0_system.UI
import com.ivy.design.utils.IvyPreview
import com.ivy.frp.view.navigation.Navigation
import com.ivy.frp.view.navigation.NavigationRoot


@Composable
fun ivyWalletCtx(): IvyWalletCtx = ivyContext() as IvyWalletCtx

@Composable
fun rootView(): View = LocalView.current

@Composable
fun rootScreen(): RootScreen = LocalContext.current as RootScreen

fun appDesign(context: IvyWalletCtx): IvyDesign = object : IvyWalletDesign() {
    override fun context(): IvyContext = context
}

@Composable
fun IvyWalletComponentPreview(
    theme: Theme = Theme.LIGHT,
    Content: @Composable BoxScope.() -> Unit
) {
    IvyWalletPreview(
        theme = theme
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.pure),
            contentAlignment = Alignment.Center
        ) {
            Content()
        }
    }
}

@Composable
fun IvyWalletPreview(
    theme: Theme = Theme.LIGHT,
    Content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    GlobalProvider.appContext = rootView().context
    IvyPreview(
        theme = theme,
        design = appDesign(IvyWalletCtx()),
    ) {
        NavigationRoot(navigation = Navigation()) {
            Content()
        }
    }
}
