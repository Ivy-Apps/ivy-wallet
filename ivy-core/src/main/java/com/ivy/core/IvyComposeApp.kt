package com.ivy.core

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import com.ivy.design.IvyContext
import com.ivy.design.api.IvyDesign
import com.ivy.design.api.ivyContext
import com.ivy.design.api.systems.IvyWalletDesign
import com.ivy.design.l0_system.Theme
import com.ivy.design.l0_system.UI

@Composable
fun ivyWalletCtx(): IvyWalletCtx = ivyContext() as IvyWalletCtx

@Composable
fun rootView(): View = LocalView.current

fun appDesign(context: IvyWalletCtx): IvyDesign = object : IvyWalletDesign() {
    override fun context(): IvyContext = context
}