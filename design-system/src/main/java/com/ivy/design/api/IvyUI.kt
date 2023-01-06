package com.ivy.design.api

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ivy.data.Theme
import com.ivy.design.api.systems.ivyWalletDesign
import com.ivy.design.l0_system.IvyTheme
import com.ivy.design.l0_system.UI

private val appDesign = mutableStateOf(
    ivyWalletDesign(theme = Theme.Auto, isSystemInDarkTheme = false)
)

fun setAppDesign(design: IvyDesign) {
    appDesign.value = design
}

@Composable
fun IvyUI(
    Content: @Composable (BoxWithConstraintsScope.() -> Unit)
) {
    IvyTheme(design = appDesign.value) {
        val systemUiController = rememberSystemUiController()
        val useDarkIcons = UI.colors.isLight

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
