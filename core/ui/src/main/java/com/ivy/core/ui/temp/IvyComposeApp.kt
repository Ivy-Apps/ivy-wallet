package com.ivy.core.ui.temp

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

@Composable
fun rootView(): View = LocalView.current

@Composable
fun rootScreen(): RootScreen = LocalContext.current as RootScreen