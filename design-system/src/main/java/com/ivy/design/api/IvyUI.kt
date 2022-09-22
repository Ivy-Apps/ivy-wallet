package com.ivy.design.api

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ivy.design.Theme
import com.ivy.design.api.systems.IvyWalletDesign
import com.ivy.design.l0_system.IvyTheme

private val appTheme = mutableStateOf(Theme.Auto)
private val appDesign = mutableStateOf<IvyDesign>(IvyWalletDesign())

fun setAppTheme(theme: Theme) {
    appTheme.value = theme
}

fun setAppDesign(design: IvyDesign) {
    appDesign.value = design
}

@Composable
fun IvyUI(
    Content: @Composable (BoxWithConstraintsScope.() -> Unit)
) {
    IvyTheme(
        theme = appTheme.value,
        design = appDesign.value
    ) {
        val systemUiController = rememberSystemUiController()
        val useDarkIcons = MaterialTheme.colors.isLight

        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = useDarkIcons
            )
        }

        Surface(modifier = Modifier.fillMaxSize()) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                Content()
            }
        }
    }
}
